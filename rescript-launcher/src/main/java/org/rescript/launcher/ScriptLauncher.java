package org.rescript.launcher;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import org.rescript.cloader.CLoader;

public class ScriptLauncher {

  private static final String MAVEN_REPO_ENV = "BS_MAVEN_REPO";

  private static String MAVEN_REPO = "https://repo.maven.apache.org/maven2/";

  private static String[] scriptArgs(String[] args) {
    String[] scriptArgs = new String[args.length-1];
    System.arraycopy(args, 1, scriptArgs, 0, scriptArgs.length);
    return scriptArgs;
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
    return MAVEN_REPO.endsWith("/")?MAVEN_REPO + path:MAVEN_REPO + "/" + path;
  }

  private static String version() {
    try {
      Enumeration<URL> manifestUrls = ScriptLauncher.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
      while(manifestUrls.hasMoreElements()) {
        URL url = manifestUrls.nextElement();
        Manifest manifest = new Manifest(url.openStream());
        if("rescript-launcher".equals(manifest.getMainAttributes().getValue("Implementation-Title"))) {
          String version = manifest.getMainAttributes().getValue("Implementation-Version");
          if(version != null) {
            return version;
          } else {
            throw new ScriptLauncherException("no version on rescript launcher manifest");
          }
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException("failed to determine rescript launcher version", e);
    }
    throw new ScriptLauncherException("failed to determine rescript lancher version, manifest not found");
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
    String repos = System.getenv().get(MAVEN_REPO_ENV);
    if(!isBlank(repos)) {
      MAVEN_REPO = repos;
    }
  }

  public static void main(String[] args) throws Exception {
//    System.out.println(Arrays.toString(args));
    if(args.length < 2) {
      System.err.println("script file parameter missing");
      System.exit(1);
    }
    String rescriptHome = args[0];
    File bsHomeVersion = new File(rescriptHome+"/"+version());
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
    String scriptFile = args[1];
//    try(URLClassLoader loader = new URLClassLoader(
    try(CLoader loader = new CLoader(
        "launcherClassLoader",
        libs.stream()
        .map(ScriptLauncher::toUrl)
        .toArray(URL[]::new),
        ScriptLauncher.class.getClassLoader())) {
      File f = new File(scriptFile);
      if(f.exists()) {
        String script = new String(Files.readAllBytes(f.toPath()));
        Class<?> scriptClass = loader.loadClass("org.rescript.Script");
        Object s = scriptClass.getDeclaredConstructor().newInstance();
        Method setShowErrorsOnConsole = scriptClass.getMethod("setShowErrorsOnConsole", boolean.class);
        setShowErrorsOnConsole.invoke(s, true);
        Method setArguments = scriptClass.getMethod("setArguments", String[].class);
        setArguments.invoke(s, new Object[]{scriptArgs(args)});
        // setting the script class loader to the app class loader prevents the script seeing (and clashing)
        // with the script engines own dependencies (like antlr4 or commons lang3 etc.)
        Method setScriptClassLoader = scriptClass.getMethod("setScriptClassLoader", ClassLoader.class);
        setScriptClassLoader.invoke(s, ScriptLauncher.class.getClassLoader());
        Method run = scriptClass.getMethod("run", String.class);
        run.invoke(s, script);
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
