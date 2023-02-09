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

import org.ria.Options;
import org.ria.dependency.Dependencies;
import org.ria.dependency.Repositories;
import org.ria.run.ScriptContext;
import org.ria.value.Value;

public class HeaderEnterStatement extends AbstractStatement {

  public static final String OPTIONS = "options";
  public static final String DEPENDENCIES = "dependencies";
  public static final String REPOSITORIES = "repositories";

  private Repositories repos;

  private Options options;

  public HeaderEnterStatement(int lineNumber, String defaultMavenRepo, File cacheBase, Options options) {
    super(lineNumber);
    this.repos = new Repositories(defaultMavenRepo, cacheBase);
    this.options = options;
  }

  @Override
  public void execute(ScriptContext ctx) {
    if(!ctx.getSymbols().getScriptSymbols().isDefined(OPTIONS)) {
      ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot(
          OPTIONS, Value.of(options));
    }
    if(!ctx.getSymbols().getScriptSymbols().isDefined(DEPENDENCIES)) {
      ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot(
          DEPENDENCIES, Value.of(new Dependencies(ctx)));
    }
    if(!ctx.getSymbols().getScriptSymbols().isDefined(REPOSITORIES)) {
      ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot(
          REPOSITORIES, Value.of(repos));
    }
  }

}
