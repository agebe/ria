package org.rescript.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;
import org.rescript.pom.MavenRepository;

public class DependencyNode {

  private String group;

  private String artifact;

  private String version;

  private File file;

  private DependencyNode parent;

  private List<DependencyNode> children = new ArrayList<>();

  private List<Exclusion> exclusions = new ArrayList<>();

  private boolean optional;

  private MavenRepository repository;

  private String packaging;

  public DependencyNode() {
    super();
  }

  public DependencyNode(File file) {
    super();
    this.file = file;
  }

  public DependencyNode(
      String group,
      String artifact,
      String version,
      List<Exclusion> exclusions,
      boolean optional) {
    super();
    if(StringUtils.isBlank(group)) {
      throw new ScriptException("group is blank");
    }
    if(StringUtils.isBlank(artifact)) {
      throw new ScriptException("artifact is blank");
    }
    if(StringUtils.isBlank(version)) {
      throw new ScriptException("version is blank");
    }
    this.group = group;
    this.artifact = artifact;
    this.version = version;
    this.exclusions = exclusions != null?exclusions:List.of();
    this.optional = optional;
  }

  public boolean isManaged() {
    return StringUtils.isNotBlank(group);
  }

  public String groupArtifactId() {
    return group + ":" + artifact;
  }

  public String id() {
    return groupArtifactId() + ":" + version;
  }

  public void addChild(DependencyNode child) {
    if((child.parent != null) && !child.parent.equals(this)) {
      throw new ScriptException("child dependency node has already other parent");
    }
    child.parent = this;
    children.add(child);
  }

  public void addChildren(List<DependencyNode> children) {
    children.forEach(this::addChild);
  }

  public void addExclusion(Exclusion exclusion) {
    this.exclusions.add(exclusion);
  }

  public String getGroup() {
    return group;
  }

  public String getArtifact() {
    return artifact;
  }

  public String getVersion() {
    return version;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public List<File> filesRecursive() {
    return filesRecursive(new ArrayList<>());
  }

  private List<File> filesRecursive(List<File> files) {
    if(this.file != null) {
      files.add(this.file);
    }
    children.forEach(node -> node.filesRecursive(files));
    return files;
  }

  public List<DependencyNode> asList() {
    return asList(new ArrayList<>());
  }

  public List<DependencyNode> asList(List<DependencyNode> nodes) {
    nodes.add(this);
    children.forEach(node -> node.asList(nodes));
    return nodes;
  }

  public DependencyNode getParent() {
    return parent;
  }

  public List<Exclusion> getExclusions() {
    return exclusions;
  }

  public void setExclusions(List<Exclusion> exclusions) {
    this.exclusions = exclusions;
  }

  public boolean isExcluded(DependencyNode node) {
    boolean excluded = this.exclusions
        .stream()
        .anyMatch(e -> e.group().equals(node.getGroup()) && e.artifact().equals(node.getArtifact()));
    if(excluded) {
      return true;
    } else {
      if(parent != null) {
        return parent.isExcluded(node);
      } else {
        return false;
      }
    }
  }

  public boolean isOptional() {
    return optional;
  }

  public MavenRepository getRepository() {
    return repository;
  }

  public void setRepository(MavenRepository repository) {
    this.repository = repository;
  }

  public String getPackaging() {
    return packaging;
  }

  public void setPackaging(String packaging) {
    this.packaging = packaging;
  }

  public List<DependencyNode> getChildren() {
    return children;
  }

  @Override
  public String toString() {
    return group + ":" + artifact + ":" + version;
  }

}
