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
