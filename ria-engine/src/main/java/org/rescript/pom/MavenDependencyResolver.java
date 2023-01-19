package org.rescript.pom;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

public class MavenDependencyResolver {

  private MavenRepository repo;

  public MavenDependencyResolver(MavenRepository repo) {
    this.repo = repo;
  }

  public Model resolve(MavenCoordinates coord) throws Exception {
    PomResolver pomResolver = new PomResolver(repo);
    var pair = new PomResolver(repo).load(coord);
    Model model = pair.getLeft();
    Model parent = pair.getRight();
    DependencyResolver dResolver = new DependencyResolver(model, parent, pomResolver);
    List<Dependency> resolved = model.getDependencies()
        .stream()
        .map(d -> dResolver.resolve(d))
        .toList();
    model.setDependencies(resolved);
//    MavenXpp3Writer writer = new MavenXpp3Writer();
//    writer.write(System.out, model);
    // TODO only keep dependencies needed for runtime
    return model;
  }

}
