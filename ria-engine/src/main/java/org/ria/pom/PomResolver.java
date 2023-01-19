package org.ria.pom;

import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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

  public Pair<Model, Model> load(MavenCoordinates coord) throws Exception {
    String pomString = repository.getFile(coord, ".pom");
    MavenXpp3Reader reader = new MavenXpp3Reader();
    Model model = reader.read(new StringReader(pomString));
    MavenCoordinates parentCoord = parentCoords(model);
    Model parent = parentCoord!=null?resolveMergeRecursive(parentCoord):null;
    return Pair.of(model, parent);
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
