package com.pince.compiler.architecture.generator

import com.pince.compiler.architecture.*
import com.pince.lib_annotation.method.ViewModelJsonMethod
import com.squareup.kotlinpoet.*
import javax.lang.model.element.ExecutableElement

class GeneratorFunJson(executableElement: ExecutableElement) : GeneratorFun(executableElement) {

    val mViewModelJsonMethod: ViewModelJsonMethod

    init {
        mViewModelJsonMethod = executableElement.getAnnotation(ViewModelJsonMethod::class.java)
    }

    override fun generateFun(typeBuilder: TypeSpec.Builder): TypeSpec.Builder {
        val jsonParamsStringBuilder = StringBuilder("val params = %T()")
        for (keyIndex in mViewModelJsonMethod.keys.indices) {
            val key = mViewModelJsonMethod.keys.get(keyIndex)
            val keyClass = mViewModelJsonMethod.keysClass.get(keyIndex)
            val paramName = key
            mFunSpecBuilder.addParameter(paramName, keyClass.simpleJavaTypeToClassName()!!)
            jsonParamsStringBuilder.append(String.format(".put(\"%s\"," + paramName, paramName) + ")")
        }

        //kt拓展方法toResponseBody无法通过%T自动import包，也无法直接固定全名
        return typeBuilder.addFunction(mFunSpecBuilder
                .addStatement(jsonParamsStringBuilder.toString(), BASE_JSON_OBJECT_CLASS)
                .addStatement("mService.%L(%T.create(%T.parse(\"application/json\"), params.toString())).subscribe(%T(%L))",
                        mFunName, REQUEST_BODY_CLASS, MEDIA_TYPE_CLASS, BASE_APP_LIVEDATA_OBSERVER, mPropertyName)
                .build())
    }

}