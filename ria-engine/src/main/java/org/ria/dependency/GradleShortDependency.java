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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ria.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GradleShortDependency implements Dependency {

  private static final Logger log = LoggerFactory.getLogger(GradleShortDependency.class);

  private String group;

  private String artifact;

  private String version;

  private List<Exclusion> excludes = new ArrayList<>();

  public GradleShortDependency(String gradleShort) {
    super();
    String[] split = StringUtils.split(gradleShort, ':');
    if(split.length != 3) {
      throw new ScriptException("gradle short dependencies, wrong format."
          + " requires group:artifact:version but got " + gradleShort);
    }
    group = split[0];
    artifact = split[1];
    version = split[2];
  }

  public void exclude(String exclude) {
    String[] s = StringUtils.split(exclude, ':');
    if(s == null) {
      return;
    }
    if(s.length == 2) {
      excludes.add(new Exclusion(s[0], s[1]));
    } else {
      log.warn("wrong gradle exclude format '{}', needs to be <group>:<artifact>", exclude);
    }
  }

  @Override
  public List<DependencyNode> resolve() {
    // TODO add support for exclusions
    return List.of(new DependencyNode(group, artifact, version, excludes, false));
  }

  public static boolean isGradleShortFormat(String s) {
    String[] split = StringUtils.split(s, ':');
    return (split.length == 3) &&
        StringUtils.isNotBlank(split[0]) &&
        StringUtils.isNotBlank(split[1]) &&
        StringUtils.isNotBlank(split[2]);
  }

}
