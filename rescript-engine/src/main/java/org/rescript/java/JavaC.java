package org.rescript.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;
import org.rescript.cloader.CLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// https://stackoverflow.com/a/7989365/20615256s
public class JavaC {

  private static final Logger log = LoggerFactory.getLogger(JavaC.class);

  private static String getPath(Diagnostic<? extends JavaFileObject> d) {
    try {
      return d.getSource().toUri().getPath();
    } catch(Exception e) {
      return "(unknown)";
    }
  }

  private static String diagnosticToString(Diagnostic<? extends JavaFileObject> d) {
    return 
        "javac: code '%s', kind '%s', position '%s', start position '%s', end position '%s', source '%s', message '%s'"
        .formatted(
            d.getCode(),
            d.getKind(),
            d.getPosition(),
            d.getStartPosition(),
            d.getEndPosition(),
            getPath(d),
            d.getMessage(null));
  }

  public static ClassLoader compile(List<? extends JavaFileObject> compilationUnits, ClassLoader loader) {
    List<String> options = new ArrayList<>();
    if(loader instanceof CLoader cloader) {
      String classpath = System.getProperty("java.class.path");
      String separator = System.getProperty("path.separator");
      String depClasspath = cloader.getFiles()
          .stream()
          .map(File::getAbsolutePath)
          .collect(Collectors.joining(separator));
      classpath += separator + depClasspath;
      options.add("-classpath");
      options.add(classpath);
      log.debug("javac classpath {}", Arrays.stream(StringUtils.split(classpath, separator))
          .collect(Collectors.joining("\n")));
    }
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(diagnostics, null, null);
    FileManager fileManager = new FileManager(stdFileManager);
    CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);
    boolean success = task.call();
    diagnostics.getDiagnostics().forEach(d -> System.err.println(diagnosticToString(d)));
    if(success) {
      return new CfClassLoader(fileManager.getFiles(), loader);
    } else {
      throw new ScriptException("failed to compile java sources.\n" + diagnostics.getDiagnostics()
      .stream()
      .filter(d -> Kind.ERROR.equals(d.getKind()))
      .map(JavaC::diagnosticToString)
      .collect(Collectors.joining("\n")));
    }
  }

  public static void main(String[] args) throws Exception {
    List<JavaSource> sources = List.of(
        new JavaSource("HelloWorld", """
            public class HelloWorld {
              public static void main(String args[]) {
                System.out.println("This is in another java file");
                bar.Foo.sayHello();
              }
            }
            """),
        new JavaSource("bar.Foo", """
            package bar;
            public class Foo {
              int a = 1;
              public Foo(int b) {
                a = b;
                char c = '}';
                // }
              }
              public static void sayHello() {
                Integer i = new Integer(1);
                Double d = new Double(1.5);
                System.out.println("hello");
                //HelloWorld.main(null);
              }
            }
                """));
    ClassLoader cloader = compile(sources, JavaC.class.getClassLoader());
    cloader
    .loadClass("HelloWorld")
    .getDeclaredMethod("main", new Class[] { String[].class })
    .invoke(null, new Object[] { null });
  }
}