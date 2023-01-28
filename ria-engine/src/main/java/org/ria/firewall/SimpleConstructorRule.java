package org.ria.firewall;

import java.lang.reflect.Constructor;

import org.apache.commons.lang3.StringUtils;

public class SimpleConstructorRule implements ConstructorRule {

  private String packageName;

  private String className;

  private RuleAction action;

  public SimpleConstructorRule() {
    super();
  }

  public SimpleConstructorRule(String packageName, String className, RuleAction action) {
    super();
    this.packageName = packageName;
    this.className = className;
    this.action = action;
  }

  @Override
  public boolean matches(Constructor<?> c) {
    if(StringUtils.isNotBlank(packageName) &&
        !StringUtils.equals(packageName, c.getDeclaringClass().getPackageName())) {
      return false;
    }
    if(StringUtils.isNotBlank(className) && !StringUtils.equals(className, c.getDeclaringClass().getSimpleName())) {
      return false;
    }
    return true;
  }

  public String getPackageName() {
    return packageName;
  }

  public SimpleConstructorRule setPackageName(String packageName) {
    this.packageName = packageName;
    return this;
  }

  public String getClassName() {
    return className;
  }

  public SimpleConstructorRule setClassName(String className) {
    this.className = className;
    return this;
  }

  @Override
  public RuleAction getAction() {
    return action;
  }

  public SimpleConstructorRule setAction(RuleAction action) {
    this.action = action;
    return this;
  }

}
