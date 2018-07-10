package com.pince.compiler.architecture.ext

import com.pince.compiler.architecture.ANNOTATION_METHOD_NAMES
import com.pince.compiler.architecture.info
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element

/**
 * 是否该元素的注解列表中包含优先级更高的方法注解
 */
fun Element.isContainMethodAnnotation(): Boolean {
    for (annotationMirror in this.annotationMirrors) {
        if(annotationMirror.annotationType.toString() in ANNOTATION_METHOD_NAMES.map { it.canonicalName }) {
            return true
        }
    }
    return false
}


fun Element.getClassListFromAnnotation(annotationName: String, keyName: String): AnnotationValue? {
    for (annotationMirror in this.annotationMirrors) {
        if(annotationMirror.annotationType.toString().equals(annotationName)) {
            for (entry in annotationMirror.elementValues.entries) {
                if (keyName.equals(entry.key.simpleName.toString())) {
                    return entry.value
                }
            }
        }
    }
    return null
}