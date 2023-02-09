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
package org.ria.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

import org.ria.ScriptEngine;
import org.ria.cloader.CLoader;
import org.ria.util.ManifestUtils;

public class ScriptLauncher {

  private static final String MAVEN_REPO_ENV = "RIA_MAVEN_REPO";

  private static String mavenRepo = "https://repo.maven.apache.org/maven2/";

  private static CliOptions cliOptions;

  private static String getBasePackage() {
    String s = ScriptLauncher.class.getPackageName();
    int i = s.lastIndexOf('.');
    return s.substring(0, i);
  }

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
    return mavenRepo.endsWith("/")?mavenRepo + path:mavenRepo + "/" + path;
  }

  private static void downloadFromRemote(String url, File libsDir) {
    try {
      HttpClient client = HttpClient
          .newBuilder()
          .build();
      String filename = url.substring(url.lastIndexOf('/')+1);
      File f = new File(libsDir, filename);
      if(!cliOptions.quiet) {
        System.err.println("get script engine dependency " + url);
      }
      HttpRequest request = HttpRequest.newBuilder()
          .uri(new URI(url))
          .version(HttpClient.Version.HTTP_2)
          .GET()
          .build();
      HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
      if((response.statusCode() >= 200) && (response.statusCode() <= 299)) {
        Files.copy(
            response.body(),
            f.toPath(),
            StandardCopyOption.REPLACE_EXISTING);
      } else {
        throw new ScriptLauncherException("got http status code '%s' on '%s'".formatted(response.statusCode(), url));
      }
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

  private static void downloadMissing(File riaHomeVersion, File libsDir) throws IOException {
    Files.readAllLines(new File(riaHomeVersion, "libs.txt").toPath())
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
      mavenRepo = cliOptions.mavenRepo;
    } else {
      String repos = System.getenv().get(MAVEN_REPO_ENV);
      if(!isBlank(repos)) {
        mavenRepo = repos;
      }
    }
  }

  private static void setupLogging(ClassLoader cloader, String level) {
    try {
      Class<?> clsLoggerFactory = cloader.loadClass("org.slf4j.LoggerFactory");
      Method methodGetILoggerFactory = clsLoggerFactory.getMethod("getILoggerFactory", new Class[0]);
      Object loggerContext = methodGetILoggerFactory.invoke(null, new Object[0]);
      Method methodGetLogger = loggerContext.getClass().getMethod("getLogger", String.class);
      Object logger = methodGetLogger.invoke(loggerContext, getBasePackage());
      Class<?> clsLevel = cloader.loadClass("ch.qos.logback.classic.Level");
      Method methodValueOf = clsLevel.getMethod("valueOf", String.class);
      Object levelObject = methodValueOf.invoke(null, level.toUpperCase());
      Method methodSetLevel = logger.getClass().getMethod("setLevel", clsLevel);
      methodSetLevel.invoke(logger, levelObject);
    } catch(Exception e) {
      e.printStackTrace(System.err);
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
    String version = ManifestUtils.version(ScriptLauncher.class.getClassLoader(), "ria-launcher");
    if(cliOptions.version) {
      System.err.println(version);
      if(version.endsWith("-SNAPSHOT")) {
        System.err.println(ManifestUtils.gitVersion(ScriptLauncher.class.getClassLoader(), "ria-launcher"));
      }
    }
//    System.out.println(Arrays.toString(args));
    if(cliOptions.scriptFile == null) {
      System.err.println("script file parameter missing");
      cliOptions.printHelp();
      System.exit(1);
    }
    if(Files.notExists(cliOptions.scriptFile)) {
      System.err.println("script file not found " + cliOptions.scriptFile);
      System.exit(1);
    }
    File home = cliOptions.scriptEngineHome.toFile();
    File homeVersion = new File(home, version);
    File libsDir = new File(homeVersion, "libs");
    // the native launcher should have created the libs dir
    if(!libsDir.exists()) {
      throw new ScriptLauncherException("lib dir '%s' not found".formatted(libsDir.getAbsolutePath()));
    }
    setupMavenRepo();
    downloadMissing(homeVersion, libsDir);
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
      if(cliOptions.debug) {
        setupLogging(loader, "debug");
      } else {
        setupLogging(loader, "info");
      }
      String script = new String(Files.readAllBytes(scriptFile));
      Class<?> scriptClass = loader.loadClass(getBasePackage()+".Script");
      Object s = scriptClass.getDeclaredConstructor().newInstance();
      ((ScriptEngine)s)
      .setDefaultMavenRepository(mavenRepo)
      .setScriptClassLoader(ScriptLauncher.class.getClassLoader())
      .setShowErrorsOnConsole(true)
      .setArguments(cliOptions.scriptArgs)
      .setHome(home)
      .setDownloadDependenciesOnly(cliOptions.downloadDependenciesOnly)
      .setDisplayInfo(cliOptions.info)
      .setQuiet(cliOptions.quiet)
      .run(script);
    } catch(Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

}
