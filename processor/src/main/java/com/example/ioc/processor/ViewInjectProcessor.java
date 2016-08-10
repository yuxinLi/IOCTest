package com.example.ioc.processor;

import com.example.ioc.lib.InjectView;
import com.google.auto.service.AutoService;

import java.io.Writer;
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
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * 描    述：
 * 作    者：liyx@13322.com
 * 时    间：2016/8/8
 */
@AutoService(Processor.class)
public class ViewInjectProcessor extends AbstractProcessor {

    private Map<String , ProxyInfo> mProxyMap = new HashMap<String, ProxyInfo>();
    private Filer mFileUtils;
    private Elements mElementUtils;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFileUtils = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<String>();
        annotationTypes.add(InjectView.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        String fqClassName, className, packageName;
        for (Element ele : roundEnv.getElementsAnnotatedWith(InjectView.class)){
            mMessager.printMessage(Diagnostic.Kind.NOTE , "ele = "+ ele);

            if (ele.getKind() == ElementKind.FIELD){
                VariableElement varElement = (VariableElement) ele;
                TypeElement classElement = (TypeElement) varElement.getEnclosingElement();
                // 全类名
                fqClassName = classElement.getQualifiedName().toString();
                PackageElement packageElement = mElementUtils.getPackageOf(classElement);
                packageName = packageElement.getQualifiedName().toString();
                className = getClassName(classElement, packageName);

                int id = varElement.getAnnotation(InjectView.class).value();
                String fieldName = varElement.getSimpleName().toString();
                String fieldType = varElement.asType().toString();

                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.NOTE,
                        "annatated field : fieldName = "
                                + fieldName
                                + " , id = " + id + " , fileType = "
                                + fieldType);

                ProxyInfo proxyInfo = mProxyMap.get(fqClassName);
                if (proxyInfo == null)
                {
                    proxyInfo = new ProxyInfo(packageName, className);
                    mProxyMap.put(fqClassName, proxyInfo);
                    proxyInfo.setTypeElement(classElement);

                    mProxyMap.put(fqClassName , proxyInfo);
                }
                proxyInfo.putViewInfo(id,
                        new ViewInfo(id, fieldName, fieldType));
            }

        }

        for (String key : mProxyMap.keySet()){
            ProxyInfo proxyInfo = mProxyMap.get(key);
            try {
                JavaFileObject jfo = mFileUtils.createSourceFile(proxyInfo.getProxyClassFullName() ,
                        proxyInfo.getTypeElement());

                Writer writer = jfo.openWriter();

                String source = proxyInfo.generateJavaCode();
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.NOTE,
                        "java source : ");
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.NOTE,
                        source);

                writer.write(source);
                writer.flush();
                writer.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace('.', '$');
    }
}
