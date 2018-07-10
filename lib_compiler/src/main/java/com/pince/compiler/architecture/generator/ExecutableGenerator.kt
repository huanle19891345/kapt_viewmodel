package com.pince.compiler.architecture.generator

import com.pince.compiler.architecture.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.ExecutableElement

open class ExecutableGenerator(executableElement: ExecutableElement) {

    val mExecutableElement: ExecutableElement
    var mPropertyName: String = ""
    var mFunName: String = ""

    /**
     * MutableLiveData<List<xxxModel>>
     */
    lateinit var mPropertyType: ParameterizedTypeName
    /**
     * List<xxxModel>
     */
    lateinit var mModelEntireType: TypeName


    init {
        mExecutableElement = executableElement
        mFunName = mExecutableElement.simpleName.toString()
        mPropertyName = mFunName + "LiveData";
    }

    fun initPropertyType(): ParameterizedTypeName {
        //.Observable<.List<.xxxModel>>
        val modelsTypeStringObservable = mExecutableElement.returnType.asTypeName().toString()
        //.List<.xxxModel>
        val modelsTypeStringLong = modelsTypeStringObservable.getParameterizedTypeInnerOne()
        //List<xxxModel>
        val modelsTypeString = modelsTypeStringObservable.getModelsParameterizedType()
        info("modelsTypeString = " + modelsTypeString)

        if (modelsTypeString.contains("List<")) {
            mModelEntireType = ClassName("",modelsTypeStringLong.replaceJavaTypeToKotlinType())
        } else {
            mModelEntireType = ClassName("", modelsTypeStringLong.getParameterizedTypeInnerOne().replaceJavaTypeToKotlinType())
        }

        mPropertyType = ParameterizedTypeName.get(MEDIATOR_LIVEDATA_CLASS, mModelEntireType)
        return mPropertyType;
    }

}