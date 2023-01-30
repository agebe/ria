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

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

public class MavenDependencyResolver {

  private MavenRepository repo;

  public MavenDependencyResolver(MavenRepository repo) {
    this.repo = repo;
  }

  public Model resolve(MavenCoordinates coord) throws Exception {
    PomResolver pomResolver = new PomResolver(repo);
    var pair = new PomResolver(repo).load(coord);
    Model model = pair.getLeft();
    Model parent = pair.getRight();
    DependencyResolver dResolver = new DependencyResolver(model, parent, pomResolver);
    List<Dependency> resolved = model.getDependencies()
        .stream()
        .map(d -> dResolver.resolve(d))
        .toList();
    model.setDependencies(resolved);
//    MavenXpp3Writer writer = new MavenXpp3Writer();
//    writer.write(System.out, model);
    // TODO only keep dependencies needed for runtime
    return model;
  }

}
