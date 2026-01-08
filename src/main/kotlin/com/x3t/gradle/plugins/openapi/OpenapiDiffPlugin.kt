package com.x3t.gradle.plugins.openapi

import org.gradle.api.Plugin
import org.gradle.api.Project

class OpenapiDiffPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val diffExtension = project.extensions.create("openapi_diff", OpenapiDiffPluginExtension::class.java, project)
        project.tasks.register("openapi_diff", OpenapiDiffPluginTask::class.java) {
            it.failOnChange.set(diffExtension.failOnChange)
            it.failOnIncompatible.set(diffExtension.failOnIncompatible)
            it.htmlReport.set(diffExtension.htmlReport)
            it.jsonReport.set(diffExtension.jsonReport)
            it.textReport.set(diffExtension.textReport)
            it.markdownReport.set(diffExtension.markdownReport)
            it.asciidocReport.set(diffExtension.asciidocReport)
            it.reportName.set(diffExtension.reportName)
            it.originalFile.set(diffExtension.originalFile)
            it.newFile.set(diffExtension.newFile)
            it.buildDirectory.set(project.layout.buildDirectory)
            if (!diffExtension.reportName.isPresent) {
                it.reportName.set(
                    project.layout.buildDirectory.get().toString() + java.io.File.separator + "Openapi_Diff_Report"
                )
            }

            it.description = "Compare 2 OpenAPI Specification files to see if there any differences."
            it.group = "OpenAPI Diff"
        }


    }
}