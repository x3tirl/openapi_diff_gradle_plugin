package com.x3t.gradle.plugins.openapi

import io.swagger.v3.parser.core.models.AuthorizationValue
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import org.openapitools.openapidiff.core.OpenApiCompare
import org.openapitools.openapidiff.core.output.ConsoleRender
import org.openapitools.openapidiff.core.output.HtmlRender
import org.openapitools.openapidiff.core.output.JsonRender
import org.openapitools.openapidiff.core.output.MarkdownRender
import org.openapitools.openapidiff.core.output.AsciidocRender
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import javax.inject.Inject


abstract class OpenapiDiffPluginTask @Inject constructor() : DefaultTask() {

    @get:Optional
    @get:Input
    abstract val failOnChange: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val failOnIncompatible: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val htmlReport: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val jsonReport: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val textReport: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val markdownReport: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val asciidocReport: Property<Boolean>

    @get:Optional
    @get:Input
    abstract val reportName: Property<String>

    @get:Internal
    abstract val buildDirectory: DirectoryProperty

    @get:InputFile
    @get:Option(option = "originalFile", description = "The original OpenAPI specification file")
    abstract val originalFile: RegularFileProperty

    @get:InputFile
    @get:Option(option = "newFile", description = "The new OpenAPI specification file")
    abstract val newFile: RegularFileProperty

    @get:OutputFiles
    abstract val outputFiles: ConfigurableFileCollection

    @Suppress("unused")
    @TaskAction
    fun doWork() {
        val auths: List<AuthorizationValue>? = null
        //val result = OpenApiCompare.fromLocations(originalFile.get(), newFile.get(), auths)
        val result = OpenApiCompare.fromFiles(originalFile.asFile.get(), newFile.asFile.get(), auths)

        val outputFile: String = if (reportName.isPresent) {
            reportName.get().split(".")[0]
        } else {
            buildDirectory.get().asFile.toString() + File.separator + "Openapi_Diff_Report"
        }

        buildDirectory.get().asFile.mkdirs()
        // Collect all generated report files
        val outputFiles = mutableSetOf<File>()

        if (htmlReport.isPresent and htmlReport.get()) {
            val localOutputFile = "%s.html".format(outputFile)
            logger.debug("HtmlFile - Report Name: $localOutputFile")

            val output_file = File(localOutputFile).absoluteFile
            outputFiles.add(output_file)

            val htmlRender = HtmlRender()
            val outputStream = FileOutputStream(localOutputFile)
            val outputStreamWriter = OutputStreamWriter(outputStream)
            htmlRender.render(result, outputStreamWriter)
        }

        if (jsonReport.isPresent and jsonReport.get()) {
            val localOutputFile = "%s.json".format(outputFile)
            logger.debug("JsonFile - Report Name: $localOutputFile")

            val output_file = File(localOutputFile).absoluteFile
            outputFiles.add(output_file)

            val jsonRender = JsonRender()
            val outputStream = FileOutputStream(localOutputFile)
            val outputStreamWriter = OutputStreamWriter(outputStream)
            jsonRender.render(result, outputStreamWriter)
        }

        if (textReport.isPresent and textReport.get()) {
            val localOutputFile = "%s.txt".format(outputFile)
            logger.debug("PlaintextFile - Report Name: $localOutputFile")

            val output_file = File(localOutputFile).absoluteFile
            outputFiles.add(output_file)

            val consoleRender = ConsoleRender()
            val outputStream = FileOutputStream(localOutputFile)
            val outputStreamWriter = OutputStreamWriter(outputStream)
            consoleRender.render(result, outputStreamWriter)
        }

        if (markdownReport.isPresent and markdownReport.get()) {
            val localOutputFile = "%s.md".format(outputFile)
            logger.debug("MarkdownFile - Report Name: $localOutputFile")

            val output_file = File(localOutputFile).absoluteFile
            outputFiles.add(output_file)

            val mdRender = MarkdownRender()
            val outputStream = FileOutputStream(localOutputFile)
            val outputStreamWriter = OutputStreamWriter(outputStream)
            mdRender.render(result, outputStreamWriter)
        }

        if (asciidocReport.isPresent and asciidocReport.get()) {
            val localOutputFile = "%s.adoc".format(outputFile)
            logger.debug("AsciiDocFile - Report Name: $localOutputFile")

            val output_file = File(localOutputFile).absoluteFile
            outputFiles.add(output_file)

            val asciidocRender = AsciidocRender()
            val outputStream = FileOutputStream(localOutputFile)
            val outputStreamWriter = OutputStreamWriter(outputStream)
            asciidocRender.render(result, outputStreamWriter)
        }

        this.outputFiles.plus(outputFiles)

        if (failOnChange.isPresent and failOnChange.get() and !result.isUnchanged) {
            throw GradleException("The specifications do not match and the build settings state to fail if any change is detected.")
        }

        if (failOnIncompatible.isPresent and failOnIncompatible.get() and !result.isCompatible) {
            throw GradleException("The specifications do not match and the build settings specify to fail if changes break compatibility.")
        }
    }


    init {
        htmlReport.convention(false)
        jsonReport.convention(false)
        textReport.convention(false)
        markdownReport.convention(false)
        asciidocReport.convention(false)
        failOnIncompatible.convention(false)
        failOnChange.convention(false)
        // Set default buildDirectory if not already set (for tasks created directly)
        if (!buildDirectory.isPresent) {
            buildDirectory.convention(project.layout.buildDirectory)
        }
    }
}
