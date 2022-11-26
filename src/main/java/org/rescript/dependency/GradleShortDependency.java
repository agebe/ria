package org.rescript.dependency;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class GradleShortDependency implements Dependency {

  private String gradleShort;

  public GradleShortDependency(String gradleShort) {
    super();
    this.gradleShort = gradleShort;
  }

  @Override
  public List<File> resolve() {
    // TODO
    return Collections.emptyList();
  }

}
