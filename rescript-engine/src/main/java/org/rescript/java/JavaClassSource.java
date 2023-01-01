package org.rescript.java;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class JavaClassSource implements JavaSourceBuilder {

  private String packageName;

  private String accessModifer;

  private String type;

  private String generic;

  private String extendsType;

  private List<String> implementsTypes;

  private List<String> imports;

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

  public String getGeneric() {
    return generic;
  }

  public void setGeneric(String generic) {
    this.generic = generic;
  }

  public String getExtendsType() {
    return extendsType;
  }

  public void setExtendsType(String extendsType) {
    this.extendsType = extendsType;
  }

  public List<String> getImplementsTypes() {
    return implementsTypes;
  }

  public void setImplementsTypes(List<String> implementsTypes) {
    this.implementsTypes = implementsTypes;
  }

  public List<String> getImports() {
    return imports;
  }

  public void setImports(List<String> imports) {
    this.imports = imports;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  @Override
  public void addImport(String importType) {
    // TODO Auto-generated method stub
    
  }

  private String fqcn() {
    return StringUtils.isBlank(packageName)?type:packageName+"."+type;
  }

  @Override
  public JavaSource create() {
    StringBuilder b = new StringBuilder();
    if(StringUtils.isNotBlank(packageName)) {
      b.append("package " + packageName + ";\n");
    }

    // TODO add imports here
    b.append("import java.util.function.*;\n");

    if(StringUtils.isNotBlank(accessModifer)) {
      b.append(accessModifer);
      b.append(" ");
    }
    b.append("class ");
    b.append(type);
    b.append(" ");
    if(StringUtils.isNotBlank(generic)) {
      b.append(generic);
      b.append(" ");
    }
    if(StringUtils.isNotBlank(extendsType)) {
      b.append("extends ");
      b.append(extendsType);
      b.append(" ");
    }
    if(implementsTypes != null && !implementsTypes.isEmpty()) {
      b.append("implements ");
      b.append(implementsTypes.stream().collect(Collectors.joining(", ")));
      b.append(" ");
    }
    b.append(body);
    return new JavaSource(fqcn(), b.toString());
  }

}
