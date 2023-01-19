package org.rescript.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.rescript.ScriptException;

public class Dependencies implements Consumer<Object> {

  private List<Dependency> dependencies = new ArrayList<>();

  @Override
  public void accept(Object o) {
    if(o instanceof Dependency d) {
      dependencies.add(d);
    } else if(o instanceof String s) {
      dependencies.add(new MultiFormatDependency(s));
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

}
