package org.rescript.dependency;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.rescript.ScriptException;
import org.rescript.expression.Expression;
import org.rescript.expression.NewOp;
import org.rescript.parser.FunctionParameter;
import org.rescript.run.ScriptContext;
import org.rescript.statement.BlockStatement;
import org.rescript.statement.ExpressionStatement;
import org.rescript.statement.Function;
import org.rescript.symbol.SymbolTable;

public class DependencyResolver {

  private URL toUrl(File f) {
    try {
      return f.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new ScriptException("failed to convert file '%s' to url".formatted(f.getAbsolutePath()), e);
    }
  }

  public ClassLoader resolveAll(List<Expression> dependencies) {
    List<File> l = resolveDependencies(dependencies);
    if(l.isEmpty()) {
      return null;
    } else {
      return new URLClassLoader(
          "scriptClassLoader",
          l.stream().map(this::toUrl).toArray(URL[]::new),
          this.getClass().getClassLoader());
    }
  }

  private Function fileTree() {
    BlockStatement block = new BlockStatement();
    FunctionParameter p0 = new FunctionParameter(
        ctx -> ctx.getSymbols().getScriptSymbols().resolveVar("baseDir").getVal());
    NewOp n = new NewOp(FileTreeDependency.class.getName(), List.of(p0));
    block.addStatement(new ExpressionStatement(n));
    Function f = new Function();
    f.setName("fileTree");
    f.setParameterNames(List.of("baseDir"));
    f.setStatements(block);
    return f;
  }

  // TODO add function file(string) to resolve a single file
  // TODO add function files([string]) to resolve all files in the list

  private List<File> resolveDependencies(List<Expression> expressions) {
    ScriptContext ctx = new ScriptContext(new SymbolTable());
    ctx.getSymbols().getScriptSymbols().setCtx(ctx);
    Function dependencyMain = Function.dependencies();
    ctx.getSymbols().getScriptSymbols().setMain(dependencyMain);
    ctx.enterFunction(ctx.getSymbols().getScriptSymbols().getMain());
    dependencyMain.addFunction(fileTree());
    return expressions.stream()
        .map(expr -> expr.eval(ctx))
        .map(val -> {
          if(val.val() instanceof String s) {
            return new GradleShortDependency(s);
          } else if(val.val() instanceof Dependency d) {
            return d;
          } else {
            throw new ScriptException(
                "dependency expression evaluated to '%s'".formatted(val.type())
                + " but expected an instanceof String or Dependency");
          }
        })
        .flatMap(dep -> dep.resolve().stream())
        .toList();
  }

}
