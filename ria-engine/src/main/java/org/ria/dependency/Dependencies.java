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
import java.util.function.Consumer;

import org.ria.ScriptException;
import org.ria.run.ScriptContext;

public class Dependencies implements Consumer<Object> {

  private List<Dependency> dependencies = new ArrayList<>();

  private ScriptContext ctx;

  public Dependencies(ScriptContext ctx) {
    super();
    this.ctx = ctx;
  }

  public Dependencies(List<Dependency> dependencies) {
    this.dependencies.addAll(dependencies);
  }

  @Override
  public void accept(Object o) {
    if(o instanceof Dependency d) {
      dependencies.add(d);
    } else if(o instanceof String s) {
      dependencies.add(new MultiFormatDependency(s, ctx));
    } else {
      throw new ScriptException("unsupported dependency type " + o);
    }
  }

  public List<Dependency> getDependencies() {
    return dependencies;
  }

  public void setDependencies(List<Dependency> dependencies) {
    this.dependencies = dependencies;
  }

  public boolean hasDependencies() {
    return (dependencies != null) && (!dependencies.isEmpty());
  }

}
