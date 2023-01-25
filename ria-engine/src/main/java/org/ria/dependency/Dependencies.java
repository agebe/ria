package org.ria.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.ria.ScriptException;

public class Dependencies implements Consumer<Object> {

  private List<Dependency> dependencies = new ArrayList<>();

  public Dependencies() {
    super();
  }

  public Dependencies(List<Dependency> dependencies) {
    this.dependencies.addAll(dependencies);
  }

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
