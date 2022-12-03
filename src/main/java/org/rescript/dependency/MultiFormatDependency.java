package org.rescript.dependency;

import java.util.List;

import org.rescript.ScriptException;

public class MultiFormatDependency implements Dependency {

  private String dependency;

  public MultiFormatDependency(String dependency) {
    super();
    this.dependency = dependency;
  }

  @Override
  public List<DependencyNode> resolve() {
    if(GradleShortDependency.isGradleShortFormat(dependency)) {
      return new GradleShortDependency(dependency).resolve();
    } else if(FileTreeDependency.isFileTreeDependency(dependency)) {
      return new FileTreeDependency(dependency).resolve();
    } else if(FileDependency.isFileDependency(dependency)) {
      return new FileDependency(dependency).resolve();
    } else if(MavenDependency.isMavenFormat(dependency)) {
      return new MavenDependency(dependency).resolve();
    } else {
      throw new ScriptException("unsupported dependeny format '%s'".formatted(dependency));
    }
  }

}
