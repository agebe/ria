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

import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.merge.ModelMerger;

public class PomResolver {

  private MavenRepository repository;

  public PomResolver(MavenRepository repository) {
    super();
    this.repository = repository;
  }

  public Pair<Model, Model> load(MavenCoordinates coord) throws Exception {
    String pomString = repository.getFile(coord, ".pom");
    MavenXpp3Reader reader = new MavenXpp3Reader();
    Model model = reader.read(new StringReader(pomString));
    MavenCoordinates parentCoord = parentCoords(model);
    Model parent = parentCoord!=null?resolveMergeRecursive(parentCoord):null;
    return Pair.of(model, parent);
  }

  private Model resolveMergeRecursive(MavenCoordinates coord) throws Exception {
    String pomString = repository.getFile(coord, ".pom");
    MavenXpp3Reader reader = new MavenXpp3Reader();
    Model model = reader.read(new StringReader(pomString));
    MavenCoordinates parentCoord = parentCoords(model);
    if(parentCoord != null) {
      Model parent = resolveMergeRecursive(parentCoord);
      new ModelMerger().merge(model, parent, false, null);
      return model;
    } else {
      return model;
    }
  }

  private MavenCoordinates parentCoords(Model model) {
    Parent p = model.getParent();
    if(p == null) {
      return null;
    }
    String g = p.getGroupId();
    String a = p.getArtifactId();
    String v = p.getVersion();
    if(StringUtils.isNotBlank(g) && StringUtils.isNotBlank(a) && StringUtils.isNotBlank(v)) {
      return new MavenCoordinates(
          model.getParent().getGroupId(),
          model.getParent().getArtifactId(),
          model.getParent().getVersion());
    } else {
      return null;
    }
  }

}
