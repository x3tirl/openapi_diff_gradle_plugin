package com.x3t.gradle.plugins.openapi


import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File

open class OpenapiDiffPluginExtension(project: Project) {
    private val objects = project.objects

    val failOnChange : Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    val failOnIncompatible : Property<Boolean>  = objects.property(Boolean::class.java).convention(false)
    val htmlReport : Property<Boolean>  = objects.property(Boolean::class.java).convention(false)
    val jsonReport : Property<Boolean>  = objects.property(Boolean::class.java).convention(false)
    val textReport : Property<Boolean>  = objects.property(Boolean::class.java).convention(false)
    val markdownReport : Property<Boolean>  = objects.property(Boolean::class.java).convention(false)
    val asciidocReport : Property<Boolean>  = objects.property(Boolean::class.java).convention(false)

    val originalFile : Property<String> = objects.property(String::class.java)
    val newFile : Property<String> = objects.property(String::class.java)

    val reportName : Property<String> = objects.property(String::class.java).convention(
        project.layout.buildDirectory.get().toString() + File.separator + "Openapi_Diff_Report"
    )


    init {
        applyDefaults()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun applyDefaults() {
        failOnChange.set(false)
        failOnIncompatible.set(false)
        htmlReport.set(false)
        jsonReport.set(false)
        textReport.set(false)
        markdownReport.set(false)
        asciidocReport.set(false)
    }
}