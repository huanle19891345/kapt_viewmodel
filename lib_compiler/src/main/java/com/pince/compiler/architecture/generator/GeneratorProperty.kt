package com.pince.compiler.architecture.generator

import com.squareup.kotlinpoet.*
import javax.lang.model.element.ExecutableElement

class GeneratorProperty(executableElement: ExecutableElement) : ExecutableGenerator(executableElement) {

    fun generateProperty(typeBuilder: TypeSpec.Builder): TypeSpec.Builder {
        initPropertyType()
        return typeBuilder.addProperty(PropertySpec.builder(mPropertyName, mPropertyType,
                KModifier.PUBLIC)
                .delegate("lazy { " + "%T() }", mPropertyType)
                .build())
    }

}