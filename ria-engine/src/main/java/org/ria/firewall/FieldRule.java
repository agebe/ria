package org.ria.firewall;

import java.lang.reflect.Field;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class FieldRule {

  private Set<FieldAccess> fieldAccess;

  private String packageName;

  private String className;

  private String fieldName;

  private RuleAction action;

  public FieldRule() {
    super();
  }

  public boolean matches(Field field, FieldAccess fieldAccess) {
    if((this.fieldAccess != null) && !this.fieldAccess.contains(fieldAccess)) {
      return false;
    }
    if(StringUtils.isNotBlank(packageName) &&
        !StringUtils.equals(packageName, field.getDeclaringClass().getPackageName())) {
      return false;
    }
    if(StringUtils.isNotBlank(className) && !StringUtils.equals(className, field.getDeclaringClass().getName())) {
      return false;
    }
    if(StringUtils.isNotBlank(fieldName) && !StringUtils.equals(fieldName, field.getName())) {
      return false;
    }
    return true;
  }

  public Set<FieldAccess> getFieldAccess() {
    return fieldAccess;
  }

  public FieldRule setFieldAccess(Set<FieldAccess> fieldAccess) {
    this.fieldAccess = fieldAccess;
    return this;
  }

  public RuleAction getAction() {
    return action;
  }

  public FieldRule setAction(RuleAction action) {
    this.action = action;
    return this;
  }

  public String getPackageName() {
    return packageName;
  }

  public FieldRule setPackageName(String packageName) {
    this.packageName = packageName;
    return this;
  }

  public String getClassName() {
    return className;
  }

  public FieldRule setClassName(String className) {
    this.className = className;
    return this;
  }

  public String getFieldName() {
    return fieldName;
  }

  public FieldRule setFieldName(String fieldName) {
    this.fieldName = fieldName;
    return this;
  }

}
