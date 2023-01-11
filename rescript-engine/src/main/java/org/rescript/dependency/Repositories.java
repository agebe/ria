package org.rescript.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.rescript.pom.MavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repositories implements Consumer<Object> {

  private static final Logger log = LoggerFactory.getLogger(Repositories.class);

  private static final String CENTRAL_URL = "https://repo.maven.apache.org/maven2/";

  private final MavenRepository CENTRAL;

  private MavenRepository defaultRepository;

  private List<MavenRepository> repositories = new ArrayList<>();

  private File cacheBase;

  public Repositories(File cacheBase) {
    this(null, cacheBase);
  }

  public Repositories(String defaultMavenRepo, File cacheBase) {
    super();
    CENTRAL = new MavenRepository(CENTRAL_URL, cacheBase);
    defaultRepository = StringUtils.isBlank(defaultMavenRepo)?CENTRAL:new MavenRepository(defaultMavenRepo, cacheBase);
    this.cacheBase = cacheBase;
  }

  @Override
  public void accept(Object t) {
    if(t instanceof MavenRepository m) {
      repositories.add(m);
    } else if(t instanceof String s) {
      repositories.add(new MavenRepository(s, cacheBase));
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

  public List<MavenRepository> getRepositoriesOrDefault() {
    return (repositories == null || repositories.isEmpty())?List.of(defaultRepository):repositories;
  }

  // keep this, it is used by scripts
  public MavenRepository mavenCentral() {
    return CENTRAL;
  }

  // keep this, it is used by scripts
  public MavenRepository maven(String url) {
    return new MavenRepository(url, cacheBase);
  }

}
