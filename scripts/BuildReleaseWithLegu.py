# coding=utf-8

import base64
import shlex
import subprocess
import time
import random
import hmac
from hashlib import sha1git

try:
    from urllib import request
except ImportError:
    import urllib.request
try:
    from urllib import urlencode
except ImportError:
    from urllib.parse import urlencode

from urllib.parse import urlencode

try:
    from urllib.parse import urlparse
except ImportError:
    from urlparse import urlparse

import ssl
import json
import hashlib
import base64
import binascii
import hashlib
import sys
import requests
from boto3.session import Session

LOCAL_FILE_PATH = 'app/build/outputs/apk/release/DDMobileApp-release.apk'
APK_URL = "提供给legu下载的apk链接，这里用的是aws"
SECRET_ID = "Legu SECRET_ID"
SECRET_KEY = "Legu SECRET_KEY"
HTTP_BASE_URL = "https://ms.tencentcloudapi.com/"
APK_DOWN_TARGET = "Shield.apk"

# AWS prop：文件上传的aws的配置信息
AWS_KEY = "AWS_KEY"
AWS_SECRET = "AWS_SECRET"
BUCKET_NAME = "BUCKET_NAME"
AWS_PATH = "cms/Androidapk/"


def error_exit(message):
    print(message)
    sys.exit(1)


def bash(cmd):
    p = subprocess.Popen(shlex.split(cmd), stdout=subprocess.PIPE)
    out = ""
    while p.poll() is None:
        out = p.stdout.readline().strip()
        print(out)
    if p.returncode != 0:
        error_exit(cmd + " error")
    return out


def md5():
    with open(LOCAL_FILE_PATH, 'rb') as fp:
        data = fp.read()
    file_md5 = hashlib.md5(data).hexdigest()
    print("md5:" + file_md5)
    return file_md5


# todo: return
def upload_apk_to_aws():
    session = Session(aws_access_key_id=AWS_KEY, aws_secret_access_key=AWS_SECRET,
                      region_name="cn-north-1")
    s3 = session.resource("s3")
    upload_key = AWS_PATH + "DDMobileApp-release.apk"
    try:
        file_obj = s3.Bucket(BUCKET_NAME).put_object(
            ACL="public-read",
            Key=upload_key,
            Body=open(LOCAL_FILE_PATH, "rb")
        )
    except Exception as e:
        error_exit("upload_apk_to_aws error:{}".format(e))
        return


def https_get(params):
    context = ssl._create_unverified_context()
    url = HTTP_BASE_URL + "?" + urlencode(params)
    print("Http get:" + url)
    response = request.urlopen(url, context=context)
    resp_str = response.read().decode('utf-8')
    print(resp_str)
    return json.loads(resp_str)


def get_base_query_params(action):
    query_params = {
        "SecretId": SECRET_ID,
        "Version": "2018-04-08",
        "Action": action,
        "Timestamp": int(time.time()),
        "Nonce": random.randint(1, sys.maxsize),
        "Region": "",
        "Language": "zh-CN"
    }
    return query_params


def query_result_params(itemId):
    query_params = get_base_query_params("DescribeShieldResult")
    query_params["ItemId"] = itemId
    return query_params


def create_shield_params():
    query_params = get_base_query_params("CreateShieldInstance")
    query_params["ServiceInfo.ServiceEdition"] = "basic"
    query_params["ServiceInfo.SubmitSource"] = "api"
    query_params["AppInfo.AppUrl"] = APK_URL
    query_params["AppInfo.AppMd5"] = md5()
    query_params["ServiceInfo.CallbackUrl"] = ""
    return query_params


def singe(params):
    sorted_params = sorted(params)
    sign_str = "GETms.tencentcloudapi.com/?"
    for item in sorted_params:
        sign_str += "{}={}&".format(item, params[item])
    sign_str = sign_str[:-1]
    print("string to signe:" + sign_str)
    secret_key = SECRET_KEY
    if sys.version_info[0] > 2:
        sign_str = bytes(sign_str, "utf-8")
        secret_key = bytes(secret_key, "utf-8")
    hashed = hmac.new(secret_key, sign_str, hashlib.sha1)
    signature = binascii.b2a_base64(hashed.digest())[:-1]
    if sys.version_info[0] > 2:
        signature = signature.decode()
    return signature


def create_shield():
    print("start create shield...")
    params = create_shield_params()
    sined = singe(params)
    print("sined str:" + sined)
    params["Signature"] = sined
    return https_get(params)


def query_shield_result(item_id):
    params = query_result_params(item_id)
    sined = singe(params)
    print("query_shield_result sined:" + sined)
    params["Signature"] = sined
    return https_get(params)


# TaskStatus 任务状态: 0-请返回,1-已完成,2-处理中,3-处理出错,4-处理超时
def shield_progress(resp):
    return resp["Response"]["TaskStatus"]


def get_shield_result(status):
    if status == 0:
        return "请返回"
    elif status == 1:
        return "已完成"
    elif status == 2:
        return "处理中"
    elif status == 3:
        return "处理出错"
    elif status == 4:
        return "处理超时"
    else:
        return "未知状态"


def download_file(file_url):
    print("start download from {}".format(file_url))
    try:
        r = requests.get(file_url)
        with open(APK_DOWN_TARGET, "wb") as code:
            code.write(r.content)
    except Exception as e:
        error_exit("download error:{}".format(e))
        return
    print("download complete")


def assemble_release():
    print("start assembleRelease...")
    bash("./../../gradlew clean")
    bash("./../../gradlew assembleRelease")
    print("finish assembleRelease")


def query_download_url(item_id):
    shield_result = query_shield_result(item_id)
    status = shield_progress(shield_result)
    while status == 2:
        time.sleep(60)
        shield_result = query_shield_result(item_id)
        status = shield_progress(shield_result)

    print("create shield complete, result:" + get_shield_result(status))
    if status != 1:
        error_exit("create shield fail")
        return
    try:
        return shield_result["Response"]["ShieldInfo"]["AppUrl"]
    except Exception as e:
        error_exit("APPUrl not found:{}".format(e))
        return


def main(build_release):
    if build_release != "not_build_release":
        assemble_release()

    upload_apk_to_aws()

    shield_resp = create_shield()
    has_item_id = "ItemId" in shield_resp["Response"]
    if not has_item_id:
        error_exit("error: ItemId not found")
        return
    item_id = shield_resp["Response"]["ItemId"]
    time.sleep(10)

    apk_download_url = query_download_url(item_id)
    download_file(apk_download_url)

    bash("rm -rf ../../scripts/daodao/builds")
    bash("mkdir ../../scripts/daodao/builds")

    # 运行渠道包脚本
    bash("./../../scripts/daodao/build_release.sh {} {}".format(APK_DOWN_TARGET, "major"))
    bash("./../../scripts/daodao/build_release.sh {} {}".format(APK_DOWN_TARGET, "minor"))


if __name__ == '__main__':
    main(sys.argv[1])

