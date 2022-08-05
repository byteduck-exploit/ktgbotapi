package dev.inmo.tgbotapi.ksp.processor

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import dev.inmo.tgbotapi.ksp.lib.ClassCastsIncluded
import java.io.File

class TelegramBotAPISymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val targetPackage: String = "",
    private val outputFile: String = "Output",
    private val outputFolder: String? = null
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val classes = resolver.getSymbolsWithAnnotation(ClassCastsIncluded::class.qualifiedName!!).filterIsInstance<KSClassDeclaration>()
        val classesSubtypes = mutableMapOf<KSClassDeclaration, MutableSet<KSClassDeclaration>>()

        resolver.getAllFiles().forEach {
            it.declarations.forEach { potentialSubtype ->
                if (potentialSubtype is KSClassDeclaration) {
                    val allSupertypes = potentialSubtype.getAllSuperTypes().map { it.declaration }
                    classes.forEach {
                        if (it in allSupertypes) {
                            classesSubtypes.getOrPut(it) { mutableSetOf() }.add(potentialSubtype)
                        }
                    }
                }
            }
        }
        fun fillWithSealeds(source: KSClassDeclaration, current: KSClassDeclaration = source) {
            current.getSealedSubclasses().forEach {
                classesSubtypes.getOrPut(source) { mutableSetOf() }.add(it)
                fillWithSealeds(source, it)
            }
        }
        classes.forEach { fillWithSealeds(it) }

        val fileSpec = FileSpec.builder(
            targetPackage,
            outputFile
        ).apply {
            classes.forEach {
                fill(
                    it,
                    classesSubtypes.toMap()
                )
            }
        }.build()
        runCatching {
            outputFolder ?.also {
                File(it).apply {
                    delete()
                    runCatching { mkdirs() }
                    fileSpec.writeTo(this)
                }
            } ?: fileSpec.writeTo(codeGenerator, false)
        }

        return emptyList()
    }
}