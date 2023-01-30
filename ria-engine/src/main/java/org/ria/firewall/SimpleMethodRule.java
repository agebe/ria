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

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

public class SimpleMethodRule implements MethodRule {

  private String packageName;

  private String className;

  private String name;

  private RuleAction action;

  public SimpleMethodRule() {
    super();
  }

  public SimpleMethodRule(String packageName, String className, String name, RuleAction action) {
    super();
    this.packageName = packageName;
    this.className = className;
    this.name = name;
    this.action = action;
  }

  @Override
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

  public SimpleMethodRule setPackageName(String packageName) {
    this.packageName = packageName;
    return this;
  }

  public String getClassName() {
    return className;
  }

  public SimpleMethodRule setClassName(String className) {
    this.className = className;
    return this;
  }

  public String getName() {
    return name;
  }

  public SimpleMethodRule setName(String name) {
    this.name = name;
    return this;
  }

  @Override
  public RuleAction getAction() {
    return action;
  }

  public SimpleMethodRule setAction(RuleAction action) {
    this.action = action;
    return this;
  }

  @Override
  public String toString() {
    return "MethodRule [packageName=" + packageName + ", className=" + className + ", name=" + name + ", action="
        + action + "]";
  }

}
