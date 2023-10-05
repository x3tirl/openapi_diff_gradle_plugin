package com.x3t.gradle.plugins.openapi

import io.swagger.v3.parser.core.models.AuthorizationValue
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.api.tasks.options.Option
import org.gradle.util.GradleVersion
import org.openapitools.openapidiff.core.OpenApiCompare
import org.openapitools.openapidiff.core.output.ConsoleRender
import org.openapitools.openapidiff.core.output.HtmlRender
import org.openapitools.openapidiff.core.output.JsonRender
import org.openapitools.openapidiff.core.output.MarkdownRender
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import javax.inject.Inject


abstract class OpenapiDiffPluginTask @Inject constructor(private val objectFactory: ObjectFactory) : DefaultTask() {

    @get:Optional
    @get:Input
    abstract val failOnChange : Property<Boolean>

    @get:Optional
    @get:Input
    abstract val failOnIncompatible : Property<Boolean>

    @get:Optional
    @get:Input
    abstract val htmlReport : Property<Boolean>

    @get:Optional
    @get:Input
    abstract val jsonReport : Property<Boolean>

    @get:Optional
    @get:Input
    abstract val textReport : Property<Boolean>

    @get:Optional
    @get:Input
    abstract val markdownReport : Property<Boolean>

    @get:Optional
    @get:Input
    abstract val reportName : Property<String>

    @get:Input
    @get:Option(option = "originalFile", description = "The original OpenAPI specification file")
    abstract val originalFile : Property<String>

    @get:Input
    @get:Option(option = "newFile", description = "The new OpenAPI specification file")
    abstract val newFile : Property<String>


    private fun <T : Any?> Property<T>.ifNotEmpty(block: Property<T>.(T) -> Unit) {
        if (isPresent) {
            val item: T? = get()
            if (item != null) {
                when (get()) {
                    is String -> if ((get() as String).isNotEmpty()) {
                        block(get())
                    }
                    is String? -> if (true == (get() as String?)?.isNotEmpty()) {
                        block(get())
                    }
                    else -> block(get())
                }
            }
        }
    }

    private fun createFileSystemManager(): FileSystemManager {
        return if(GradleVersion.current() >= GradleVersion.version("6.0")) {
            objectFactory.newInstance(FileSystemManagerDefault::class.java)
        } else {
            objectFactory.newInstance(FileSystemManagerLegacy::class.java, project)
        }
    }

    internal interface FileSystemManager {
        fun delete(outputDir: Property<String>)
    }

    internal open class FileSystemManagerLegacy @Inject constructor(private val project: Project): FileSystemManager {
        override fun delete(outputDir: Property<String>) {
            project.delete(outputDir)
        }
    }

    internal open class FileSystemManagerDefault @Inject constructor(private val fs: FileSystemOperations) : FileSystemManager {
        override fun delete(outputDir: Property<String>) {
            fs.delete { delete(outputDir) }
        }
    }

    @Suppress("unused")
    @TaskAction
    fun doWork() {
        val auths: List<AuthorizationValue>? = null
        val result = OpenApiCompare.fromLocations(originalFile.get(), newFile.get(), auths)
        val outputFile: String

        if(reportName.isPresent) {
            outputFile = reportName.get().split(".")[0]
        } else {
            outputFile = project.layout.buildDirectory.get().toString() + File.separator + "Openapi_Diff_Report"
        }

        project.mkdir(project.layout.buildDirectory.get())

        if( htmlReport.isPresent and htmlReport.get()) {
            val localOutputFile = "%s.html".format(outputFile)
            logger.debug("HtmlFile - Report Name: $localOutputFile")
            val htmlRender = HtmlRender()
            val outputStream = FileOutputStream(localOutputFile)
            val outputStreamWriter = OutputStreamWriter(outputStream)
            htmlRender.render(result, outputStreamWriter)
        }

        if( jsonReport.isPresent and jsonReport.get()) {
            val localOutputFile = "%s.json".format(outputFile)
            logger.debug("JsonFile - Report Name: $localOutputFile")
            val jsonRender = JsonRender()
            val outputStream = FileOutputStream(localOutputFile)
            val outputStreamWriter = OutputStreamWriter(outputStream)
            jsonRender.render(result, outputStreamWriter)
        }

        if( textReport.isPresent and textReport.get()) {
            val localOutputFile = "%s.txt".format(outputFile)
            logger.debug("PlaintextFile - Report Name: $localOutputFile")
            val consoleRender = ConsoleRender()
            val outputStream = FileOutputStream(localOutputFile)
            val outputStreamWriter = OutputStreamWriter(outputStream)
            consoleRender.render(result, outputStreamWriter)
        }

        if( markdownReport.isPresent and markdownReport.get()) {
            val localOutputFile = "%s.md".format(outputFile)
            logger.debug("PlaintextFile - Report Name: $localOutputFile")
            val mdRender = MarkdownRender()
            val outputStream = FileOutputStream(localOutputFile)
            val outputStreamWriter = OutputStreamWriter(outputStream)
            mdRender.render(result, outputStreamWriter)
        }

        if(failOnChange.isPresent and failOnChange.get() and !result.isUnchanged){
            throw GradleException("The specifications do not match and the build settings state to fail if any change is detected.")
        }

        if(failOnIncompatible.isPresent and failOnChange.get() and !result.isCompatible){
            throw GradleException("The specifications do not match and the build settings specify to fail if changes break compatability.")
        }
    }

    init {
        htmlReport.set(false)
        jsonReport.set(false)
        textReport.set(false)
        markdownReport.set(false)
        failOnIncompatible.set(false)
        failOnChange.set(false)
        reportName.set(project.layout.buildDirectory.get().toString() + File.separator + "Openapi_Diff_Report")

    }
}