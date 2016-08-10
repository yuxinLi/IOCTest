package com.example.ioc.processor;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * 描    述：
 * 作    者：liyx@13322.com
 * 时    间：2016/8/3
 */
public class ProxyInfo {

    private String packageName;
    private String targetClassName;
    private String proxytClassName;
    private TypeElement typeElement;
    public static final String PROXY = "Injector";

    private int layoutId;

    private Map<Integer, ViewInfo> idViewMap = new HashMap<Integer, ViewInfo>();

    public ProxyInfo(String packageName, String targetClassName) {
        this.packageName = packageName;
        this.targetClassName = targetClassName;
        this.proxytClassName = targetClassName + "$$" + PROXY;
    }

    public void putViewInfo(int id, ViewInfo viewInfo) {
        idViewMap.put(id, viewInfo);
    }

    public Map<Integer, ViewInfo> getIdViewMap() {
        return idViewMap;
    }

    public String getProxyClassFullName() {
        return packageName + "." + proxytClassName;
    }

    public void setTypeElement(TypeElement typeElement) {
        this.typeElement = typeElement;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }


    public Map<Integer, VariableElement> mInjectElements = new HashMap<Integer, VariableElement>();

    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("package " + packageName).append(";\n\n");
        builder.append("import com.example.ioc.*;\n");
        builder.append("import com.example.ioc.processor.AbstractInjector;\n");
        builder.append("public class ").append(proxytClassName).append(" implements " + " AbstractInjector " + "<" + typeElement.getQualifiedName() + ">");
        builder.append("\n{\n");


        builder = generateMethod(builder);
        builder.append("\n}\n");
        return builder.toString();
    }

    private StringBuilder generateMethod(StringBuilder builder) {
        builder.append("public void inject(" + typeElement.getQualifiedName() + " host , Object object )");
        builder.append("\n{\n");
        for (int id : idViewMap.keySet()) {
            ViewInfo viewInfo = idViewMap.get(id);
            String name = viewInfo.getName();
            String type = viewInfo.getType();

            builder.append(" if(object instanceof android.app.Activity)");
            builder.append("\n{\n");
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.app.Activity)object).findViewById(" + id + "));");
            builder.append("\n}\n").append("else").append("\n{\n");
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.view.View)object).findViewById(" + id + "));");
            builder.append("\n}\n");
        }
        builder.append("\n}\n");
        return builder;
    }

//    private StringBuilder generateMethod(StringBuilder builder) {
//        builder.append("public void inject(" + typeElement.getQualifiedName() + " host , Object object )");
//        builder.append("\n{\n");
//        for (int id : mInjectElements.keySet()) {
//            VariableElement variableElement = mInjectElements.get(id);
//            String name = variableElement.getSimpleName().toString();
//            String type = variableElement.asType().toString();
//
//            builder.append(" if(object instanceof android.app.Activity)");
//            builder.append("\n{\n");
//            builder.append("host." + name).append(" = ");
//            builder.append("(" + type + ")(((android.app.Activity)object).findViewById(" + id + "));");
//            builder.append("\n}\n").append("else").append("\n{\n");
//            builder.append("host." + name).append(" = ");
//            builder.append("(" + type + ")(((android.view.View)object).findViewById(" + id + "));");
//            builder.append("\n}\n");
//        }
//        builder.append("\n}\n");
//        return builder;
//    }
}
