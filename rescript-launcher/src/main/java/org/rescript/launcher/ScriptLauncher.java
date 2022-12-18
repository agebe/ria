package org.rescript.launcher;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

public class ScriptLauncher {

  private static String[] scriptArgs(String[] args) {
    String[] scriptArgs = new String[args.length-1];
    System.arraycopy(args, 1, scriptArgs, 0, scriptArgs.length);
    return scriptArgs;
  }

  private static URL toUrl(File f) {
    try {
      return f.toURI().toURL();
    } catch (MalformedURLException e) {
      // TODO replace with ScriptLauncherException
      throw new RuntimeException("failed to convert file '%s' to url".formatted(f.getAbsolutePath()), e);
    }
  }

  public static void main(String[] args) {
//    System.out.println(Arrays.toString(args));
    if(args.length < 2) {
      System.err.println("script file parameter missing");
      System.exit(1);
    }
    String rescriptHome = args[0];
    File libsDir = new File(rescriptHome+"/0.1.0-SNAPSHOT/libs");
    if(!libsDir.exists()) {
      throw new RuntimeException("lib dir '%s' not found".formatted(libsDir.getAbsolutePath()));
    }
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
        Method run = scriptClass.getMethod("run", String.class);
        run.invoke(s, script);
//        Script s = new Script(script);
//        s.setShowErrorsOnConsole(true);
//        s.setArguments(scriptArgs(args));
//        s.run();
        // TODO set the script classloader to the launcher classloader
        // that should avoid classpath clashes of script engine and script dependencies
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
