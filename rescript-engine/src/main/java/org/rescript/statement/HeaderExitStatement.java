package org.rescript.statement;

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
import org.rescript.Options;
import org.rescript.ScriptException;
import org.rescript.cloader.CLoader;
import org.rescript.dependency.Dependencies;
import org.rescript.dependency.DependencyNode;
import org.rescript.dependency.DependencyResolver;
import org.rescript.dependency.Repositories;
import org.rescript.java.JavaC;
import org.rescript.java.JavaSource;
import org.rescript.java.JavaSourceBuilder;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.VarSymbol;
import org.rescript.symbol.java.JavaSymbols;
import org.rescript.util.PackageNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderExitStatement extends AbstractStatement {

  private static final Logger log = LoggerFactory.getLogger(HeaderExitStatement.class);

  private List<JavaSourceBuilder> javaTypes = new ArrayList<>();

  private ClassLoader scriptClassLoader;

  private boolean downloadDependenciesOnly;

  private boolean printDependencies;

  public HeaderExitStatement(
      int lineNumber,
      ClassLoader scriptClassLoader,
      boolean downloadDependenciesOnly,
      boolean printDependencies) {
    super(lineNumber);
    this.scriptClassLoader = scriptClassLoader;
    this.downloadDependenciesOnly = downloadDependenciesOnly;
    this.printDependencies = printDependencies;
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
    if(dependencies != null) {
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
        ctx.getSymbols().getJavaSymbols().setClassLoader(loader);
        if(importFromDependencies(ctx)) {
          importDependencies(root, ctx);
        }
      }
    } else {
      log.debug("dependencies is null");
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
    if(options.defaultImports == null) {
      log.debug("default imports list is null");
      return;
    }
    JavaSymbols symbols = ctx.getSymbols().getJavaSymbols();
    // add additional auto imports late so the user defined imports take precedence
    options.defaultImports.forEach(symbols::addImport);
  }

  private void compileJavaTypes(ScriptContext ctx) {
    JavaSymbols symbols = ctx.getSymbols().getJavaSymbols();
    if(!javaTypes.isEmpty()) {
      List<JavaSource> l = javaTypes.stream()
          .map(builder -> toJavaSource(builder, ctx))
          .peek(source -> log.debug("source of type '{}':\n{}", source.getName(), source.getCharContent(true)))
          .toList();
      ClassLoader loader = JavaC.compile(l, symbols.getClassLoader());
      symbols.setClassLoader(loader);
    }
  }

  @Override
  public void execute(ScriptContext ctx) {
    addDefaultImports(ctx);
    resolveDependencies(ctx);
    if(downloadDependenciesOnly) {
      ctx.setExit();
      // skip compiling java types as we exit anyway
      return;
    }
    compileJavaTypes(ctx);
    JavaSymbols symbols = ctx.getSymbols().getJavaSymbols();
    // some libraries like e.g. kafka prefer to use the context class loader
    // so set it up here but restore to the previous context class loader when the script is done executing
    // the restoring is done in Script.runVal
    Thread.currentThread().setContextClassLoader(symbols.getClassLoader());
  }

  private JavaSource toJavaSource(JavaSourceBuilder builder, ScriptContext ctx) {
    ctx.getSymbols().getJavaSymbols().getStaticImports().forEach(builder::addStaticImport);
    ctx.getSymbols().getJavaSymbols().getImports().forEach(builder::addImport);
    return builder.create();
  }

  public void addJavaType(JavaSourceBuilder source) {
    javaTypes.add(source);
  }

}
