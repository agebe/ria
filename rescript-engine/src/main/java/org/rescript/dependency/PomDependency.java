package org.rescript.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.model.Model;
import org.rescript.ScriptException;
import org.rescript.pom.MavenCoordinates;
import org.rescript.pom.MavenDependencyResolver;
import org.rescript.pom.MavenRepository;

public class PomDependency implements Dependency {

  private DependencyNode node;

  private String gradleShort;

  private String group;

  private String artifact;

  private String version;

  private Repositories repos;

  public PomDependency(DependencyNode node, Repositories repos) {
    super();
    this.node = node;
    this.gradleShort = node.id();
    String[] split = StringUtils.split(gradleShort, ':');
    if(split.length != 3) {
      throw new ScriptException("gradle short dependencies, wrong format."
          + " requires group:artifact:version but got " + gradleShort);
    }
    group = split[0];
    artifact = split[1];
    version = split[2];
    this.repos = repos;
  }

  private Pair<Model, MavenRepository> resolveModel() {
    List<Exception> suppressed = new ArrayList<>();
    List<MavenRepository> r = repos.getRepositoriesOrDefault();
    for(int i=0;i<r.size();i++) {
      try {
        MavenRepository mr = r.get(i);
        var resolver = new MavenDependencyResolver(mr);
        Model model = resolver.resolve(new MavenCoordinates(group, artifact, version));
        return Pair.of(model, mr);
      } catch(Exception e) {
        if(i == r.size() -1) {
          ScriptException exception = new ScriptException("failed to resolve '%s' from all repositories"
              .formatted(gradleShort), e);
          suppressed.forEach(exception::addSuppressed);
          throw exception;
        } else {
          suppressed.add(e);
        }
      }
    }
    throw new ScriptException("'%s' failed to resolve".formatted(gradleShort));
  }

  @Override
  public List<DependencyNode> resolve() {
    try {
      Pair<Model, MavenRepository> p = resolveModel();
      Model model = p.getLeft();
      if(model == null) {
        return List.of();
      }
      node.setRepository(p.getRight());
      node.setPackaging(model.getPackaging());
      if(model.getDependencies() == null) {
        return List.of();
      }
      // FIXME honour excludes
      return model.getDependencies()
          .stream()
          .filter(this::runtimeScope)
          .flatMap(this::resolveImportsAndMap)
          .toList();
    } catch(Exception e) {
      throw new ScriptException("failed to resolve dependency '%s'".formatted(gradleShort), e);
    }
  }

  private boolean runtimeScope(org.apache.maven.model.Dependency d) {
    // keep everything else also empty scope
    return !StringUtils.equalsAnyIgnoreCase(d.getScope(), "provided", "system", "test");
  }

  private Stream<DependencyNode> resolveImportsAndMap(org.apache.maven.model.Dependency d) {
    if(StringUtils.equalsAnyIgnoreCase(d.getScope(), "import")) {
      // FIXME
      throw new ScriptException("import dependency not implemented yet");
    }
    return Stream.of(new DependencyNode(
        d.getGroupId(),
        d.getArtifactId(),
        d.getVersion(),
        exclusions(d),
        d.isOptional()));
  }

  private List<Exclusion> exclusions(org.apache.maven.model.Dependency d) {
    if(d.getExclusions() != null) {
      return d.getExclusions()
          .stream()
          .map(e -> new Exclusion(e.getGroupId(), e.getArtifactId()))
          .toList();
    } else {
      return List.of();
    }
  }

}
