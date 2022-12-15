package org.rescript.dependency;

import java.io.File;
import java.util.List;

import org.rescript.ScriptException;

public class FileDependency implements Dependency {

  private String file;

  public FileDependency(String file) {
    super();
    this.file = file;
  }

  @Override
  public List<DependencyNode> resolve() {
    File f = new File(file);
    if(!f.exists()) {
      throw new ScriptException("file dependency not found, " + f.getAbsolutePath());
    }
    if(!f.isFile()) {
      throw new ScriptException("file dependency '%s' is not a normal file".formatted(f.getAbsolutePath()));
    }
    return List.of(new DependencyNode(f));
  }

  public static boolean isFileDependency(String s) {
    try {
      File f = new File(s);
      return f.isFile();
    } catch(Exception e) {
      return false;
    }
  }

}
