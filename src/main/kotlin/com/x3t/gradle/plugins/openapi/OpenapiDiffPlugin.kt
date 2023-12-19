package com.x3t.gradle.plugins.openapi

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class OpenapiDiffPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val diffExtention = project.extensions.create("openapi_diff", OpenapiDiffPluginExtention::class.java, project)
        project.tasks.create("openapi_diff", OpenapiDiffPluginTask::class.java) {
            it.failOnChange.set(diffExtention.failOnChange)
            it.failOnIncompatible.set(diffExtention.failOnIncompatible)
            it.htmlReport.set(diffExtention.htmlReport)
            it.jsonReport.set(diffExtention.jsonReport)
            it.textReport.set(diffExtention.textReport)
            it.markdownReport.set(diffExtention.markdownReport)
            it.asciidocReport.set(diffExtention.asciidocReport)
            it.reportName.set(diffExtention.reportName)
            it.originalFile.set(diffExtention.originalFile)
            it.newFile.set(diffExtention.newFile)


            it.description = "Compare 2 OpenAPI Specification files to see if there any differences."
            it.group = "OpenAPI Diff"
        }


    }
}