package com.x3t.gradle.plugins.openapi

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import kotlin.test.Test


/**
 * A simple functional test for the 'test.greeting' plugin.
 */
class TestPluginFunctionalTest {

    @field:TempDir
    lateinit var projectDir: File

    private val buildFile by lazy { projectDir.resolve("build.gradle") }
    private val settingsFile by lazy { projectDir.resolve("settings.gradle") }
    private val testFile1 by lazy { projectDir.resolve("missing_1.yaml") }
    private val testFile2 by lazy { projectDir.resolve("missing_2.yaml") }

    private val missingFile1 :InputStream = javaClass.getClassLoader().getResourceAsStream("missing_1.yaml")
    private val missingFile2 :InputStream = javaClass.getClassLoader().getResourceAsStream("missing_2.yaml")

    @Test fun canRunTaskUsingExtension() {
        // Set up the test build
        settingsFile.writeText("")
        buildFile.writeText("""
            plugins {
                id('com.x3t.gradle.plugins.openapi.openapi_diff')
            }
            
            openapi_diff {
                originalFile = "${projectDir}/missing_1.yaml"
                newFile = "${projectDir}/missing_2.yaml"
            }
        """.trimIndent())

        Files.copy(missingFile1,testFile1.toPath())
        Files.copy(missingFile2,testFile2.toPath())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("openapi_diff")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.toString().isNotEmpty())
    }

    @Test fun canRunStandaloneTask() {
        // Set up the test build
        settingsFile.writeText("")
        buildFile.writeText("""
            plugins {
                id 'com.x3t.gradle.plugins.openapi.openapi_diff'
            }
            
            task triggerCompatible(type: com.x3t.gradle.plugins.openapi.OpenapiDiffPluginTask){
                originalFile.set("${projectDir}/missing_1.yaml")
                newFile.set("${projectDir}/missing_2.yaml")
                failOnIncompatible.set(true)
            }
        """.trimIndent())

        Files.copy(missingFile1,testFile1.toPath())
        Files.copy(missingFile2,testFile2.toPath())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("triggerCompatible")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.toString().isNotEmpty())
    }

    @Test fun canRunFailOnChange() {
        // Set up the test build
        settingsFile.writeText("")
        buildFile.writeText("""
            plugins {
                id 'com.x3t.gradle.plugins.openapi.openapi_diff'
            }
            
            task triggerCompatible(type: com.x3t.gradle.plugins.openapi.OpenapiDiffPluginTask){
                originalFile.set("${projectDir}/missing_1.yaml")
                newFile.set("${projectDir}/missing_2.yaml")
                failOnChange.set(true)
            }
        """.trimIndent())

        Files.copy(missingFile1,testFile1.toPath())
        Files.copy(missingFile2,testFile2.toPath())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("triggerCompatible")
        runner.withProjectDir(projectDir)

        val exception: Exception = assertThrows(UnexpectedBuildFailure::class.java) { runner.build() }
        assertTrue(exception.toString().contains("The specifications do not match and the build settings state to fail if any change is detected."))
    }

    @Test fun canCreateTextReport() {
        // Set up the test build
        settingsFile.writeText("")
        buildFile.writeText("""
            plugins {
                id('com.x3t.gradle.plugins.openapi.openapi_diff')
            }
            
            openapi_diff {
                originalFile = "${projectDir}/missing_1.yaml"
                newFile = "${projectDir}/missing_2.yaml"
                textReport.set(true)
            }
        """.trimIndent())

        Files.copy(missingFile1,testFile1.toPath())
        Files.copy(missingFile2,testFile2.toPath())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("openapi_diff")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.toString().isNotEmpty())
        val reportPath = projectDir.resolve("build/Openapi_Diff_Report.txt")
        val data = reportPath.readText()

        assertTrue(Files.exists(reportPath.toPath()))
        assertTrue(data.contains("API changes are backward compatible"))
    }

    @Test fun canCreateHtmlReport() {
        // Set up the test build
        settingsFile.writeText("")
        buildFile.writeText("""
            plugins {
                id('com.x3t.gradle.plugins.openapi.openapi_diff')
            }
            
            openapi_diff {
                originalFile = "${projectDir}/missing_1.yaml"
                newFile = "${projectDir}/missing_2.yaml"
                htmlReport.set(true)
            }
        """.trimIndent())

        Files.copy(missingFile1,testFile1.toPath())
        Files.copy(missingFile2,testFile2.toPath())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("openapi_diff")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.toString().isNotEmpty())
        val reportPath = projectDir.resolve("build/Openapi_Diff_Report.html")
        val data = reportPath.readText()

        assertTrue(Files.exists(reportPath.toPath()))
        assertTrue(data.contains("<title>Api Change Log</title>"))
    }

    @Test fun canCreateJsonReport() {
        // Set up the test build
        settingsFile.writeText("")
        buildFile.writeText("""
            plugins {
                id('com.x3t.gradle.plugins.openapi.openapi_diff')
            }
            
            openapi_diff {
                originalFile = "${projectDir}/missing_1.yaml"
                newFile = "${projectDir}/missing_2.yaml"
                jsonReport.set(true)
            }
        """.trimIndent())

        Files.copy(missingFile1,testFile1.toPath())
        Files.copy(missingFile2,testFile2.toPath())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("openapi_diff")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.toString().isNotEmpty())
        val reportPath = projectDir.resolve("build/Openapi_Diff_Report.json")
        val data = reportPath.readText()

        assertTrue(Files.exists(reportPath.toPath()))
        assertTrue(data.contains("\"compatible\":true"))
    }

    @Test fun canCreateMarkdownReport() {
        // Set up the test build
        settingsFile.writeText("")
        buildFile.writeText("""
            plugins {
                id('com.x3t.gradle.plugins.openapi.openapi_diff')
            }
            
            openapi_diff {
                originalFile = "${projectDir}/missing_1.yaml"
                newFile = "${projectDir}/missing_2.yaml"
                markdownReport.set(true)
            }
        """.trimIndent())

        Files.copy(missingFile1,testFile1.toPath())
        Files.copy(missingFile2,testFile2.toPath())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("openapi_diff")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.toString().isNotEmpty())
        val reportPath = projectDir.resolve("build/Openapi_Diff_Report.md")
        val data = reportPath.readText()

        assertTrue(Files.exists(reportPath.toPath()))
        assertTrue(data.contains("* Deleted property `childProperty` (object)"))
    }

    @Test fun canCreateAsciidocReport() {
        // Set up the test build
        settingsFile.writeText("")
        buildFile.writeText("""
            plugins {
                id('com.x3t.gradle.plugins.openapi.openapi_diff')
            }
            
            openapi_diff {
                originalFile = "${projectDir}/missing_1.yaml"
                newFile = "${projectDir}/missing_2.yaml"
                asciidocReport.set(true)
            }
        """.trimIndent())

        Files.copy(missingFile1,testFile1.toPath())
        Files.copy(missingFile2,testFile2.toPath())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("openapi_diff")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.toString().isNotEmpty())
        val reportPath = projectDir.resolve("build/Openapi_Diff_Report.adoc")
        val data = reportPath.readText()

        assertTrue(Files.exists(reportPath.toPath()))
        assertTrue(data.contains("NOTE: API changes are backward compatible"))
    }

    @Test fun canChangeReportName() {
        // Set up the test build
        settingsFile.writeText("")
        buildFile.writeText("""
            plugins {
                id('com.x3t.gradle.plugins.openapi.openapi_diff')
            }
            
            openapi_diff {
                originalFile = "${projectDir}/missing_1.yaml"
                newFile = "${projectDir}/missing_2.yaml"
                markdownReport.set(true)
                reportName.set("${projectDir}/build/TestReport")
            }
        """.trimIndent())

        Files.copy(missingFile1,testFile1.toPath())
        Files.copy(missingFile2,testFile2.toPath())

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("openapi_diff")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.toString().isNotEmpty())
        val reportPath = projectDir.resolve("${projectDir}/build/TestReport.md")
        val data = reportPath.readText()

        assertTrue(Files.exists(reportPath.toPath()))
        assertTrue(data.contains("* Deleted property `childProperty` (object)"))
    }
}
