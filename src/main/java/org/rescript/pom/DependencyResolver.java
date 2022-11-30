package org.rescript.pom;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.rescript.ScriptException;

public class DependencyResolver {

  private Map<String, Dependency> parentDependencies;

  private PropertyResolver properties;

//all parents merged
  public DependencyResolver(Model model, Model parent) {
    super();
    parentDependencies = dependencies(parent);
    properties = new PropertyResolver(model, parent);
  }

  private Map<String, Dependency> dependencies(Model model) {
    if((model == null) ||
        (model.getDependencyManagement() == null) ||
        (model.getDependencyManagement().getDependencies() == null)) {
      return Map.of();
    } else {
      return model.getDependencyManagement()
          .getDependencies()
          .stream()
          .collect(Collectors.toMap(this::getId, d -> d));
    }
  }

  private String getId(Dependency d) {
    return d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getType() + ":" + d.getClassifier();
  }

  public Dependency resolve(Dependency d) {
    String id = getId(d);
    Dependency dp = parentDependencies.get(id);
    if(StringUtils.isBlank(d.getVersion())) {
      if(dp == null) {
        throw new ScriptException("can't resolve version of " + d);
      }
      d.setVersion(dp.getVersion());
    }
    d.setVersion(properties.resolve(d.getVersion()));
    if(dp != null) {
      if(StringUtils.isBlank(d.getScope())) {
        d.setScope(dp.getScope());
      }
      if(StringUtils.isBlank(d.getOptional())) {
        d.setOptional(dp.getOptional());
      }
      if(dp.getExclusions() != null) {
        if(d.getExclusions() != null) {
          d.setExclusions(dp.getExclusions());
        } else {
          // copying all exclusions from the parent could produce duplicates in the resolved exclusions list
          // but does it matter?
          d.getExclusions().addAll(dp.getExclusions());
        }
      }
    }
    return d;
  }

}
