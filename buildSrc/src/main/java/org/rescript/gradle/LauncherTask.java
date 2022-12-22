package org.rescript.gradle;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

// https://www.jrebel.com/blog/using-buildsrc-custom-logic-gradle-builds
public class LauncherTask extends DefaultTask {

  private static final int CTRUE = 1;
  private static final int CFALSE = 0;

  // copied from gradle --info --refresh-dependencies clean jar (inside rescript-engine module)
  // TODO get all dependencies from gradle directly
  // https://stackoverflow.com/questions/37697622/gradle-get-url-of-dependency-artifact
  private final String dependencies = """
https://repo.maven.apache.org/maven2/commons-io/commons-io/2.11.0/commons-io-2.11.0.jar
https://repo.maven.apache.org/maven2/ch/qos/logback/logback-classic/1.4.5/logback-classic-1.4.5.jar
https://repo.maven.apache.org/maven2/ch/qos/logback/logback-core/1.4.5/logback-core-1.4.5.jar
Downloading https://repo.maven.apache.org/maven2/org/antlr/antlr4/4.11.1/antlr4-4.11.1.pom to /home/andre/.gradle/.tmp/gradle_download2640302989933963647bin
Downloading https://repo.maven.apache.org/maven2/org/antlr/antlr4-master/4.11.1/antlr4-master-4.11.1.pom to /home/andre/.gradle/.tmp/gradle_download6633064513484959291bin
Cached resource https://repo.maven.apache.org/maven2/org/sonatype/oss/oss-parent/9/oss-parent-9.pom is up-to-date (lastModified: Tue Jan 28 05:15:21 AEST 2014).
Downloading https://repo.maven.apache.org/maven2/org/antlr/antlr4-runtime/4.11.1/antlr4-runtime-4.11.1.pom to /home/andre/.gradle/.tmp/gradle_download16164772899016412053bin
Downloading https://repo.maven.apache.org/maven2/org/antlr/antlr-runtime/3.5.3/antlr-runtime-3.5.3.pom to /home/andre/.gradle/.tmp/gradle_download1267392963285455698bin
Downloading https://repo.maven.apache.org/maven2/org/antlr/ST4/4.3.4/ST4-4.3.4.pom to /home/andre/.gradle/.tmp/gradle_download10019743808339032836bin
Downloading https://repo.maven.apache.org/maven2/org/abego/treelayout/org.abego.treelayout.core/1.0.3/org.abego.treelayout.core-1.0.3.pom to /home/andre/.gradle/.tmp/gradle_download14408654350703085081bin
Downloading https://repo.maven.apache.org/maven2/com/ibm/icu/icu4j/71.1/icu4j-71.1.pom to /home/andre/.gradle/.tmp/gradle_download9937975142651401786bin
Downloading https://repo.maven.apache.org/maven2/org/glassfish/javax.json/1.1.4/javax.json-1.1.4.pom to /home/andre/.gradle/.tmp/gradle_download15630069443432218911bin
Cached resource https://repo.maven.apache.org/maven2/org/sonatype/oss/oss-parent/7/oss-parent-7.pom is up-to-date (lastModified: Mon Mar 07 22:42:48 AEST 2011).
Downloading https://repo.maven.apache.org/maven2/org/antlr/antlr-master/3.5.3/antlr-master-3.5.3.pom to /home/andre/.gradle/.tmp/gradle_download13540598857677541068bin
Downloading https://repo.maven.apache.org/maven2/org/glassfish/json/1.1.4/json-1.1.4.pom to /home/andre/.gradle/.tmp/gradle_download2637747473356772500bin
Downloading https://repo.maven.apache.org/maven2/net/java/jvnet-parent/5/jvnet-parent-5.pom to /home/andre/.gradle/.tmp/gradle_download6641899782483380022bin
Downloading https://repo.maven.apache.org/maven2/org/abego/treelayout/org.abego.treelayout.core/1.0.3/org.abego.treelayout.core-1.0.3.jar to /home/andre/.gradle/.tmp/gradle_download5161320078383776170bin
Downloading https://repo.maven.apache.org/maven2/org/glassfish/javax.json/1.1.4/javax.json-1.1.4.jar to /home/andre/.gradle/.tmp/gradle_download15084568994861466496bin
Downloading https://repo.maven.apache.org/maven2/org/antlr/ST4/4.3.4/ST4-4.3.4.jar to /home/andre/.gradle/.tmp/gradle_download8412886751654224144bin
Downloading https://repo.maven.apache.org/maven2/org/antlr/antlr4/4.11.1/antlr4-4.11.1.jar to /home/andre/.gradle/.tmp/gradle_download7208798941337880115bin
Downloading https://repo.maven.apache.org/maven2/org/antlr/antlr4-runtime/4.11.1/antlr4-runtime-4.11.1.jar to /home/andre/.gradle/.tmp/gradle_download11930335795065907966bin
Downloading https://repo.maven.apache.org/maven2/org/antlr/antlr-runtime/3.5.3/antlr-runtime-3.5.3.jar to /home/andre/.gradle/.tmp/gradle_download15717956581827696675bin
Downloading https://repo.maven.apache.org/maven2/com/ibm/icu/icu4j/71.1/icu4j-71.1.jar to /home/andre/.gradle/.tmp/gradle_download2034692108529361350bin
Cached resource https://repo.maven.apache.org/maven2/org/apache/maven/maven-model/3.8.6/maven-model-3.8.6.pom is up-to-date (lastModified: Tue Jun 07 02:36:05 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/slf4j/slf4j-api/2.0.5/slf4j-api-2.0.5.pom is up-to-date (lastModified: Fri Nov 25 21:42:48 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/apache/commons/commons-text/1.10.0/commons-text-1.10.0.pom is up-to-date (lastModified: Sun Sep 25 00:57:11 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/javimmutable/javimmutable-collections/3.2.1/javimmutable-collections-3.2.1.pom is up-to-date (lastModified: Fri Jan 01 23:38:09 AEST 2021).
Cached resource https://repo.maven.apache.org/maven2/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.pom is up-to-date (lastModified: Sat Feb 27 06:40:52 AEST 2021).
Cached resource https://repo.maven.apache.org/maven2/org/slf4j/slf4j-parent/2.0.5/slf4j-parent-2.0.5.pom is up-to-date (lastModified: Fri Nov 25 21:42:37 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/apache/commons/commons-parent/54/commons-parent-54.pom is up-to-date (lastModified: Mon Sep 19 01:32:17 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/junit/junit-bom/5.7.1/junit-bom-5.7.1.pom is up-to-date (lastModified: Fri Feb 05 05:46:38 AEST 2021).
Cached resource https://repo.maven.apache.org/maven2/org/apache/maven/maven/3.8.6/maven-3.8.6.pom is up-to-date (lastModified: Tue Jun 07 02:35:54 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/junit/junit-bom/5.7.1/junit-bom-5.7.1.module is up-to-date (lastModified: Fri Feb 05 05:46:45 AEST 2021).
Cached resource https://repo.maven.apache.org/maven2/org/apache/apache/27/apache-27.pom is up-to-date (lastModified: Sun Jul 10 19:20:11 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/apache/maven/maven-parent/35/maven-parent-35.pom is up-to-date (lastModified: Mon Feb 28 04:04:07 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/junit/junit-bom/5.9.0/junit-bom-5.9.0.pom is up-to-date (lastModified: Wed Jul 27 05:09:06 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/apache/apache/25/apache-25.pom is up-to-date (lastModified: Fri Feb 18 08:08:51 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/junit/junit-bom/5.9.0/junit-bom-5.9.0.module is up-to-date (lastModified: Wed Jul 27 05:09:07 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/junit/junit-bom/5.9.1/junit-bom-5.9.1.pom is up-to-date (lastModified: Wed Sep 21 04:34:30 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/junit/junit-bom/5.9.1/junit-bom-5.9.1.module is up-to-date (lastModified: Wed Sep 21 04:34:31 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-utils/3.3.1/plexus-utils-3.3.1.pom is up-to-date (lastModified: Tue Dec 28 08:28:51 AEST 2021).
Cached resource https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus/5.1/plexus-5.1.pom is up-to-date (lastModified: Tue May 08 19:22:33 AEST 2018).
Cached resource https://repo.maven.apache.org/maven2/org/apache/commons/commons-text/1.10.0/commons-text-1.10.0.jar is up-to-date (lastModified: Sun Sep 25 00:57:09 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/apache/maven/maven-model/3.8.6/maven-model-3.8.6.jar is up-to-date (lastModified: Tue Jun 07 02:36:04 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/slf4j/slf4j-api/2.0.5/slf4j-api-2.0.5.jar is up-to-date (lastModified: Fri Nov 25 21:42:46 AEST 2022).
Cached resource https://repo.maven.apache.org/maven2/org/apache/commons/commons-lang3/3.12.0/commons-lang3-3.12.0.jar is up-to-date (lastModified: Sat Feb 27 06:40:51 AEST 2021).
Cached resource https://repo.maven.apache.org/maven2/org/javimmutable/javimmutable-collections/3.2.1/javimmutable-collections-3.2.1.jar is up-to-date (lastModified: Fri Jan 01 23:38:09 AEST 2021).
Cached resource https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-utils/3.3.1/plexus-utils-3.3.1.jar is up-to-date (lastModified: Tue Dec 28 08:28:50 AEST 2021).
      """;

  private Map<String, String> filenameToUrlMap = new HashMap<>();

  private boolean includeBootAndSnapshotDependenciesOnly;

  public LauncherTask() {
    String[] split = dependencies.split("\n");
    Pattern pattern = Pattern.compile("^.*(https://.*jar).*$");
    for(String s : split) {
      Matcher m = pattern.matcher(s);
      if(m.matches()) {
        String url = m.group(1);
        String filename = url.substring(url.lastIndexOf('/')+1);
        filenameToUrlMap.put(filename, url);
      }
    }
  }

  // this is to build online version of the launcher (which will download additional dependencies from maven central)
  // or offline versions which includes all dependencies
  public void includeBootAndSnapshotDependenciesOnly(boolean includeBootAndSnapshotDependenciesOnly) {
    this.includeBootAndSnapshotDependenciesOnly = includeBootAndSnapshotDependenciesOnly;
  }

  @TaskAction
  public void run() throws IOException {
    //System.out.println(getDescription());
    //System.out.println(includeBootAndSnapshotDependenciesOnly);
    Project project = getProject();
    File buildDir = project.getBuildDir();
    File libDir = new File(buildDir, "install/rescript-launcher/lib");
    //File libDir = new File(buildDir, "libs");
    if(!libDir.isDirectory()) {
      throw new RuntimeException("lib dir does not exist, " + libDir.getAbsolutePath());
    }
    List<File> jars = Arrays.stream(libDir.listFiles())
        .sorted(Comparator.comparing(f -> f.getName().toLowerCase()))
//        .filter(f -> includeBootAndSnapshotDependenciesOnly?f.getName().startsWith("rescript-launcher-") || f.getName().contains("-SNAPSHOT."):true)
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

  private boolean isBootLib(File f) {
    return f.getName().startsWith("rescript-launcher-") ||
        f.getName().startsWith("rescript-utils-");
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
    if(isIncludeFile(f)) {
      return "NULL";
    } else {
      String url = filenameToUrlMap.get(f.getName());
      if(url != null) {
        return "\"" + url + "\"";
      } else {
        throw new RuntimeException("url not found for dependency " + f.getName());
        //return "NULL";
      }
    }
  }

//  private void testDependencyToUrl() {
//    // TODO get the url of the dependencies from gradle
////    https://stackoverflow.com/questions/37697622/gradle-get-url-of-dependency-artifact
//    System.out.println("repo size: " + this.getProject().getRepositories().size());
//    this.getProject().getRepositories().stream().forEach(repo -> {
//      System.out.println(repo);
//    });
//    this.getProject().getConfigurations().stream().forEach(c -> {
//      System.out.println(c);
//    });
//    Configuration cfg = this.getProject().getConfigurations().getByName("implementation");
////    System.out.println(cfg);
//    cfg.getDependencies().stream().forEach(d -> {
//      System.out.println(d);
//      //this.getProject().getDependencies().
//    });
//  }

  private boolean isIncludeFile(File f) {
    if(includeBootAndSnapshotDependenciesOnly) {
      String name = f.getName();
      return name.startsWith("rescript-launcher-") ||
          name.contains("-SNAPSHOT.");
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

