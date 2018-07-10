package com.pince.compiler.architecture

import com.pince.compiler.architecture.ext.isAnnotationMethod
import com.pince.lib_annotation.method.ViewModelJsonMethod
import com.pince.lib_annotation.method.ViewModelLocalRemoteMethod
import com.pince.lib_annotation.method.ViewModelRemoteMethod
import com.pince.lib_annotation.type.ViewModelRemoteType
import com.squareup.kotlinpoet.ClassName


val MUTABLE_LIVEDATA = "MutableLiveData"
val MEDIATOR_LIVEDATA = "MediatorLiveData"

val VIEW_MODEL_REMOTE_TYPE = ViewModelRemoteType::class.java
val VIEW_MODEL_JSON_METHOD = ViewModelJsonMethod::class.java
val VIEW_MODEL_LOCAL_REMOTE_METHOD = ViewModelLocalRemoteMethod::class.java
val VIEW_MODEL_REMOTE_METHOD = ViewModelRemoteMethod::class.java

val SUPPORED_TYPES = mutableSetOf(VIEW_MODEL_REMOTE_TYPE
        , VIEW_MODEL_JSON_METHOD
        , VIEW_MODEL_LOCAL_REMOTE_METHOD
        , VIEW_MODEL_REMOTE_METHOD)

/**
 * 方法注解名称，相对于类注解拥有更高的优先级
 */
val ANNOTATION_METHOD_NAMES = SUPPORED_TYPES.filterIndexed { index, clazz -> clazz.isAnnotationMethod() }


val APPLICATION_CLASS = ClassName("android.app", "Application")
val MUTABLE_LIVEDATA_CLASS = ClassName("android.arch.lifecycle", MUTABLE_LIVEDATA)
val MEDIATOR_LIVEDATA_CLASS = ClassName("android.arch.lifecycle", MEDIATOR_LIVEDATA)
val LIST_KT_CLASS = ClassName("kotlin.collections", "List")
val STRING_KT_CLASS = ClassName("kotlin", "String")
val JSON_OBJECT_CLASS = ClassName("org.json", "JSONObject")
val REQUEST_BODY_CLASS = ClassName("okhttp3", "RequestBody")
val MEDIA_TYPE_CLASS = ClassName("okhttp3", "MediaType")
val OBSERVABLE_CLASS = ClassName("io.reactivex", "Observable")

val BASE_VIEWMODEL_CLASS = ClassName("com.baseproject.architecture", "BaseViewModel")
val BASE_JSON_OBJECT_CLASS = ClassName("com.baseproject.base", "BaseJSONObject")
val BASE_APP_LIVEDATA_OBSERVER = ClassName("com.superfans.ipo.appbase", "BaseAppLiveDataObserver")
val BASE_APP_NETWORK_BOUND_RESOURCE_CLASS = ClassName("com.superfans.ipo.appbase", "BaseAppNetworkBoundResource")