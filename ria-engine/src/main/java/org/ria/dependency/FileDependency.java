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
import java.util.List;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;

public class FileDependency implements Dependency {

  private String file;

  private ScriptContext ctx;

  public FileDependency(String file, ScriptContext ctx) {
    super();
    this.file = file;
    this.ctx = ctx;
  }

  @Override
  public List<DependencyNode> resolve() {
    File f = resolve(file, ctx);
    if(!f.exists()) {
      throw new ScriptException("file dependency not found, " + f.getAbsolutePath());
    }
    if(!f.isFile()) {
      throw new ScriptException("file dependency '%s' is not a file".formatted(f.getAbsolutePath()));
    }
    return List.of(new DependencyNode(f));
  }

  private static File resolve(String s, ScriptContext ctx) {
    if(new File(s).isAbsolute()) {
      return new File(s);
    } else {
      File scriptDirectory = ctx.getScriptDirectory();
      return scriptDirectory!=null?new File(scriptDirectory, s):null;
    }
  }

  public static boolean isFileDependency(String s, ScriptContext ctx) {
    try {
      File f = resolve(s, ctx);
      return f!=null?f.isFile():false;
    } catch(Exception e) {
      return false;
    }
  }

}
