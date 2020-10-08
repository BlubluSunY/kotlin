/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test

import com.intellij.openapi.Disposable
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.test.components.ConfigurationComponents
import org.jetbrains.kotlin.test.components.KotlinCoreEnvironmentProviderImpl
import org.jetbrains.kotlin.test.components.LanguageVersionSettingsProviderImpl
import org.jetbrains.kotlin.test.components.SourceFileProviderImpl
import org.jetbrains.kotlin.test.directives.SimpleDirectivesContainer
import org.jetbrains.kotlin.test.impl.FirDependencyProvider
import org.jetbrains.kotlin.test.impl.FirFrontendFacade
import org.jetbrains.kotlin.test.model.ModuleStructureExtractor
import org.junit.jupiter.api.Test

class SomeDiagnosticsTest {

    @Test
    @TestMetadata("compiler/new-tests-infrastructure/testData/a.kt")
    fun testSimple() {
        doTest("compiler/new-tests-infrastructure/testData/a.kt")
    }

    fun doTest(fileName: String) {
        val components = ConfigurationComponents.build {
            kotlinCoreEnvironmentProvider = KotlinCoreEnvironmentProviderImpl(this, TestDisposable())
            sourceFileProvider = SourceFileProviderImpl(emptyList())
            languageVersionSettingsProvider = LanguageVersionSettingsProviderImpl()
        }
        val facade = FirFrontendFacade(components)

        val (modules, globalDirectives) = ModuleStructureExtractor.splitTestDataByModules(fileName, SimpleDirectivesContainer.Empty)
        val dependencyProvider = FirDependencyProvider(components, modules)
        for (module in modules) {
            val analysisResults = facade.analyze(module, dependencyProvider)
            dependencyProvider.registerAnalyzedModule(module.name, analysisResults)
            val (session, firFiles) = analysisResults
            firFiles.values.forEach { println(it.render()) }
        }
    }
}


class TestDisposable : Disposable {
    @Volatile
    var isDisposed = false
        private set

    override fun dispose() {
        isDisposed = true
    }
}
