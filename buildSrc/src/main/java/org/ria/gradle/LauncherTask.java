/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria.gradle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// https://www.jrebel.com/blog/using-buildsrc-custom-logic-gradle-builds
public class LauncherTask extends DefaultTask {

  private static final Logger log = LoggerFactory.getLogger(LauncherTask.class);

  private static final int CTRUE = 1;
  private static final int CFALSE = 0;

  private Map<String, String> filenameToMavenPathMap = new HashMap<>();

  private boolean includeBootAndSnapshotDependenciesOnly;

  private File workingDir;

  // this is to build online version of the launcher (which will download additional dependencies from maven central)
  // or offline versions which includes all dependencies
  public void includeBootAndSnapshotDependenciesOnly(boolean includeBootAndSnapshotDependenciesOnly) {
    this.includeBootAndSnapshotDependenciesOnly = includeBootAndSnapshotDependenciesOnly;
  }

  public void workingDir(String workingDir) {
    this.workingDir = new File(getProject().getProjectDir(), workingDir);
  }

  private boolean isRiaDependency(String filename) {
    String[] parts = filename.split("-");
    return parts[0].equals("ria");
  }

  private String getRiaVersion(String filename) {
    //ria-engine-0.1.0-SNAPSHOT.jar
    String[] parts = filename.split("-");
    String version = parts[parts.length-1].substring(0, parts[parts.length-1].lastIndexOf('.'));
    if("SNAPSHOT".equals(version)) {
      version = parts[parts.length-2] + "-SNAPSHOT";
    }
    return version;
  }

  private String getRiaArtifact(String filename) {
    String[] parts = filename.split("-");
    return parts[0] + "-" + parts[1];
  }

  @TaskAction
  public void run() throws IOException {
    //System.out.println(getDescription());
    //System.out.println(includeBootAndSnapshotDependenciesOnly);
    Project project = getProject();
    //https://stackoverflow.com/questions/37697622/gradle-get-url-of-dependency-artifact
    project.getConfigurations().getByName("runtimeClasspath").forEach(d -> {
      String[] path = d.getAbsolutePath().split(File.separator);
      String filename = path[path.length-1];
      String version = isRiaDependency(filename)?getRiaVersion(filename):path[path.length-3];
      String artifact = isRiaDependency(filename)?getRiaArtifact(filename):path[path.length-4];
      String group = isRiaDependency(filename)?"io.github.agebe":path[path.length-5];
      String mavenPath = Stream.of(
          group.replace('.', '/'),
          artifact,
          version,
          filename).collect(Collectors.joining("/"));
      log.info("map '{}' to '{}'", filename, mavenPath);
      filenameToMavenPathMap.put(filename, mavenPath);
    });
    File buildDir = project.getBuildDir();
    File libDir = new File(buildDir, "install/ria-launcher/lib");
    //File libDir = new File(buildDir, "libs");
    if(!libDir.isDirectory()) {
      throw new RuntimeException("lib dir does not exist, " + libDir.getAbsolutePath());
    }
    List<File> jars = Arrays.stream(libDir.listFiles())
        .sorted(Comparator.comparing(f -> f.getName().toLowerCase()))
//        .filter(f -> includeBootAndSnapshotDependenciesOnly?f.getName().startsWith("ria-launcher-") || f.getName().contains("-SNAPSHOT."):true)
        .collect(Collectors.toList());
    log.info("runtime dependencies '{}'", jars);
//    File outDir = new File(buildDir,"launcher");
    File outDir = workingDir;
//    System.out.println(outDir.getAbsolutePath());
    outDir.mkdirs();
    writeFilesDotH(outDir, jars);
    writeFile(outDir, "launcher.c");
    writeFile(outDir, "launcher.h");
    writeFile(outDir, "findLibJvm.h");
    writeFile(outDir, "findLibJvm.c");
    writeFile(outDir, "launchJvm.h");
    writeFile(outDir, "launchJvm.c");;
  }

  private void writeFile(File outDir, String filename) throws IOException {
    FileUtils.copyInputStreamToFile(
        this.getClass().getClassLoader().getResourceAsStream(filename),
        new File(outDir, filename));
  }

  private boolean isBootLib(File f) {
    return f.getName().startsWith("ria-launcher-") ||
        f.getName().startsWith("ria-utils-");
  }

  private void writeFilesDotH(File outDir, List<File> jars) {
    try(PrintWriter writer = new PrintWriter(new FileWriter(new File(outDir, "files.h")))) {
      writer.println("#ifndef _FILES_H");
      writer.println("#define _FILES_H");
      writer.println("#include \"launcher.h\"");
      writer.println();
      writer.println("char *version = \"%s\";".formatted(getProject().getVersion()));
      writer.println();
      for(int i=0;i<jars.size();i++) {
        File f = jars.get(i);
        writer.println("char fn%s[] = \"%s\";".formatted(i, f.getName()));
        writer.println("char fc%s[] = {".formatted(i));
        long length = includeFile(writer, f);
        writer.println(" };");
        writer.println("long fcl%s = %s;".formatted(i, length));
        // Zero is interpreted as false and anything non-zero is interpreted as true
        writer.println("int fb%s = %s;".formatted(i, isBootLib(f)?CTRUE:CFALSE));
        writer.println("char* fUrl%s = %s;".formatted(i, url(f)));
        writer.println();
      }
      writer.println("struct EmbeddedFile files[%s];".formatted(jars.size()));
      writer.println("int filesCount = %s;".formatted(jars.size()));
      writer.println();
      writer.println("void initFiles() {");
      for(int i=0;i<jars.size();i++) {
        writer.println("  files[%s].name = fn%s;".formatted(i,i));
        writer.println("  files[%s].content = fc%s;".formatted(i,i));
        writer.println("  files[%s].contentLength = fcl%s;".formatted(i,i));
        writer.println("  files[%s].boot = fb%s;".formatted(i,i));
        writer.println("  files[%s].url = fUrl%s;".formatted(i,i));
        writer.println("  files[%s].download = %s;".formatted(i,isIncludeFile(jars.get(i))?CFALSE:CTRUE));
      }
      writer.println("}");
      writer.println();
      writer.println("#endif");
    } catch(Exception e) {
      throw new RuntimeException("failed to write files.h", e);
    }
  }

  private String url(File f) {
    String url = filenameToMavenPathMap.get(f.getName());
    if(isIncludeFile(f)) {
      if(url != null) {
        return "\"" + url + "\"";
      } else {
        return "NULL";
      }
    } else {
      if(url != null) {
        return "\"" + url + "\"";
      } else {
        throw new RuntimeException("url not found for dependency " + f.getName());
        //return "NULL";
      }
    }
  }

  private boolean isIncludeFile(File f) {
    if(includeBootAndSnapshotDependenciesOnly) {
      String name = f.getName();
      return isBootLib(f) || name.contains("-SNAPSHOT.");
    } else {
      return true;
    }
  }

  private long includeFile(PrintWriter writer, File f) {
    if(isIncludeFile(f)) {
      return writeByteArray(writer, f);
    } else {
      return f.length();
    }
  }

  private long writeByteArray(PrintWriter writer, File f) {
    try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(f))) {
      long counter = 0;
      writer.print("  ");
      for(;;) {
        int b = in.read();
        if(b == -1) {
          break;
        }
        if(counter > 0) {
          if(counter % 32 == 0) {
            writer.println(",");
            writer.print("  ");
          } else {
            writer.print(", ");
          }
        }
        writer.print(String.format("0x%02x", b));
        counter++;
      }
      return counter;
    } catch(Exception e) {
      throw new RuntimeException("failed to read " + f.getAbsolutePath());
    }
  }

}

