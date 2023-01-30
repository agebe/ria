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
package org.ria.pom;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;
import org.ria.ScriptException;

public class PropertyResolver {

  private Map<String, String> pMap;

  public PropertyResolver(Model model, Model parent) {
    super();
    pMap = new HashMap<>();
    if(parent != null) {
      if(parent.getProperties() != null) {
        parent.getProperties().forEach((k,v) -> pMap.put(k.toString(), v.toString()));
      }
      if(StringUtils.isNotBlank(parent.getVersion())) {
        pMap.put("project.version", parent.getVersion());
      }
    }
    if(model.getProperties() != null) {
      model.getProperties().forEach((k,v) -> pMap.put(k.toString(), v.toString()));
    }
    if(StringUtils.isNotBlank(model.getVersion())) {
      pMap.put("project.version", model.getVersion());
    }
    if(StringUtils.isNotBlank(model.getGroupId())) {
      pMap.put("project.groupId", model.getGroupId());
    } else if(StringUtils.isNotBlank(model.getParent().getGroupId())) {
      pMap.put("project.groupId", model.getParent().getGroupId());
    }
  }

  public String resolve(String s) {
    String property = StringUtils.substringBetween(s, "${", "}");
    if(StringUtils.isBlank(property)) {
      return s;
    }
    String v = pMap.get(property);
    if(StringUtils.isBlank(v)) {
      throw new ScriptException("could not resolve property '%s', not found".formatted(property));
    }
    return resolve(StringUtils.replace(s, "${"+property+"}", v));
  }

}
