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

public class FileTreeDependency implements Dependency {

  private String baseDir;

  private ScriptContext ctx;

  public FileTreeDependency(String baseDir, ScriptContext ctx) {
    super();
    this.baseDir = baseDir;
    this.ctx = ctx;
  }

  @Override
  public List<DependencyNode> resolve() {
    File base = resolve(baseDir, ctx);
    if(!base.exists()) {
      throw new ScriptException("file tree base not found, " + base.getAbsolutePath());
    }
    if(!base.isDirectory()) {
      throw new ScriptException("file tree base need to be a directory, " + base.getAbsolutePath());
    }
    return List.of(new DependencyNode(base));
  }

  private static File resolve(String s, ScriptContext ctx) {
    if(new File(s).isAbsolute()) {
      return new File(s);
    } else {
      File scriptDirectory = ctx.getScriptDirectory();
      return scriptDirectory!=null?new File(scriptDirectory, s):null;
    }
  }

  public static boolean isFileTreeDependency(String s, ScriptContext ctx) {
    try {
      File f = resolve(s, ctx);
      return f!=null?f.isDirectory():false;
    } catch(Exception e) {
      return false;
    }
  }

}
