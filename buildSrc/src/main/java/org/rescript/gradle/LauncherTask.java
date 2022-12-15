package org.rescript.gradle;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.io.*;

import org.apache.commons.io.FileUtils;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

// https://www.jrebel.com/blog/using-buildsrc-custom-logic-gradle-builds
public class LauncherTask extends DefaultTask {

  @TaskAction
  public void run() throws IOException {
    Project project = getProject();
    File buildDir = project.getBuildDir();
    File libDir = new File(buildDir, "install/rescript-launcher/lib");
    //File libDir = new File(buildDir, "libs");
    if(!libDir.isDirectory()) {
      throw new RuntimeException("lib dir does not exist, " + libDir.getAbsolutePath());
    }
    List<File> jars = Arrays.stream(libDir.listFiles())
        .sorted(Comparator.comparing(f -> f.getName().toLowerCase()))
        .collect(Collectors.toList());
    System.out.println(jars);
    File outDir = new File(buildDir,"launcher");
    outDir.mkdirs();
    writeFilesDotH(outDir, jars);
    writeFile(outDir, "bs.c");
    writeFile(outDir, "bs.h");
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

  private void writeFilesDotH(File outDir, List<File> jars) {
    try(PrintWriter writer = new PrintWriter(new FileWriter(new File(outDir, "files.h")))) {
      writer.println("#ifndef _FILES_H");
      writer.println("#define _FILES_H");
      writer.println("#include \"bs.h\"");
      writer.println();
      writer.println("char *version = \"%s\";".formatted(getProject().getVersion()));
      writer.println();
      for(int i=0;i<jars.size();i++) {
        File f = jars.get(i);
        writer.println("char fn%s[] = \"%s\";".formatted(i, f.getName()));
        writer.println("char fc%s[] = {".formatted(i));
        long length = writeByteArray(writer, f);
        writer.println(" };");
        writer.println("long fcl%s = %s;".formatted(i, length));
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
      }
      writer.println("}");
      writer.println();
      writer.println("#endif");
    } catch(Exception e) {
      throw new RuntimeException("failed to write files.h", e);
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

