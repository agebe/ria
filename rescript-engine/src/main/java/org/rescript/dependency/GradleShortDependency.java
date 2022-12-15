package org.rescript.dependency;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;

public class GradleShortDependency implements Dependency {

  private String group;

  private String artifact;

  private String version;

  public GradleShortDependency(String gradleShort) {
    super();
    String[] split = StringUtils.split(gradleShort, ':');
    if(split.length != 3) {
      throw new ScriptException("gradle short dependencies, wrong format."
          + " requires group:artifact:version but got " + gradleShort);
    }
    group = split[0];
    artifact = split[1];
    version = split[2];
  }

  @Override
  public List<DependencyNode> resolve() {
    // TODO add support for exclusions
    return List.of(new DependencyNode(group, artifact, version, List.of(), false));
  }

  public static boolean isGradleShortFormat(String s) {
    String[] split = StringUtils.split(s, ':');
    return (split.length == 3) &&
        StringUtils.isNotBlank(split[0]) &&
        StringUtils.isNotBlank(split[1]) &&
        StringUtils.isNotBlank(split[2]);
  }

}
