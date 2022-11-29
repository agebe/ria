package org.rescript.dependency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;

public class DependencyNode {

  private String group;

  private String artifact;

  private String version;

  private File file;

  private List<DependencyNode> children = new ArrayList<>();

  public DependencyNode() {
    super();
  }

  public DependencyNode(File file) {
    super();
    this.file = file;
  }

  public DependencyNode(String group, String artifact, String version, File file) {
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
    this.file = file;
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
    children.add(child);
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

  public List<DependencyNode> getChildren() {
    return children;
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
 
}
