package org.rescript.java;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;

public class JavaTypeSource implements JavaSourceBuilder {

  private JavaType type;

  private String packageName;

  private String accessModifer;

  private boolean abstractClass;

  private String typeName;

  private String remain;

  private List<String> imports = new ArrayList<>();

  private List<String> staticImports = new ArrayList<>();

  private String body;

  public JavaType getType() {
    return type;
  }

  public void setType(JavaType type) {
    this.type = type;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getAccessModifer() {
    return accessModifer;
  }

  public void setAccessModifer(String accessModifer) {
    this.accessModifer = accessModifer;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public String getRemain() {
    return remain;
  }

  public void setRemain(String remain) {
    this.remain = remain;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @Override
  public void addImport(String imp) {
    imports.add(imp);
  }

  @Override
  public void addStaticImport(String staticImport) {
    staticImports.add(staticImport);
  }

  private String fqcn() {
    return StringUtils.isBlank(packageName)?typeName:packageName+"."+typeName;
  }

  public boolean isAbstractClass() {
    return abstractClass;
  }

  public void setAbstractClass(boolean abstractClass) {
    this.abstractClass = abstractClass;
  }

  @Override
  public JavaSource create() {
    if(type == null) {
      throw new ScriptException("type has not been set");
    }
    StringBuilder b = new StringBuilder();
    if(StringUtils.isNotBlank(packageName)) {
      b.append("package " + packageName + ";\n\n");
    }
    if(!staticImports.isEmpty()) {
      staticImports.forEach(imp -> b.append("import static " + imp +";\n"));
      b.append("\n");
    }
    if(!imports.isEmpty()) {
      imports.forEach(imp -> b.append("import " + imp +";\n"));
      b.append("\n");
    }
    if(StringUtils.isNotBlank(accessModifer)) {
      b.append(accessModifer);
      b.append(" ");
    }
    if(isAbstractClass()) {
      b.append("abstract ");
    }
    b.append(type.code());
    b.append(" ");
    b.append(typeName);
    b.append(" ");
    if(StringUtils.isNotBlank(remain)) {
      b.append(remain);
      b.append(" ");
    }
    b.append(body);
    return new JavaSource(fqcn(), b.toString());
  }

}
