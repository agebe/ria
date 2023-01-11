package org.rescript.statement;

import java.util.ArrayList;
import java.util.List;

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

  private Repositories getRepos(ScriptContext ctx) {
    VarSymbol r = ctx.getSymbols().getScriptSymbols().resolveVar(HeaderEnterStatement.REPOSITORIES);
    if(r != null) {
      Object o = r.get().val();
      if(o instanceof Repositories repos) {
        return repos;
      }
    }
    return new Repositories();
  }

  private void resolveDependencies(ScriptContext ctx) {
    VarSymbol v = ctx.getSymbols().getScriptSymbols().resolveVar("dependencies");
    if(v != null) {
      Object o = v.get().val();
      if(o instanceof Dependencies dependencies) {
        ClassLoader dependencyClassLoader = new DependencyResolver(getRepos(ctx))
            .resolveAll(dependencies, scriptClassLoader);
        ClassLoader loader = dependencyClassLoader;
        ctx.getSymbols().getJavaSymbols().setClassLoader(loader);
      }
    }
  }

  private void addStdImports(ScriptContext ctx) {
    // TODO additional default imports should be configurable?
    // FIXME add a flag that disables additional default imports (other than java.lang.*) (language keyword for headers)
    // TODO all packages from direct dependencies should also be auto imported
    // add additional auto imports late so the user defined imports take precedence
    JavaSymbols symbols = ctx.getSymbols().getJavaSymbols();
    symbols.addImport("java.math.*");
    symbols.addImport("java.util.*");
    symbols.addImport("java.util.concurrent.*");
    symbols.addImport("java.util.concurrent.atomic.*");
    symbols.addImport("java.util.concurrent.locks.*");
    symbols.addImport("java.util.function.*");
    symbols.addImport("java.util.regex.*");
    symbols.addImport("java.util.stream.*");
    symbols.addImport("java.time.*");
    symbols.addImport("java.time.chrono.*");
    symbols.addImport("java.time.format.*");
    symbols.addImport("java.time.temporal.*");
    symbols.addImport("java.time.zone.*");
    symbols.addImport("java.io.*");
    symbols.addImport("java.net.*");
    symbols.addImport("java.nio.*");
    symbols.addImport("java.nio.channels.*");
    symbols.addImport("java.nio.charset.*");
    symbols.addImport("java.nio.file.*");
    symbols.addImport("java.nio.file.attribute.*");
    symbols.addImport("java.text.*");
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
    addStdImports(ctx);
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
