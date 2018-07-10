package com.pince.compiler.architecture;

import com.google.auto.service.AutoService;
import com.pince.compiler.architecture.ext.isAnnotationMethod
import com.pince.compiler.architecture.ext.isAnnotationType
import com.pince.compiler.architecture.ext.isContainMethodAnnotation
import java.util.*;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

/**
 * 使用 Google 的 auto-service 库可以自动生成 META-INF/services/javax.annotation.processing.Processor 文件
 * 使用 gradlew :app:compileDebugJavaWithJavac 进行调试
 * 每个注解的处理逻辑应该独立，不应该相互关联
 *
 * ViewModel生成器，支持如下注解
 *  注解名称                   缓存方式                               生成方法中的参数获取方式
 * ViewModelRemoteType:        直接调用api网络请求                    利用被注解方法ExecutableElement获取
 * ViewModelLocalService   先展示本地数据，再网络请求结果再次刷新   利用被注解方法ExecutableElement获取
 * ViewModelPostJson        直接调用api网络请求                    注解中获取
 */
@AutoService(Processor::class)
class ViewModelProcesser : AbstractProcessor() {

    private lateinit var mFiler: Filer //文件相关的辅助类
    private lateinit var mElementUtils: Elements //元素相关的辅助类
    private lateinit var mRoundEnvironment: RoundEnvironment
    private val mAnnotatedClassMap = HashMap<String, AnnotatedClass>()

    companion object {
        //注: key= kapt.kotlin.generated value = C:\Users\zhenghuan\git\zb-android\app\build\generated\source\kaptKotlin\debug
        //该目录下生成的kt文件不能被项目代码引用，因此修改为C:\Users\zhenghuan\git\zb-android\app\build\generated\source\apt\debug
        private const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        private lateinit var mOutputDirectory: String
        lateinit var mMessager: Messager //日志相关的辅助类
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        mFiler = processingEnv.filer
        mElementUtils = processingEnv.elementUtils
        mMessager = processingEnv.messager
        info("[init] start")
        for (item in processingEnv.options) {
            info("key= " + item.key + " value = " + item.value)
            if (item.key.equals(KAPT_KOTLIN_GENERATED_OPTION_NAME)) {
                mOutputDirectory = item.value.transformFromKaptPathToAptPath()
            }
        }
        info("mFiler = " + mFiler)
        info("mElementUtils = " + mElementUtils)
        info("mMessager = " + mMessager)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        info("getSupportedAnnotationTypes")
        return SUPPORED_TYPES.map { it.canonicalName }.toMutableSet()
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        info("getSupportedSourceVersion")
        return SourceVersion.latestSupported();
    }

    override fun process(p0: MutableSet<out TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        info("[process] start");
        mAnnotatedClassMap.clear()
        mRoundEnvironment = roundEnvironment
        for (annotation in SUPPORED_TYPES) {
            if (annotation.isAnnotationType()) {
                handleClassAnnotation(annotation)
            } else if (annotation.isAnnotationMethod()) {
                handleFunAnnotation(annotation)
            }
        }

        writeAnnotatedClassMap()
        info("[process] end")
        return false
    }

    fun handleClassAnnotation(classAnnotation: Class<out Annotation>) {
        for (element in mRoundEnvironment.getElementsAnnotatedWith(classAnnotation)) {
            //处理类注解中所有元素中不包含自定义方法注解的方法(方法注解中的方法在对应的注解处理逻辑中处理)
            info("handleClassAnnotation = " + element)
            if (element is TypeElement) {
                for (subElement in element.enclosedElements) {
                    if (subElement is ExecutableElement) {
                        if (!subElement.isContainMethodAnnotation()) {
                            getAnnotatedClass(element).appendViewModelItem(classAnnotation, subElement)
                        }
                    }
                }
            }
        }
    }

    fun handleFunAnnotation(funAnnotation: Class<out Annotation>) {
        for (element in mRoundEnvironment.getElementsAnnotatedWith(funAnnotation)) {
            info("handleFunAnnotation = " + element)
            if (element is ExecutableElement) {
                getAnnotatedClassFromFun(element).appendViewModelItem(funAnnotation, element)
            }
        }
    }

    fun writeAnnotatedClassMap() {
        for (annotatedClass in mAnnotatedClassMap.values) {
            try {
                annotatedClass.generateViewModel().writeTo(mOutputDirectory)
            } catch (e: Exception) {
                error(e.message!!)
            }
        }
    }

    /**
     * 根据fun element获取AnnotatedClass实例
     */
    private fun getAnnotatedClassFromFun(executableElement: Element): AnnotatedClass {
        val classElement = executableElement.enclosingElement as TypeElement
        return getAnnotatedClass(classElement)
    }

    private fun getAnnotatedClass(classElement: TypeElement): AnnotatedClass {
        val fullClassName = classElement.qualifiedName.toString()
        var annotatedClass: AnnotatedClass? = mAnnotatedClassMap.get(fullClassName)
        if (annotatedClass == null) {
            annotatedClass = AnnotatedClass(classElement)
            mAnnotatedClassMap.put(fullClassName, annotatedClass)
        }
        return annotatedClass
    }

}