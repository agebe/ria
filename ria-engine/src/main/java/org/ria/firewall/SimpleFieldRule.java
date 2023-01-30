/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria.firewall;

import java.lang.reflect.Field;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class SimpleFieldRule implements FieldRule {

  private Set<FieldAccess> fieldAccess;

  private String packageName;

  private String className;

  private String fieldName;

  private RuleAction action;

  public SimpleFieldRule() {
    super();
  }

  public SimpleFieldRule(Set<FieldAccess> fieldAccess, String packageName, String className, String fieldName,
      RuleAction action) {
    super();
    this.fieldAccess = fieldAccess;
    this.packageName = packageName;
    this.className = className;
    this.fieldName = fieldName;
    this.action = action;
  }

  @Override
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

  public SimpleFieldRule setFieldAccess(Set<FieldAccess> fieldAccess) {
    this.fieldAccess = fieldAccess;
    return this;
  }

  @Override
  public RuleAction getAction() {
    return action;
  }

  public SimpleFieldRule setAction(RuleAction action) {
    this.action = action;
    return this;
  }

  public String getPackageName() {
    return packageName;
  }

  public SimpleFieldRule setPackageName(String packageName) {
    this.packageName = packageName;
    return this;
  }

  public String getClassName() {
    return className;
  }

  public SimpleFieldRule setClassName(String className) {
    this.className = className;
    return this;
  }

  public String getFieldName() {
    return fieldName;
  }

  public SimpleFieldRule setFieldName(String fieldName) {
    this.fieldName = fieldName;
    return this;
  }

}
