package org.rescript.pom;

import org.apache.maven.model.Model;

public class MavenDependencyResolver {

  private MavenRepository repo;

  public MavenDependencyResolver(MavenRepository repo) {
    this.repo = repo;
  }

  public Model resolve(MavenCoordinates coord) throws Exception {
    Model model = new PomResolver(repo).resolve(coord);
//    MavenXpp3Writer writer = new MavenXpp3Writer();
//    writer.write(System.out, model);
    // TODO only keep dependencies needed for runtime
    return model;
  }

}
