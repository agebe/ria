package org.ria.firewall;

import java.lang.reflect.Constructor;

import org.apache.commons.lang3.StringUtils;

public class ConstructorRule {

  private String packageName;

  private String className;

  private RuleAction action;

  public ConstructorRule() {
    super();
  }

  public ConstructorRule(String packageName, String className, RuleAction action) {
    super();
    this.packageName = packageName;
    this.className = className;
    this.action = action;
  }

  public boolean matches(Constructor<?> c) {
    if(StringUtils.isNotBlank(packageName) &&
        !StringUtils.equals(packageName, c.getDeclaringClass().getPackageName())) {
      return false;
    }
    if(StringUtils.isNotBlank(className) && !StringUtils.equals(className, c.getDeclaringClass().getName())) {
      return false;
    }
    return true;
  }

  public String getPackageName() {
    return packageName;
  }

  public ConstructorRule setPackageName(String packageName) {
    this.packageName = packageName;
    return this;
  }

  public String getClassName() {
    return className;
  }

  public ConstructorRule setClassName(String className) {
    this.className = className;
    return this;
  }

  public RuleAction getAction() {
    return action;
  }

  public ConstructorRule setAction(RuleAction action) {
    this.action = action;
    return this;
  }

}
