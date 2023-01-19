package org.ria.pom;

public record MavenCoordinates(
    String group,
    String artifact,
    String version) {

  @Override
  public String toString() {
    return group + ":" + artifact + ":" + version;
  }

}
