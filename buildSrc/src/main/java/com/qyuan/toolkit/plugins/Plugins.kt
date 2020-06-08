package com.qyuan.toolkit.plugins

import com.qyuan.toolkit.plugins.legu.LeguExtensions
import org.gradle.api.Plugin
import org.gradle.api.Project

class Plugins : Plugin<Project>{
    override fun apply(project: Project) {
        project.extensions.create("legu", LeguExtensions::class.java)
        println("hello plugin")
    }
}
