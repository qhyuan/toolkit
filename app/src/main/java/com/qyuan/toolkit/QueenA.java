package com.qyuan.toolkit;

import android.provider.ContactsContract;
import android.util.Pair;

import java.util.*;

/**
 * Created by qyuan on 2020-01-01.
 */
public class QueenA {
    public static void main(String[] args) {
        PriorityQueue<Map.Entry<Integer, Integer>> queue = new PriorityQueue<>(
                11,
                new Comparator<Map.Entry<Integer, Integer>>() {
                    @Override
                    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                        return 0;
                    }
                }
        );
        HashMap<Integer,Integer> map = new HashMap();
        Integer value = map.get(1) ;
        map.put(1,(value !=null ? value : 0) +1);
        ArrayList list = new ArrayList();
        Collections.reverse(list);
    }
}
