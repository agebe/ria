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
package org.ria.statement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ria.Options;
import org.ria.dependency.Dependencies;
import org.ria.dependency.Dependency;
import org.ria.dependency.FileDependency;
import org.ria.dependency.FileTreeDependency;
import org.ria.dependency.Repositories;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class HeaderEnterStatement extends AbstractStatement {

  public static final String OPTIONS = "options";
  public static final String DEPENDENCIES = "dependencies";
  public static final String REPOSITORIES = "repositories";

  private Repositories repos;

  private Options options;

  private List<File> classpaths = new ArrayList<>();

  public HeaderEnterStatement(
      int lineNumber,
      String defaultMavenRepo,
      File cacheBase,
      Options options,
      List<File> classpath) {
    super(lineNumber);
    this.repos = new Repositories(defaultMavenRepo, cacheBase);
    this.options = options;
    if(classpath != null) {
      classpath.forEach(cp -> {
        if(cp.exists()) {
          classpaths.add(cp);
        }
      });
    }
  }

  private Dependency fromFile(File f, ScriptContext ctx) {
    if(f == null) {
      return null;
    }
    if(f.isFile()) {
      return new FileDependency(f.getPath(), ctx);
    } else if(f.isDirectory()) {
      return new FileTreeDependency(f.getPath(), ctx);
    } else {
      return null;
    }
  }

  @Override
  public void execute(ScriptContext ctx) {
    if(!ctx.getSymbols().getScriptSymbols().isDefined(OPTIONS)) {
      ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot(
          OPTIONS, Value.of(options));
    }
    if(!ctx.getSymbols().getScriptSymbols().isDefined(DEPENDENCIES)) {
      Dependencies dependencies = new Dependencies(ctx);
      classpaths.forEach(cp -> {
        Dependency d = fromFile(cp, ctx);
        if(d != null) {
          dependencies.accept(d);
        }
      });
      ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot(
          DEPENDENCIES, Value.of(dependencies));
    }
    if(!ctx.getSymbols().getScriptSymbols().isDefined(REPOSITORIES)) {
      ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot(
          REPOSITORIES, Value.of(repos));
    }
  }

}
