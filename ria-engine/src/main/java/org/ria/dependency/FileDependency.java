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

public class FileDependency implements Dependency {

  private String file;

  public FileDependency(String file) {
    super();
    this.file = file;
  }

  @Override
  public List<DependencyNode> resolve() {
    File f = new File(file);
    if(!f.exists()) {
      throw new ScriptException("file dependency not found, " + f.getAbsolutePath());
    }
    if(!f.isFile()) {
      throw new ScriptException("file dependency '%s' is not a normal file".formatted(f.getAbsolutePath()));
    }
    return List.of(new DependencyNode(f));
  }

  public static boolean isFileDependency(String s) {
    try {
      File f = new File(s);
      return f.isFile();
    } catch(Exception e) {
      return false;
    }
  }

}
