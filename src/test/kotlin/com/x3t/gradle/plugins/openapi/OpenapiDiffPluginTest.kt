package com.x3t.gradle.plugins.openapi

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import kotlin.test.assertNotEquals

class OpenapiDiffPluginTest {
    @Test
    fun `plugin registers task correctly`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("com.x3t.gradle.plugins.openapi.openapi_diff")

        // Verify the result
        assertNotNull(project.tasks.findByName("openapi_diff"))
    }

    @Test
    fun `extension openapi_diff is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.x3t.gradle.plugins.openapi.openapi_diff")

        assertNotNull(project.extensions.getByName("openapi_diff"))
    }

    @Test
    fun `Test Html Report Parameters passed correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.x3t.gradle.plugins.openapi.openapi_diff")
        (project.extensions.getByName("openapi_diff") as OpenapiDiffPluginExtension).apply {
            htmlReport.set(true)
        }

        val task = project.tasks.getByName("openapi_diff") as OpenapiDiffPluginTask

        assertEquals(true, task.htmlReport.get())
        assertNotEquals(true, task.jsonReport.get())
        assertNotEquals(true, task.textReport.get())
        assertNotEquals(true, task.markdownReport.get())
        assertNotEquals(true, task.asciidocReport.get())
    }

    @Test
    fun `Test JSON Report Parameters passed correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.x3t.gradle.plugins.openapi.openapi_diff")
        (project.extensions.getByName("openapi_diff") as OpenapiDiffPluginExtension).apply {
            jsonReport.set(true)
        }

        val task = project.tasks.getByName("openapi_diff") as OpenapiDiffPluginTask

        assertEquals(true, task.jsonReport.get())
        assertNotEquals(true, task.htmlReport.get())
        assertNotEquals(true, task.textReport.get())
        assertNotEquals(true, task.markdownReport.get())
    }

    @Test
    fun `Test Markdown Report Parameters passed correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.x3t.gradle.plugins.openapi.openapi_diff")
        (project.extensions.getByName("openapi_diff") as OpenapiDiffPluginExtension).apply {
            markdownReport.set(true)
        }

        val task = project.tasks.getByName("openapi_diff") as OpenapiDiffPluginTask

        assertEquals(true, task.markdownReport.get())
        assertNotEquals(true, task.htmlReport.get())
        assertNotEquals(true, task.textReport.get())
        assertNotEquals(true, task.jsonReport.get())
    }

    @Test
    fun `Test Asciidoc Report Parameters passed correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.x3t.gradle.plugins.openapi.openapi_diff")
        (project.extensions.getByName("openapi_diff") as OpenapiDiffPluginExtension).apply {
            asciidocReport.set(true)
        }

        val task = project.tasks.getByName("openapi_diff") as OpenapiDiffPluginTask

        assertEquals(true, task.asciidocReport.get())
        assertNotEquals(true, task.htmlReport.get())
        assertNotEquals(true, task.textReport.get())
        assertNotEquals(true, task.jsonReport.get())
        assertNotEquals(true, task.markdownReport.get())
    }

    @Test
    fun `Test Text Report Parameters passed correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.x3t.gradle.plugins.openapi.openapi_diff")
        (project.extensions.getByName("openapi_diff") as OpenapiDiffPluginExtension).apply {
            textReport.set(true)
        }

        val task = project.tasks.getByName("openapi_diff") as OpenapiDiffPluginTask

        assertEquals(true, task.textReport.get())
        assertNotEquals(true, task.htmlReport.get())
        assertNotEquals(true, task.markdownReport.get())
        assertNotEquals(true, task.jsonReport.get())
        assertNotEquals(true, task.asciidocReport.get())
    }
}