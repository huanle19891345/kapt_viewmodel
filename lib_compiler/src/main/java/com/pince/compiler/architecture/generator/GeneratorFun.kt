package com.pince.compiler.architecture.generator

import com.pince.compiler.architecture.*
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import javax.lang.model.element.ExecutableElement

abstract class GeneratorFun(executableElement: ExecutableElement): ExecutableGenerator(executableElement) {

    protected val mFunSpecBuilder: FunSpec.Builder

    init {
        mFunSpecBuilder = FunSpec.builder(mFunName)
    }

    companion object {
        fun get(annotation: Class<out Annotation>, executableElement: ExecutableElement): GeneratorFun {
            return when(annotation.canonicalName) {
                VIEW_MODEL_REMOTE_TYPE.canonicalName -> GeneratorFunRemote(executableElement)
                VIEW_MODEL_JSON_METHOD.canonicalName -> GeneratorFunJson(executableElement)
                VIEW_MODEL_LOCAL_REMOTE_METHOD.canonicalName -> GeneratorFunLocalRemote(executableElement)
                VIEW_MODEL_REMOTE_METHOD.canonicalName -> GeneratorFunRemote(executableElement)
                else -> GeneratorFunRemote(executableElement)
            }
        }
    }

    abstract fun generateFun(typeBuilder: TypeSpec.Builder): TypeSpec.Builder

}