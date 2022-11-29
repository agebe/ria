package org.rescript.pom;

import java.io.StringReader;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.merge.ModelMerger;

public class PomResolver {

  private MavenRepository repository;

  public PomResolver(MavenRepository repository) {
    super();
    this.repository = repository;
  }

  public Model resolve(MavenCoordinates coord) throws Exception {
    //org.eclipse.jetty.aggregate:jetty-all:9.4.49.v20220914
    String pomString = repository.getFile(coord, ".pom");
    MavenXpp3Reader reader = new MavenXpp3Reader();
    Model model = reader.read(new StringReader(pomString));
    MavenCoordinates parentCoord = parentCoords(model);
    Model parent = parentCoord!=null?resolveMergeRecursive(parentCoord):null;
//    if(parent != null) {
//      MavenXpp3Writer writer = new MavenXpp3Writer();
//      writer.write(System.out, parent);
//    }
    DependencyResolver dResolver = new DependencyResolver(model, parent);
    List<Dependency> resolved = model.getDependencies()
        .stream()
        .map(d -> dResolver.resolve(d))
        .toList();
    model.setDependencies(resolved);
    return model;
  }

  private Model resolveMergeRecursive(MavenCoordinates coord) throws Exception {
    String pomString = repository.getFile(coord, ".pom");
    MavenXpp3Reader reader = new MavenXpp3Reader();
    Model model = reader.read(new StringReader(pomString));
    MavenCoordinates parentCoord = parentCoords(model);
    if(parentCoord != null) {
      Model parent = resolveMergeRecursive(parentCoord);
      new ModelMerger().merge(model, parent, false, null);
      return model;
    } else {
      return model;
    }
  }

  private MavenCoordinates parentCoords(Model model) {
    Parent p = model.getParent();
    if(p == null) {
      return null;
    }
    String g = p.getGroupId();
    String a = p.getArtifactId();
    String v = p.getVersion();
    if(StringUtils.isNotBlank(g) && StringUtils.isNotBlank(a) && StringUtils.isNotBlank(v)) {
      return new MavenCoordinates(
          model.getParent().getGroupId(),
          model.getParent().getArtifactId(),
          model.getParent().getVersion());
    } else {
      return null;
    }
  }

}
