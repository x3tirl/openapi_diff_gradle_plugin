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

    val reportName : Property<String> = objects.property(String::class.java)


    init {
    }
}