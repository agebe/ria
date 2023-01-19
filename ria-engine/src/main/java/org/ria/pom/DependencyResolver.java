package org.ria.pom;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.ria.ScriptException;

public class DependencyResolver {

  private Map<String, Dependency> dependencyManagement;

  private PropertyResolver properties;

  private PomResolver pomResolver;

//all parents merged
  public DependencyResolver(Model model, Model parent, PomResolver pomResolver) {
    super();
    this.pomResolver = pomResolver;
    dependencyManagement = dependenciesResolveImports(model, parent);
//    System.out.println(dependencyManagement);
    properties = new PropertyResolver(model, parent);
  }

  private Map<String, Dependency> dependencyManagement(Model model) {
    if((model == null) ||
        (model.getDependencyManagement() == null) ||
        (model.getDependencyManagement().getDependencies() == null)) {
      return Map.of();
    } else {
      var grouped = model.getDependencyManagement()
          .getDependencies()
          .stream()
          .collect(Collectors.groupingBy(this::getId));
      return grouped.entrySet()
          .stream()
          .collect(Collectors.toMap(
              me -> me.getKey(),
              me -> me.getValue().get(0)));
    }
  }

  private Dependency merge(Dependency m, Dependency p) {
    if((m != null) && (p != null)) {
      // not sure if the model and parent dependency has to be merged!?
      // for now simply prefer the dependency from the model without merging
      return m;
    } else if(m != null) {
      return m;
    } else if(p != null) {
      return p;
    } else {
      throw new ScriptException("both model and parent dependency is null from dependency management");
    }
  }

  private Stream<Map.Entry<String, Dependency>> resolveImport(Map.Entry<String, Dependency> me) {
    Dependency d = me.getValue();
    if(StringUtils.equalsIgnoreCase("pom", d.getType())) {
//      System.out.println("resolve import for " + d);
      try {
        var pair = pomResolver.load(new MavenCoordinates(d.getGroupId(), d.getArtifactId(), d.getVersion()));
        return dependenciesResolveImports(pair.getLeft(), pair.getRight()).entrySet().stream();
      } catch (Exception e) {
        throw new ScriptException("failed to resolve imports for " + d, e);
      }
    } else {
      return Stream.of(me);
    }
  }

  private Map.Entry<String, Dependency> resolveProperties(PropertyResolver p, Map.Entry<String, Dependency> me) {
    Dependency d = me.getValue();
    d.setVersion(p.resolve(d.getVersion()));
    d.setGroupId(p.resolve(d.getGroupId()));
    return me;
  }

  private Map<String, Dependency> dependenciesResolveImports(Model model, Model parent) {
    Map<String, Dependency> dependencyManagement = dependencies(model, parent);
    PropertyResolver p = new PropertyResolver(model, parent);
    // this is to avoid duplicate keys exception
    var grouped = dependencyManagement
        .entrySet()
        .stream()
        .map(me -> resolveProperties(p, me))
        .flatMap(this::resolveImport)
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(me -> me.getKey()));
    return grouped.entrySet()
        .stream()
        .collect(Collectors.toMap(me -> me.getKey(), me -> me.getValue().get(0).getValue()));
  }

  private Map<String, Dependency> dependencies(Model model, Model parent) {
    var modelDm = dependencyManagement(model);
    var parentDm = dependencyManagement(parent);
//    System.out.println(modelDm);
//    System.out.println(parentDm);
    var keys = new LinkedHashSet<String>();
    keys.addAll(modelDm.keySet());
    keys.addAll(parentDm.keySet());
    return keys.stream()
        .collect(Collectors.toMap(k -> k, k -> merge(modelDm.get(k), parentDm.get(k))));
  }

  private String getId(Dependency d) {
    return d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getType() + ":" + d.getClassifier();
  }

  public Dependency resolve(Dependency d) {
    String id = getId(d);
    Dependency dm = dependencyManagement.get(id);
    if(StringUtils.isBlank(d.getVersion())) {
      
      if(dm == null) {
        throw new ScriptException("can't resolve version of " + d);
      }
      d.setVersion(dm.getVersion());
    }
    d.setVersion(properties.resolve(d.getVersion()));
    d.setGroupId(properties.resolve(d.getGroupId()));
    if(dm != null) {
      if(StringUtils.isBlank(d.getScope())) {
        d.setScope(dm.getScope());
      }
      if(StringUtils.isBlank(d.getOptional())) {
        d.setOptional(dm.getOptional());
      }
      if(dm.getExclusions() != null) {
        if(d.getExclusions() != null) {
          d.setExclusions(dm.getExclusions());
        } else {
          // copying all exclusions from the parent could produce duplicates in the resolved exclusions list
          // but does it matter?
          d.getExclusions().addAll(dm.getExclusions());
        }
      }
    }
    return d;
  }

}
