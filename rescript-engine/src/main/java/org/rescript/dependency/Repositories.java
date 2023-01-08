package org.rescript.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.rescript.pom.MavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repositories implements Consumer<Object> {

  private static final Logger log = LoggerFactory.getLogger(Repositories.class);

  private static final MavenRepository CENTRAL =
      new MavenRepository("https://repo.maven.apache.org/maven2/");

  private List<MavenRepository> repositories = new ArrayList<>();

  @Override
  public void accept(Object t) {
    if(t instanceof MavenRepository m) {
      repositories.add(m);
    } else if(t instanceof String s) {
      repositories.add(new MavenRepository(s));
    } else {
      log.warn("ignoring repository '{}', unknown type", t);
    }
  }

  public List<MavenRepository> getRepositories() {
    return repositories;
  }

  public void setRepositories(List<MavenRepository> repositories) {
    this.repositories = repositories;
  }

  public List<MavenRepository> getRepositoriesOrCentral() {
    return (repositories == null || repositories.isEmpty())?List.of(CENTRAL):repositories;
  }

  public MavenRepository mavenCentral() {
    return CENTRAL;
  }

  public MavenRepository maven(String url) {
    return new MavenRepository(url);
  }

  public MavenRepository getRepo0() {
    return getRepositoriesOrCentral().get(0);
  }

}
