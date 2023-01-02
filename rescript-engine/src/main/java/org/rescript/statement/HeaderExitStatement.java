package org.rescript.statement;

import java.util.ArrayList;
import java.util.List;

import org.rescript.java.JavaC;
import org.rescript.java.JavaSource;
import org.rescript.java.JavaSourceBuilder;
import org.rescript.run.ScriptContext;
import org.rescript.symbol.java.JavaSymbols;

public class HeaderExitStatement extends AbstractStatement {

  private List<JavaSourceBuilder> javaTypes = new ArrayList<>();

  public HeaderExitStatement(int lineNumber) {
    super(lineNumber);
  }

  @Override
  public void execute(ScriptContext ctx) {
    JavaSymbols symbols = ctx.getSymbols().getJavaSymbols();
    // TODO additional default imports should be configurable?
    // FIXME add a flag that disables additional default imports (other than java.lang.*) (language keyword for headers)
    // TODO all packages from direct dependencies should also be auto imported
    // add additional auto imports late so the user defined imports take precedence
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
    if(!javaTypes.isEmpty()) {
      List<JavaSource> l = javaTypes.stream()
          .map(builder -> toJavaSource(builder, ctx))
          .toList();
      ClassLoader loader = JavaC.compile(l);
      ctx.getSymbols().getJavaSymbols().setClassLoader(loader);
    }
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
