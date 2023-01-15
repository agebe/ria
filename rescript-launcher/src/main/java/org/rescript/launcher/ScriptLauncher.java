package org.rescript.launcher;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.rescript.ScriptEngine;
import org.rescript.cloader.CLoader;
import org.rescript.util.ManifestUtils;

public class ScriptLauncher {

  private static final String MAVEN_REPO_ENV = "BS_MAVEN_REPO";

  private static String MAVEN_REPO = "https://repo.maven.apache.org/maven2/";

  private static CliOptions cliOptions;

  private static URL toUrl(File f) {
    try {
      return f.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new ScriptLauncherException("failed to convert file '%s' to url".formatted(f.getAbsolutePath()), e);
    }
  }

  private static String toUrl(String path) {
    if(path.startsWith("http://") || path.startsWith("https://")) {
      return path;
    }
    return MAVEN_REPO.endsWith("/")?MAVEN_REPO + path:MAVEN_REPO + "/" + path;
  }

  private static void downloadFromRemote(String url, File libsDir) {
    try {
      HttpClient client = HttpClient
          .newBuilder()
          .build();
      String filename = url.substring(url.lastIndexOf('/')+1);
      File f = new File(libsDir, filename);
      System.err.println("get script engine dependency " + url);
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(url))
          .version(HttpClient.Version.HTTP_2)
          .GET()
          .build();
      client.send(request, BodyHandlers.ofFile(f.toPath()));
    } catch(Exception e) {
      throw new ScriptLauncherException("failed to download file from " + url, e);
    }
  }

  private static void copy(File src, File dest) {
    try {
      Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING,
          StandardCopyOption.COPY_ATTRIBUTES);
    } catch (IOException e) {
      throw new ScriptLauncherException("failed to copy file from '%s' to '%s'"
          .formatted(src.getAbsolutePath(), dest.getAbsolutePath()), e);
    }
  }

  private static void fetchIfMissing(String url, File libsDir) {
    String filename = url.substring(url.lastIndexOf('/')+1);
    File f = new File(libsDir, filename);
    if(!f.exists()) {
      if(url.startsWith("/")) {
        copy(new File(url), new File(libsDir, filename));
      } else if(url.startsWith("file:")) {
        copy(new File(url.substring("file:".length())), new File(libsDir, filename));
      } else {
        downloadFromRemote(url, libsDir);
      }
    }
  }

  private static void downloadMissing(File bsHomeVersion, File libsDir) throws IOException {
    Files.readAllLines(new File(bsHomeVersion, "libs.txt").toPath())
    .forEach(path -> fetchIfMissing(toUrl(path), libsDir));
  }

  public static boolean isBlank(final CharSequence cs) {
    final int strLen = cs == null ? 0 : cs.length();
    if (strLen == 0) {
        return true;
    }
    for (int i = 0; i < strLen; i++) {
        if (!Character.isWhitespace(cs.charAt(i))) {
            return false;
        }
    }
    return true;
}

  private static void setupMavenRepo() {
    if(cliOptions.mavenRepo != null) {
      MAVEN_REPO = cliOptions.mavenRepo;
    } else {
      String repos = System.getenv().get(MAVEN_REPO_ENV);
      if(!isBlank(repos)) {
        MAVEN_REPO = repos;
      }
    }
  }

  public static void main(String[] args) throws Exception {
    cliOptions = new CliOptions(args);
//    System.err.println(Arrays.toString(args));
//    System.err.println(cliOptions);
    if(cliOptions.help) {
      cliOptions.printHelp();
      System.exit(0);
    }
    String version = ManifestUtils.version(ScriptLauncher.class.getClassLoader(), "rescript-launcher");
    if(cliOptions.version) {
      System.err.println(version);
      if(version.endsWith("-SNAPSHOT")) {
        System.err.println(ManifestUtils.gitVersion(ScriptLauncher.class.getClassLoader(), "rescript-launcher"));
      }
    }
//    System.out.println(Arrays.toString(args));
    if(cliOptions.scriptFile == null) {
      System.err.println("script file parameter missing");
      System.exit(1);
    }
    if(Files.notExists(cliOptions.scriptFile)) {
      System.err.println("script file not found " + cliOptions.scriptFile);
      System.exit(1);
    }
    File rescriptHome = cliOptions.nativeHome.toFile();
    File bsHomeVersion = new File(rescriptHome, version);
    File libsDir = new File(bsHomeVersion, "libs");
    // the native launcher should have created the libs dir
    if(!libsDir.exists()) {
      throw new ScriptLauncherException("lib dir '%s' not found".formatted(libsDir.getAbsolutePath()));
    }
    setupMavenRepo();
    downloadMissing(bsHomeVersion, libsDir);
    List<File> libs = Stream.of(libsDir.listFiles())
    .filter(file -> !file.isDirectory())
    .toList();
    Path scriptFile = cliOptions.scriptFile;
//    try(URLClassLoader loader = new URLClassLoader(
    try(CLoader loader = new CLoader(
        "launcherClassLoader",
        libs.stream()
        .map(ScriptLauncher::toUrl)
        .toArray(URL[]::new),
        ScriptLauncher.class.getClassLoader())) {
      File f = scriptFile.toFile();
      if(f.exists()) {
        String script = new String(Files.readAllBytes(f.toPath()));
        Class<?> scriptClass = loader.loadClass("org.rescript.Script");
        Object s = scriptClass.getDeclaredConstructor().newInstance();
        ScriptEngine engine = (ScriptEngine)s;
        engine.setDefaultMavenRepository(MAVEN_REPO);
        engine.setScriptClassLoader(ScriptLauncher.class.getClassLoader());
        engine.setShowErrorsOnConsole(true);
        engine.setArguments(cliOptions.scriptArgs);
        engine.setRescriptHome(rescriptHome);
        engine.run(script);
      } else {
        System.err.println("script file '%s' not found".formatted(f.getAbsolutePath()));
        System.exit(1);
      }
    } catch(Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

}
