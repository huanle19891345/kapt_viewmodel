package com.pince.compiler.architecture.generator

import com.pince.compiler.architecture.*
import com.squareup.kotlinpoet.*
import javax.lang.model.element.ExecutableElement

class GeneratorFunLocalRemote(executableElement: ExecutableElement) : GeneratorFun(executableElement) {

    override fun generateFun(typeBuilder: TypeSpec.Builder): TypeSpec.Builder {
        val params = mExecutableElement.parameters
        initPropertyType()
        var paramsStringBuilder = StringBuilder()
        for (param in params) {
            val paramName = param.simpleName.toString()
            info("simpleName = " + paramName + " typeName " + param.asType().asTypeName())
            paramsStringBuilder.append(paramName).append(",")
            mFunSpecBuilder.addParameter(param.simpleName.toString(), ClassName("", param.javaTypeToKotlinTypeString()))
        }
        return typeBuilder.addFunction(mFunSpecBuilder
                .addStatement("object : %T(%L) {",
                        ParameterizedTypeName.get(BASE_APP_NETWORK_BOUND_RESOURCE_CLASS, mModelEntireType), mPropertyName)
                .addStatement("override fun doApiCall(): %T<%T> {", OBSERVABLE_CLASS, mModelEntireType)
                .addStatement("return mService.%L(%L)}}", mFunName, paramsStringBuilder.toString().dropLast(1))
                .build())
    }

}