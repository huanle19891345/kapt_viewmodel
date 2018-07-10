package com.pince.compiler.architecture

import com.pince.compiler.architecture.ViewModelProcesser.Companion.mMessager
import com.pince.compiler.architecture.generator.GeneratorFun
import com.pince.compiler.architecture.generator.GeneratorProperty
import com.squareup.kotlinpoet.*
import java.io.File
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic


fun TypeSpec.Builder.addProperty(executableElement: ExecutableElement): TypeSpec.Builder {
    val property = GeneratorProperty(executableElement);
    return property.generateProperty(this)
}

fun TypeSpec.Builder.addFun(annotation : Class< out Annotation>, executableElement: ExecutableElement): TypeSpec.Builder {
    val property = GeneratorFun.get(annotation, executableElement)
    return property.generateFun(this)
}

fun String.transformFromKaptPathToAptPath(): String {
    return File(this).parentFile.parentFile.parentFile.parentFile.parentFile.path + "\\src\\main\\java"
}

fun TypeElement.serviceToViewModelName(): String {
    return "Base" + this.simpleName + "ViewModel"
}

/**
 * 为兼容java类型的retrofit service接口提供兼容转换，防止当方法参数为java类型时，生成的kt文件提示类型错误
 */
fun Element.javaTypeToKotlinTypeString(): String {
    val thisTypeName = asType().asTypeName().toString()
    return thisTypeName.replaceJavaTypeToKotlinType()
}

/**
 * java类型转化为kotlin类型，保留完整类名
 *
 */
fun String.javaTypeToKotlinTypeString(): String {
    val className = javaTypeToKotlinType()
    if (className != null) {
        return className.toString()
    } else {
        return this;
    }
}

fun String.replaceJavaTypeToKotlinType(): String {
    info("[replaceJavaTypeToKotlinType] start = " + this)
    val replaced = replace("java.util.List", "kotlin.collections.List")
            .replace("java.lang.Object", "kotlin.Any")
            .replace("java.lang", "kotlin") //此行替换所有基本类型的共同包名
    info("[replaceJavaTypeToKotlinType] end = " + replaced)
    return replaced
}

private fun String.javaTypeToKotlinType(): ClassName? {
    return when (this) {
        "java.lang.String" -> String::class.java.asClassName()
        "java.lang.Int" -> Int::class.java.asClassName()
        "java.util.List" -> List::class.java.asClassName()
        "java.lang.Object" -> Any::class.java.asClassName()
        else ->  null
    }
}

/**
 * 递归
 */
fun String.simpleJavaTypeToClassName(): TypeName? {
    var currentString = this;
    if (currentString.contains("<")) {
        val ownerType = currentString.getOwnerType()
        currentString = currentString.getParameterizedTypeInnerOne()
        return ParameterizedTypeName.get(ClassName("",ownerType), currentString.simpleJavaTypeToClassName()!!)
    } else {
        return when (this) {
            "String" -> STRING_KT_CLASS
            "Object" -> ANY
            "Array" -> ARRAY
            "Boolean"-> BOOLEAN
            "Byte" -> BYTE
            "Short" -> SHORT
            "Int" -> INT
            "Long" -> LONG
            "Char" -> CHAR
            "Float" -> FLOAT
            "Double" -> DOUBLE
            else ->  null
        }
    }
}

/**
 * 获取类型中的泛型内类型信息，MutableLiveData<List<aaa.bbb.SearchAnchorItemModel>>这类获取的是List<SearchAnchorItemModel>
 *     MutableLiveData<aaa.bbb.SearchAnchorItemModel>这类获取的是SearchAnchorItemModel
 */
fun String.getModelsParameterizedType(): String {
    return getParameterizedTypeInnerOne().getSimpleClassName()
}

/**
 * 向内获取一层的泛型信息,如果没有泛型，则返回原字符串
 */
fun String.getParameterizedTypeInnerOne(): String {
    if (contains('<')) {
        val indexStart = indexOfFirst { '<'.equals(it) }
        val indexEnd = indexOfLast { '>'.equals(it) }
        return substring(indexStart + 1, indexEnd)
    } else {
        return this;
    }
}

/**
 * 向内收缩一层，获取外部类型信息
 */

fun String.getOwnerType(): String {
    if (contains('<')) {
        val indexStart = indexOfFirst { '<'.equals(it) }
        return substring(0, indexStart)
    } else {
        return this;
    }
}

fun String.getModelPackageName(): String {
    val indexEnd = indexOfLast { '.'.equals(it) }
    if (indexEnd > 0) {
        return substring(0, indexEnd)
    } else {
        return ""
    }
}

fun String.getModelClassSimpleName(): String {
    val indexEnd = indexOfLast { '.'.equals(it) }
    if (indexEnd > 0) {
        return substring(indexEnd + 1)
    } else {
        return ""
    }
}

/**
 * 输入com.xxx.xxx.model.xxx.xxxModel，输出.xxxItemModel
 * 输入aaa.ddd.List<com.xxx.xxx.model.anchor.xxxModel>，输出List<xxxModel>
 */
fun String.getSimpleClassName(): String {
    if (this.contains(".List<")) {
        val indexStart = indexOfFirst { '<'.equals(it) }
        val indexEnd = indexOfFirst { '>'.equals(it) }
        return "List<" + substring(indexStart + 1, indexEnd).getSimpleClassName() + ">"
    } else {
        return this.split('.').findLast { !"".equals(it) }!!
    }
}

fun error(msg: String, vararg args: Any) {
    mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, *args))
}

fun info(msg: String, vararg args: Any) {
    mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, *args))
}