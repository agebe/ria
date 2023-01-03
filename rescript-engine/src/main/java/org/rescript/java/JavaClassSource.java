package org.rescript.java;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class JavaClassSource implements JavaSourceBuilder {

  private String packageName;

  private String accessModifer;

  private boolean abstractClass;

  private String type;

  private String remain;

  private List<String> imports = new ArrayList<>();

  private List<String> staticImports = new ArrayList<>();

  private String body;

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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
    return StringUtils.isBlank(packageName)?type:packageName+"."+type;
  }

  public boolean isAbstractClass() {
    return abstractClass;
  }

  public void setAbstractClass(boolean abstractClass) {
    this.abstractClass = abstractClass;
  }

  @Override
  public JavaSource create() {
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
    b.append("class ");
    b.append(type);
    b.append(" ");
    if(StringUtils.isNotBlank(remain)) {
      b.append(remain);
      b.append(" ");
    }
    b.append(body);
    return new JavaSource(fqcn(), b.toString());
  }

}
