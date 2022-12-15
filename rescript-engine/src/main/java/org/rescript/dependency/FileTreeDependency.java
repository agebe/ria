package org.rescript.dependency;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.rescript.ScriptException;

public class FileTreeDependency implements Dependency {

  private String baseDir;

  public FileTreeDependency(String baseDir) {
    super();
    this.baseDir = baseDir;
  }

  @Override
  public List<DependencyNode> resolve() {
    File base = new File(baseDir);
    if(!base.exists()) {
      throw new ScriptException("file tree base not found, " + base.getAbsolutePath());
    }
    if(!base.isDirectory()) {
      throw new ScriptException("file tree base need to be a directory, " + base.getAbsolutePath());
    }
    try(Stream<Path> stream = Files.walk((base.toPath()))) {
      return stream
      .filter(Files::isRegularFile)
      .map(Path::toFile)
      .map(DependencyNode::new)
      .toList();
    } catch(IOException e) {
      throw new ScriptException(
          "failed to resolves dependencies from file tree base '%s'".formatted(
              base.getAbsolutePath()), e);
    }
  }

  public static boolean isFileTreeDependency(String s) {
    try {
      File f = new File(s);
      return f.isDirectory();
    } catch(Exception e) {
      return false;
    }
  }

}
