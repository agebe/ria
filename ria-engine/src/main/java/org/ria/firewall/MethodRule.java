package org.ria.firewall;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

public class MethodRule {

  private String packageName;

  private String className;

  private String name;

  private RuleAction action;

  public MethodRule() {
    super();
  }

  public MethodRule(String packageName, String className, String name, RuleAction action) {
    super();
    this.packageName = packageName;
    this.className = className;
    this.name = name;
    this.action = action;
  }

  public boolean matches(Method method) {
    if(StringUtils.isNotBlank(packageName) &&
        !StringUtils.equals(packageName, method.getDeclaringClass().getPackageName())) {
      return false;
    }
    if(StringUtils.isNotBlank(className) && !StringUtils.equals(className, method.getDeclaringClass().getSimpleName())) {
      return false;
    }
    if(StringUtils.isNotBlank(name) && !StringUtils.equals(name, method.getName())) {
      return false;
    }
    return true;
  }

  public String getPackageName() {
    return packageName;
  }

  public MethodRule setPackageName(String packageName) {
    this.packageName = packageName;
    return this;
  }

  public String getClassName() {
    return className;
  }

  public MethodRule setClassName(String className) {
    this.className = className;
    return this;
  }

  public String getName() {
    return name;
  }

  public MethodRule setName(String name) {
    this.name = name;
    return this;
  }

  public RuleAction getAction() {
    return action;
  }

  public MethodRule setAction(RuleAction action) {
    this.action = action;
    return this;
  }

  @Override
  public String toString() {
    return "MethodRule [packageName=" + packageName + ", className=" + className + ", name=" + name + ", action="
        + action + "]";
  }

}
