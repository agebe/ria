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
package org.ria.statement;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.ria.Options;
import org.ria.ScriptException;
import org.ria.cloader.CLoader;
import org.ria.dependency.Dependencies;
import org.ria.dependency.DependencyNode;
import org.ria.dependency.DependencyResolver;
import org.ria.dependency.Repositories;
import org.ria.java.JavaC;
import org.ria.java.JavaSource;
import org.ria.java.JavaSourceBuilder;
import org.ria.pom.DependencyOptions;
import org.ria.run.ScriptContext;
import org.ria.symbol.VarSymbol;
import org.ria.symbol.java.JavaSymbols;
import org.ria.util.PackageNameUtils;
import org.ria.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderExitStatement extends AbstractStatement {

  private static final Logger log = LoggerFactory.getLogger(HeaderExitStatement.class);

  private List<JavaSourceBuilder> javaTypes = new ArrayList<>();

  private ClassLoader scriptClassLoader;

  private boolean downloadDependenciesOnly;

  private boolean printDependencies;

  private boolean quiet;

  public HeaderExitStatement(
      int lineNumber,
      ClassLoader scriptClassLoader,
      boolean downloadDependenciesOnly,
      boolean printDependencies,
      boolean quiet) {
    super(lineNumber);
    this.scriptClassLoader = scriptClassLoader;
    this.downloadDependenciesOnly = downloadDependenciesOnly;
    this.printDependencies = printDependencies;
    this.quiet = quiet;
  }

  private <T> T resolve(ScriptContext ctx, String name, Class<T> cls) {
    VarSymbol r = ctx.getSymbols().getScriptSymbols().resolveVar(name);
    if(r != null) {
      Object o = r.get().val();
      if(cls.isAssignableFrom(o.getClass())) {
        return cls.cast(o);
      }
    }
    return null;
  }

  private File canonical(File f) {
    try {
      return f.getCanonicalFile();
    } catch (IOException e) {
      throw new ScriptException("failed to get canonical file of " + f.getAbsolutePath());
    }
  }

  private List<File> allJars(DependencyNode root) {
    return root.asList()
        .stream()
        .map(DependencyNode::getFile)
        .filter(Objects::nonNull)
        .map(this::canonical)
        .distinct()
        .toList();
  }

  private URL toUrl(File f) {
    try {
      return f.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new ScriptException("failed to convert file '%s' to url".formatted(f.getAbsolutePath()), e);
    }
  }

  private List<File> jarsToImport(DependencyNode root) {
    return jarsToImport(root, new ArrayList<>());
  }

  private List<File> jarsToImport(DependencyNode node, List<File> result) {
    List<DependencyNode> l = node.getChildren();
    // import all packages from direct dependencies.
    // if the direct dependencies does not have a jar file (e.g. pom only dependency) then
    // import the packages of children of that dependency recursively until there is a jar file or no more dependencies
    // exist
    for(DependencyNode c : l) {
      if(c.getFile() != null) {
        result.add(c.getFile());
      } else {
        jarsToImport(c, result);
      }
    }
    return result;
  }

  private boolean importFromDependencies(ScriptContext ctx) {
    Options options = resolve(ctx, HeaderEnterStatement.OPTIONS, Options.class);
    if(options == null) {
      log.debug("options is null");
      return true;
    } else {
      return options.importDependencies;
    }
  }

  private boolean filterOut(String s, Set<Pattern> filters) {
    boolean result = filters.stream().anyMatch(p -> p.matcher(s).matches());
    if(result) {
      log.debug("path '{}' is filtered out on importDependenciesFilter filters", s, result);
    }
    return result;
  }

  private Set<String> getPackages(File f, Set<String> filters) {
    Set<Pattern> patterns = filters.stream()
        .map(Pattern::compile)
        .collect(Collectors.toSet());
    try(ZipFile zip = new ZipFile(f)) {
      return zip.stream()
          .map(ZipEntry::getName)
          .map(StringUtils::strip)
          .filter(name -> StringUtils.endsWith(name, ".class"))
          .filter(name -> !filterOut(name, patterns))
          .map(FilenameUtils::getPathNoEndSeparator)
          .map(path -> StringUtils.replaceChars(path, '/', '.'))
          .filter(StringUtils::isNotBlank)
          .filter(PackageNameUtils::isPackageNameValid)
          .collect(Collectors.toSet());
    } catch (Exception e) {
      log.debug("failed to extract package names from '{}'", f, e);
      return Set.of();
    }
  }

  private void importDependencies(DependencyNode root, ScriptContext ctx) {
    Options options = resolve(ctx, HeaderEnterStatement.OPTIONS, Options.class);
    Set<String> filters = options!=null?options.importDependenciesFilter:Set.of();
    JavaSymbols sym = ctx.getSymbols().getJavaSymbols();
    // TODO all packages from direct dependencies should also be auto imported
    List<File> jars = jarsToImport(root);
    jars.stream()
    .flatMap(jar -> getPackages(jar, filters).stream())
    .distinct()
    .sorted()
    .forEachOrdered(pkg -> {
      String p = pkg+".*";
      log.debug("adding package '{}'", p);
      sym.addImport(p);
    });
  }

  private void resolveDependencies(ScriptContext ctx) {
    Repositories repos = resolve(ctx, HeaderEnterStatement.REPOSITORIES, Repositories.class);
    if(repos == null) {
      throw new ScriptException("no maven repositories have been setup");
    }
    Dependencies dependencies = resolve(ctx, HeaderEnterStatement.DEPENDENCIES, Dependencies.class);
    if((dependencies != null) && dependencies.hasDependencies()) {
      if(ctx.getFeatures().isDependenciesEnabled()) {
        DependencyOptions.setQuiet(quiet);
        DependencyNode root = new DependencyResolver(repos).resolveAll(dependencies);
        List<File> allJars = allJars(root);
        if(!allJars.isEmpty()) {
          if(printDependencies) {
            allJars.stream()
              .map(f -> f.getAbsolutePath())
              .sorted()
              .forEach(System.err::println);
          }
          CLoader loader = new CLoader(
              "scriptClassLoader",
              allJars.stream().map(this::toUrl).toArray(URL[]::new),
              scriptClassLoader);
          log.debug("set script classloader '{}'", loader);
          ctx.getSymbols().getJavaSymbols().setClassLoader(loader);
          if(importFromDependencies(ctx)) {
            importDependencies(root, ctx);
          }
        }
      } else if(!quiet) {
        System.err.println("dependencies are disabled (via script features)");
      }
    } else {
      log.debug("no dependencies");
    }
  }

  private void addDefaultImports(ScriptContext ctx) {
    Options options = resolve(ctx, HeaderEnterStatement.OPTIONS, Options.class);
    if(options == null) {
      log.debug("options is null");
      return;
    }
    if(!options.defaultImportsEnabled) {
      log.debug("default imports disabled");
      return;
    }
    JavaSymbols symbols = ctx.getSymbols().getJavaSymbols();
    if(options.defaultImports != null) {
      // add additional auto imports late so the user defined imports take precedence
      options.defaultImports.forEach(symbols::addImport);
    } else {
      log.debug("default imports list is null");
    }
    if(options.defaultStaticImports != null) {
      options.defaultStaticImports.forEach(symbols::addStaticImport);
    } else {
      log.debug("default static imports list is null");
    }
  }

  private void compileJavaTypes(ScriptContext ctx) {
    JavaSymbols symbols = ctx.getSymbols().getJavaSymbols();
    if(!javaTypes.isEmpty()) {
      if(ctx.getFeatures().isJavaSourceEnabled()) {
        List<JavaSource> l = javaTypes.stream()
            .map(builder -> builder.create(ctx))
            .peek(source -> log.debug("source of type '{}':\n{}", source.getName(), source.getCharContent(true)))
            .toList();
        ClassLoader loader = JavaC.compile(l, symbols.getClassLoader(), quiet);
        symbols.setClassLoader(loader);
      } else if(!quiet) {
        System.err.println("java sources are disabled (via script features)");
      }
    }
  }

  private void defineImportsVariable(ScriptContext ctx) {
    StringBuilder b = new StringBuilder("\n");
    ctx.getSymbols().getJavaSymbols().getStaticImports().forEach(i -> b.append("import static "+i+";\n"));
    ctx.getSymbols().getJavaSymbols().getImports().forEach(i -> b.append("import "+i+";\n"));
    b.append("\n");
    ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot("$imports", Value.of(b.toString()));
  }

  @Override
  public void execute(ScriptContext ctx) {
    log.trace("enter header exit statement");
    addDefaultImports(ctx);
    resolveDependencies(ctx);
    if(downloadDependenciesOnly) {
      ctx.setExit();
      // skip compiling java types as we exit anyway
      return;
    }
    defineImportsVariable(ctx);
    compileJavaTypes(ctx);
    JavaSymbols symbols = ctx.getSymbols().getJavaSymbols();
    // some libraries like e.g. kafka prefer to use the context class loader
    // so set it up here but restore to the previous context class loader when the script is done executing
    // the restoring is done in Script.runVal
    Thread.currentThread().setContextClassLoader(symbols.getClassLoader());
    ctx.getSymbols().getScriptSymbols().defineOrAssignVarRoot("scriptClassLoader", Value.of(symbols.getClassLoader()));
  }

  public void addJavaType(JavaSourceBuilder source) {
    javaTypes.add(source);
  }

}
