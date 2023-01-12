package org.rescript.statement;

import java.util.ArrayList;
import java.util.List;

import org.rescript.Options;
import org.rescript.ScriptException;
import org.rescript.dependency.Dependencies;
import org.rescript.dependency.DependencyResolver;
import org.rescript.dependency.Repositories;
import org.rescript.java.JavaC;
import org.rescript.java.JavaSource;
import org.rescript.java.JavaSourceBuilder;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.VarSymbol;
import org.rescript.symbol.java.JavaSymbols;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderExitStatement extends AbstractStatement {

  private static final Logger log = LoggerFactory.getLogger(HeaderExitStatement.class);

  private List<JavaSourceBuilder> javaTypes = new ArrayList<>();

  private ClassLoader scriptClassLoader;

  public HeaderExitStatement(int lineNumber, ClassLoader scriptClassLoader) {
    super(lineNumber);
    this.scriptClassLoader = scriptClassLoader;
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

  private void resolveDependencies(ScriptContext ctx) {
    Repositories repos = resolve(ctx, HeaderEnterStatement.REPOSITORIES, Repositories.class);
    if(repos == null) {
      throw new ScriptException("no maven repositories have been setup");
    }
    Dependencies dependencies = resolve(ctx, HeaderEnterStatement.DEPENDENCIES, Dependencies.class);
    if(dependencies != null) {
      ClassLoader dependencyClassLoader = new DependencyResolver(repos)
          .resolveAll(dependencies, scriptClassLoader);
      ClassLoader loader = dependencyClassLoader;
      ctx.getSymbols().getJavaSymbols().setClassLoader(loader);
      // TODO all packages from direct dependencies should also be auto imported
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
    resolveDependencies(ctx);
    addDefaultImports(ctx);
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
