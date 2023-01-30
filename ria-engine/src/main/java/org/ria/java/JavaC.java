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
package org.ria.java;

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
import org.ria.ScriptException;
import org.ria.cloader.CLoader;
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

  public static ClassLoader compile(List<? extends JavaFileObject> compilationUnits, ClassLoader loader, boolean quiet) {
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
    if(!quiet) {
      diagnostics.getDiagnostics().forEach(d -> System.err.println(diagnosticToString(d)));
    }
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

}
