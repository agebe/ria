package org.rescript.pom;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;
import org.rescript.ScriptException;

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
