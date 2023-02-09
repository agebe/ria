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

import java.util.List;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;

public class MultiFormatDependency implements Dependency {

  private String dependency;

  private ScriptContext ctx;

  public MultiFormatDependency(String dependency, ScriptContext ctx) {
    super();
    this.dependency = dependency;
    this.ctx = ctx;
  }

  @Override
  public List<DependencyNode> resolve() {
    if(GradleShortDependency.isGradleShortFormat(dependency)) {
      return new GradleShortDependency(dependency).resolve();
    } else if(FileTreeDependency.isFileTreeDependency(dependency)) {
      return new FileTreeDependency(dependency).resolve();
    } else if(FileDependency.isFileDependency(dependency, ctx)) {
      return new FileDependency(dependency, ctx).resolve();
    } else if(MavenDependency.isMavenFormat(dependency)) {
      return new MavenDependency(dependency).resolve();
    } else {
      throw new ScriptException("unsupported dependency format '%s'".formatted(dependency));
    }
  }

}
