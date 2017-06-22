package com.lenovohit.processor;

import com.google.auto.service.AutoService;
import com.lenovohit.annotation.Action;
import com.lenovohit.annotation.Application;
import com.lenovohit.annotation.IntentInterceptor;
import com.lenovohit.annotation.Interceptor;
import com.lenovohit.annotation.Provider;
import com.lenovohit.annotation.Service;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * 扫描Processor
 * Created by yuzhijun on 2017/6/21.
 */
@AutoService(Processor.class)
public class ProviderActionProcessor extends AbstractProcessor{
    private static final String PACKAGE_NAME = "com.lenovohit";
    private static final String METHOD_NAME = "inject";
    private Filer mFileUtils;
    private Elements mElementUtils;
    private Messager mMessager;
    private Map<String, ProxyInfo> mProviderMap = new HashMap<>();
    private Map<String, ProxyInfo> mActionMap = new HashMap<>();
    private Map<String, ProxyInfo> mServiceMap = new HashMap<>();
    private Map<String, ProxyInfo> mApplicaitonMap = new HashMap<>();
    private Map<String, ProxyInfo> mIntentInterceptMap = new HashMap<>();
    private Map<String, ProxyInfo> mInterceptMap = new HashMap<>();
    private static final ClassName LRouterClass = ClassName.get("com.lenovohit.lrouter_api.core", "LocalRouter");
    private static final ClassName RemoteRouterClass = ClassName.get("com.lenovohit.lrouter_api.core", "RemoteRouter");
    private static final ClassName LRouterApplicationClass = ClassName.get("com.lenovohit.lrouter_api.base", "LRouterAppcation");
    private static final ClassName LRProviderClass = ClassName.get("com.lenovohit.lrouter_api.core", "LRProvider");
    private static final ClassName InstrumentationClass = ClassName.get("com.lenovohit.lrouter_api.hook", "InstrumentationHook");
    private static final ClassName RequstAspectClass = ClassName.get("com.lenovohit.lrouter_api.intercept", "RequstAspect");
    private static final ClassName InjectorClass = ClassName.get("com.lenovohit.lrouter_api.utils", "Injector");

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFileUtils = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<>();
        annotationTypes.add(Action.class.getCanonicalName());
        annotationTypes.add(Provider.class.getCanonicalName());
        annotationTypes.add(Service.class.getCanonicalName());
        annotationTypes.add(Application.class.getCanonicalName());
        annotationTypes.add(IntentInterceptor.class.getCanonicalName());
        annotationTypes.add(Interceptor.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion(){
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mProviderMap.clear();
        mActionMap.clear();
        mServiceMap.clear();
        mApplicaitonMap.clear();
        mIntentInterceptMap.clear();
        mInterceptMap.clear();

        if (!annotations.isEmpty()) {
            Set<? extends Element> elesWithAction = roundEnv.getElementsAnnotatedWith(Action.class);
            Set<? extends Element> elesWithProvider = roundEnv.getElementsAnnotatedWith(Provider.class);
            Set<? extends Element> elesWithService = roundEnv.getElementsAnnotatedWith(Service.class);
            Set<? extends Element> elesWithApplication = roundEnv.getElementsAnnotatedWith(Application.class);
            Set<? extends Element> elesWithIntentInterceptor = roundEnv.getElementsAnnotatedWith(IntentInterceptor.class);
            Set<? extends Element> elesWithInterceptor = roundEnv.getElementsAnnotatedWith(Interceptor.class);

            try {
                generateProviderHelper(elesWithProvider);
                generateActionHelper(elesWithAction);
                generateServiceHelper(elesWithService);
                generateApplicationHelper(elesWithApplication);
                generateIntentInterceptorHelper(elesWithIntentInterceptor);
                generateInterceptorHelper(elesWithInterceptor);
            } catch (Exception e) {
               e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    private void generateProviderHelper(Set<? extends Element> elesWithProvider) throws Exception{
        for (Element element : elesWithProvider){
                checkAnnotationValid(element, Provider.class);

            TypeElement  classElement = (TypeElement) element;
            //full class name
            String providerClassName = classElement.getQualifiedName().toString();

            ProxyInfo proxyInfo = mProviderMap.get(providerClassName);
            if (null == proxyInfo){
                proxyInfo = new ProxyInfo(mElementUtils, classElement);
                mProviderMap.put(providerClassName, proxyInfo);
            }

            Provider providerAnnotation = classElement.getAnnotation(Provider.class);
            proxyInfo.setName(providerAnnotation.name());
        }


        for (String key : mProviderMap.keySet()){
            ProxyInfo proxyInfo = mProviderMap.get(key);

            MethodSpec.Builder initBuilder = MethodSpec.methodBuilder(METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(TypeName.VOID);

            initBuilder.addStatement("$T.getInstance($T.getInstance()).registerProvider($S,new $T())",
                    LRouterClass,LRouterApplicationClass,proxyInfo.getName(),ClassName.get(proxyInfo.typeElement));

            TypeSpec providerInject = TypeSpec.classBuilder(proxyInfo.proxyClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(InjectorClass)
                    .addMethod(initBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME,providerInject).build();
            javaFile.writeTo(mFileUtils);
        }
    }

    private void generateActionHelper(Set<? extends Element> elesWithAction) throws Exception{
        for (Element element : elesWithAction){
            checkAnnotationValid(element, Action.class);

            TypeElement  classElement = (TypeElement) element;
            //full class name
            String actionClassName = classElement.getQualifiedName().toString();

            ProxyInfo proxyInfo = mActionMap.get(actionClassName);
            if (null == proxyInfo){
                proxyInfo = new ProxyInfo(mElementUtils, classElement);
                mActionMap.put(actionClassName, proxyInfo);
            }

            Action actionAnnotation = classElement.getAnnotation(Action.class);
            proxyInfo.setName(actionAnnotation.name());
            proxyInfo.setProvider(actionAnnotation.provider());
        }

        for (String key : mActionMap.keySet()) {
            ProxyInfo proxyInfo = mActionMap.get(key);

            MethodSpec.Builder initBuilder = MethodSpec.methodBuilder(METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(TypeName.VOID);

            initBuilder.addStatement("$T $N = $T.getInstance($T.getInstance()).findProvider($S)",
                    LRProviderClass,LRProviderClass.simpleName().toLowerCase(),LRouterClass,LRouterApplicationClass,proxyInfo.getProvider());
            initBuilder.addCode("if(null != $N){\n",LRProviderClass.simpleName().toLowerCase());
            initBuilder.addCode("$N.registerAction($S,new $T());\n}\n",
                    LRProviderClass.simpleName().toLowerCase(),proxyInfo.getName(),ClassName.get(proxyInfo.typeElement));

            TypeSpec actionInject = TypeSpec.classBuilder(proxyInfo.proxyClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(InjectorClass)
                    .addMethod(initBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME,actionInject).build();
            javaFile.writeTo(mFileUtils);
        }
    }

    private void generateServiceHelper(Set<? extends Element> elesWithService) throws Exception{
        for (Element element : elesWithService){
            checkAnnotationValid(element, Service.class);

            TypeElement  classElement = (TypeElement) element;
            //full class name
            String serviceClassName = classElement.getQualifiedName().toString();

            ProxyInfo proxyInfo = mServiceMap.get(serviceClassName);
            if (null == proxyInfo){
                proxyInfo = new ProxyInfo(mElementUtils, classElement);
                mServiceMap.put(serviceClassName, proxyInfo);
            }

            Service serviceAnnotation = classElement.getAnnotation(Service.class);
            proxyInfo.setName(serviceAnnotation.name());
        }

        for (String key : mServiceMap.keySet()) {
            ProxyInfo proxyInfo = mServiceMap.get(key);

            MethodSpec.Builder initBuilder = MethodSpec.methodBuilder(METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(TypeName.VOID);

            initBuilder.addStatement("$T.registerLocalRouterServiceConnection($S,$T.class)",
                    RemoteRouterClass,proxyInfo.getName(),ClassName.get(proxyInfo.typeElement));

            TypeSpec serviceInject = TypeSpec.classBuilder(proxyInfo.proxyClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(InjectorClass)
                    .addMethod(initBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME,serviceInject).build();
            javaFile.writeTo(mFileUtils);
        }
    }

    private void generateApplicationHelper(Set<? extends Element> elesWithApplication) throws Exception{
        for (Element element : elesWithApplication){
            checkAnnotationValid(element, Application.class);

            TypeElement  classElement = (TypeElement) element;
            //full class name
            String applicationClassName = classElement.getQualifiedName().toString();

            ProxyInfo proxyInfo = mApplicaitonMap.get(applicationClassName);
            if (null == proxyInfo){
                proxyInfo = new ProxyInfo(mElementUtils, classElement);
                mApplicaitonMap.put(applicationClassName, proxyInfo);
            }

            Application applicationAnnotation = classElement.getAnnotation(Application.class);
            proxyInfo.setName(applicationAnnotation.name());
            proxyInfo.setPriority(applicationAnnotation.priority());
        }

        for (String key : mApplicaitonMap.keySet()) {
            ProxyInfo proxyInfo = mApplicaitonMap.get(key);

            MethodSpec.Builder initBuilder = MethodSpec.methodBuilder(METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(TypeName.VOID);

            initBuilder.addStatement("$T.getInstance().registerAnologyApplication($S,$L,$T.class)",
                    LRouterApplicationClass,proxyInfo.getName(),proxyInfo.getPriority(),ClassName.get(proxyInfo.typeElement));

            TypeSpec applicationInject = TypeSpec.classBuilder(proxyInfo.proxyClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(InjectorClass)
                    .addMethod(initBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME,applicationInject).build();
            javaFile.writeTo(mFileUtils);
        }
    }

    private void generateIntentInterceptorHelper(Set<? extends Element> elesWithIntentInterceptor) throws Exception{
        for (Element element : elesWithIntentInterceptor){
            checkAnnotationValid(element, IntentInterceptor.class);

            TypeElement  classElement = (TypeElement) element;
            //full class name
            String intentInterceptorClassName = classElement.getQualifiedName().toString();
            ProxyInfo proxyInfo = mIntentInterceptMap.get(intentInterceptorClassName);
            if (null == proxyInfo){
                proxyInfo = new ProxyInfo(mElementUtils, classElement);
                mIntentInterceptMap.put(intentInterceptorClassName, proxyInfo);
            }
        }

        for (String key : mIntentInterceptMap.keySet()) {
            ProxyInfo proxyInfo = mIntentInterceptMap.get(key);

            MethodSpec.Builder initBuilder = MethodSpec.methodBuilder(METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(TypeName.VOID);

            initBuilder.addStatement("$T.registIntentInterceptor(new $T())",InstrumentationClass,ClassName.get(proxyInfo.typeElement));

            TypeSpec intentInterceptorInject = TypeSpec.classBuilder(proxyInfo.proxyClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(InjectorClass)
                    .addMethod(initBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME,intentInterceptorInject).build();
            javaFile.writeTo(mFileUtils);
        }
    }

    private void generateInterceptorHelper(Set<? extends Element> elesWithInterceptor) throws Exception{
        for (Element element : elesWithInterceptor){
            checkAnnotationValid(element, Interceptor.class);

            TypeElement  classElement = (TypeElement) element;
            //full class name
            String interceptorClassName = classElement.getQualifiedName().toString();

            ProxyInfo proxyInfo = mInterceptMap.get(interceptorClassName);
            if (null == proxyInfo){
                proxyInfo = new ProxyInfo(mElementUtils, classElement);
                mInterceptMap.put(interceptorClassName, proxyInfo);
            }
        }

        for (String key : mInterceptMap.keySet()) {
            ProxyInfo proxyInfo = mInterceptMap.get(key);

            MethodSpec.Builder initBuilder = MethodSpec.methodBuilder(METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(TypeName.VOID);

            initBuilder.addStatement("$T.interceptorInject(new $T())",RequstAspectClass,ClassName.get(proxyInfo.typeElement));

            TypeSpec interceptorInject = TypeSpec.classBuilder(proxyInfo.proxyClassName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(InjectorClass)
                    .addMethod(initBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(PACKAGE_NAME,interceptorInject).build();
            javaFile.writeTo(mFileUtils);
        }
    }

    private boolean checkAnnotationValid(Element annotatedElement, Class clazz) {
        if (annotatedElement.getKind() != ElementKind.CLASS) {
            error(annotatedElement, "%s must be declared on class.", clazz.getSimpleName());
            return false;
        }
        if (ClassValidator.isPrivate(annotatedElement)) {
            error(annotatedElement, "%s() must can not be private.", annotatedElement.getSimpleName());
            return false;
        }
        return true;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }
}
