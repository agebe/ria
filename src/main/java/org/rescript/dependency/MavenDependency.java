package org.rescript.dependency;

import java.io.StringReader;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.rescript.ScriptException;

public class MavenDependency implements Dependency {

  // https://maven.apache.org/guides/introduction/introduction-to-the-pom.html#Minimal_POM
  private static final String SHORT_POM_START = """
      <project>
      <modelVersion>4.0.0</modelVersion>
      <groupId>com.mycompany.app</groupId>
      <artifactId>my-app</artifactId>
      <version>1</version>
      """;

  private String dependency;

  public MavenDependency(String dependency) {
    super();
    this.dependency = StringUtils.strip(dependency);
  }

  private String pomString() {
    if(dependency.startsWith("<dependencies>")) {
      return SHORT_POM_START + dependency + "</project>";
    } else if(dependency.startsWith("<dependency>")) {
      return SHORT_POM_START + "<dependencies>" + dependency + "</dependencies></project>";
    } else {
      throw new ScriptException("wrong dependency format " + dependency);
    }
  }

  private Exclusion toExclusion(org.apache.maven.model.Exclusion e) {
    return new Exclusion(e.getGroupId(), e.getArtifactId());
  }

  private DependencyNode toNode(org.apache.maven.model.Dependency d) {
    List<Exclusion> exclusions = d.getExclusions()!=null?
        d.getExclusions().stream().map(this::toExclusion).toList()
        :List.of();
    // direct dependencies are not optional
    return new DependencyNode(d.getGroupId(), d.getArtifactId(), d.getVersion(), exclusions, false);
  }

  @Override
  public List<DependencyNode> resolve() {
    String pomString = pomString();
    try {
      MavenXpp3Reader reader = new MavenXpp3Reader();
      Model model = reader.read(new StringReader(pomString));
      return model.getDependencies().stream().map(this::toNode).toList();
    } catch(Exception e) {
      throw new ScriptException("failed to parse maven dependency " + pomString);
    }
  }

  public static boolean isMavenFormat(String s) {
    String stripped = StringUtils.strip(s);
    return StringUtils.startsWith(stripped, "<dependencies>") ||
        StringUtils.startsWith(stripped, "<dependency>");
  }

}
