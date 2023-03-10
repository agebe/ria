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
package org.ria.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ria.ScriptException;
import org.ria.pom.MavenCoordinates;
import org.ria.pom.MavenRepository;

public class DependencyResolver {

  private Repositories repositories;

  public DependencyResolver(Repositories repositories) {
    super();
    this.repositories = repositories;
  }

  public DependencyNode resolveAll(Dependencies dependencies) {
    return resolveDependencies(dependencies);
  }

  public List<File> directDependencies(Dependencies dependencies) {
    return List.of();
  }

  private DependencyNode resolveDependencies(Dependencies dependencies) {
    final DependencyNode root = new DependencyNode();
    dependencies.getDependencies().forEach(dep -> root.addChildren(dep.resolve()));
    // TODO add support for variables and variable replacements in the dependencies e.g. for versions
    // make sure to resolve different versions of the same group:artifact
    // dependency node might require a type so jar and pom dependencies can be distinguished
    // https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#importing-dependencies
    // transitive dependencies
    // https://en.wikipedia.org/wiki/Breadth-first_search
    LinkedList<DependencyNode> queue = new LinkedList<>();
    // store group:artifact id in explored so we don't add the same library in different versions
    // as described below, first dependency wins
    // https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html
    Map<String, String> explored = new HashMap<>();
    List<DependencyNode> managedDependencies = new ArrayList<>();
    root.asList()
    .stream()
    .filter(DependencyNode::isManaged)
    .forEach(d -> {
      String gaId = d.groupArtifactId();
      if(!explored.containsKey(gaId)) {
        explored.put(gaId, d.getVersion());
        queue.add(d);
      }
    });
    // ignore the optional flag on all direct dependencies
    // also direct dependency can not be excluded
    managedDependencies.addAll(queue);
//    managedDependencies.forEach(System.out::println);
    while(!queue.isEmpty()) {
      DependencyNode v = queue.remove();
      PomDependency resolver = new PomDependency(v, repositories);
      List<DependencyNode> transitiveDependencies = resolver.resolve();
      v.addChildren(transitiveDependencies);
      for(DependencyNode transitive : transitiveDependencies) {
        if(transitive.isOptional()) {
//          System.out.println("ignoring optional transitive dependeny '%s' from this path ".formatted(transitive.id()) + "TODO");
        } else if(v.isExcluded(transitive)) {
//          System.out.println("transitive dependeny '%s' is excluded from this path ".formatted(transitive.id()) + "TODO");
        } else {
          if(!explored.containsKey(transitive.groupArtifactId())) {
            explored.put(transitive.groupArtifactId(), transitive.getVersion());
            queue.add(transitive);
            managedDependencies.add(transitive);
          } else {
            // in this case even if a dependency is already explored we have to go down the dependency path anyway
            // because it may not contain the same exclusions.
            // make sure to use the same version as the explored dependency though
            // i think the dependency node needs to be merged. id from the explored, but exclusion list from the current node
            DependencyNode n2 = new DependencyNode(
                transitive.getGroup(),
                transitive.getArtifact(),
                explored.get(transitive.groupArtifactId()),
                transitive.getExclusions(),
                false);
            v.addChild(n2);
            queue.add(n2);
          }
        }
      }
    }
    root.asList().stream().forEachOrdered(d -> toJar(d, repositories));
    return root;
  }

  private File toJar(DependencyNode node, Repositories repos) {
    if(node.getFile() != null) {
      return node.getFile();
    }
    if(StringUtils.isBlank(node.getGroup())) {
      return null;
    }
    if(!StringUtils.equalsAnyIgnoreCase(node.getPackaging(), "jar", "bundle")) {
      return null;
    }
    MavenCoordinates coord = new MavenCoordinates(node.getGroup(), node.getArtifact(), node.getVersion());
    List<Exception> suppressed = new ArrayList<>();
    List<MavenRepository> r = repos.getRepositoriesOrDefault();
    // TODO if the node has the repository set on it (from which the pom was downloaded) only try that repo
    for(int i=0;i<r.size();i++) {
      try {
        MavenRepository mr = r.get(i);
        File f = mr.fetchFile(coord, ".jar");
        node.setFile(f);
        return f;
      } catch(Exception e) {
        if(i == r.size() -1) {
          ScriptException exception = new ScriptException("failed to fetch jar file from remote '%s' from all repositories"
              .formatted(coord), e);
          suppressed.forEach(exception::addSuppressed);
          throw exception;
        } else {
          suppressed.add(e);
        }
      }
    }
    throw new ScriptException("failed to fetch jar file for dependency " + coord);
  }

}

