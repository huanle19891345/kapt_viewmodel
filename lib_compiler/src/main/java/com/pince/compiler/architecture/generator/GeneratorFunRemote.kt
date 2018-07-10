package com.pince.compiler.architecture.generator

import com.pince.compiler.architecture.BASE_APP_LIVEDATA_OBSERVER
import com.pince.compiler.architecture.info
import com.pince.compiler.architecture.javaTypeToKotlinTypeString
import com.squareup.kotlinpoet.*
import javax.lang.model.element.ExecutableElement

class GeneratorFunRemote(executableElement: ExecutableElement): GeneratorFun(executableElement) {

    override fun generateFun(typeBuilder: TypeSpec.Builder): TypeSpec.Builder {
        val params = mExecutableElement.parameters
        var paramsStringBuilder  = StringBuilder()
        for (param in params) {
            val paramName = param.simpleName.toString()
            info("simpleName = " + paramName + " typeName " + param.asType().asTypeName())
            paramsStringBuilder.append(paramName).append(",")
            mFunSpecBuilder.addParameter(param.simpleName.toString(), ClassName("", param.javaTypeToKotlinTypeString()))
        }
        return typeBuilder.addFunction(mFunSpecBuilder
                .addStatement("mService.%L(%L).subscribe(%T(%L))",
                mFunName, paramsStringBuilder.toString().dropLast(1), BASE_APP_LIVEDATA_OBSERVER, mPropertyName)
                .build())
    }

}