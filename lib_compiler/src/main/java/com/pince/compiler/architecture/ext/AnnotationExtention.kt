package com.pince.compiler.architecture.ext

fun Class<out Annotation>.isAnnotationMethod(): Boolean {
    if (this.`package`.name.contains("method")) {
        return true
    }
    return false
}

fun Class<out Annotation>.isAnnotationType(): Boolean {
    if (this.`package`.name.contains("type")) {
        return true
    }
    return false
}