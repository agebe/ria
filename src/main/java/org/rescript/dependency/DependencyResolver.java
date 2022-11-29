package org.rescript.dependency;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;
import org.rescript.expression.Expression;
import org.rescript.expression.NewOp;
import org.rescript.parser.FunctionParameter;
import org.rescript.pom.MavenCoordinates;
import org.rescript.pom.MavenRepository;
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
      System.out.println("dependencies on classloader:");
      l.stream().map(f -> f.getName()).sorted().forEach(System.out::println);
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
    // FIXME repository need to be configurable
    MavenRepository mavenCentral = new MavenRepository("https://repo.maven.apache.org/maven2/");
    ScriptContext ctx = new ScriptContext(new SymbolTable());
    ctx.getSymbols().getScriptSymbols().setCtx(ctx);
    Function dependencyMain = Function.dependencies();
    ctx.getSymbols().getScriptSymbols().setMain(dependencyMain);
    ctx.enterFunction(ctx.getSymbols().getScriptSymbols().getMain());
    dependencyMain.addFunction(fileTree());
    final DependencyNode root = new DependencyNode();
    expressions.stream()
    .map(expr -> expr.eval(ctx))
    .map(val -> {
      if(val.val() instanceof String s) {
        // TODO guess by content of string, could also be a file or fileTree
        return new GradleShortDependency(s, mavenCentral);
      } else if(val.val() instanceof Dependency d) {
        return d;
      } else {
        throw new ScriptException(
            "dependency expression evaluated to '%s'".formatted(val.type())
            + " but expected an instanceof String or Dependency");
      }
    })
    .forEach(dep -> root.getChildren().addAll(dep.resolve()));
    // TODO do transitive dependencies next
    // make sure to resolve different versions of the same group:artifact
    // dependency node might require a type so jar and pom dependencies can be distinguished
    // https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#importing-dependencies
    // transitive dependencies
    // https://en.wikipedia.org/wiki/Breadth-first_search
    LinkedList<DependencyNode> queue = new LinkedList<>();
    // store group:artifact id in explored so we don't add the same library in different versions
    // as described below, first dependency wins
    // https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html
    Set<String> explored = new HashSet<>();
    List<DependencyNode> managedDependencies = new ArrayList<>();
    root.asList()
    .stream()
    .filter(DependencyNode::isManaged)
    .forEach(d -> {
      String gaId = d.groupArtifactId();
      if(!explored.contains(gaId)) {
        explored.add(gaId);
        queue.add(d);
      }
    });
    managedDependencies.addAll(queue);
    while(!queue.isEmpty()) {
      DependencyNode v = queue.remove();
      GradleShortDependency resolver = new GradleShortDependency(v.id(), mavenCentral);
      List<DependencyNode> transitiveDependencies = resolver.resolve();
      for(DependencyNode transitive : transitiveDependencies) {
        if(!explored.contains(transitive.groupArtifactId())) {
          explored.add(transitive.groupArtifactId());
          queue.add(transitive);
          managedDependencies.add(transitive);
        } else {
          System.out.println(
              "ignoring transitive dependency '%s', as we have this dependency already".formatted(transitive.id()));
        }
      }
    }
    return Stream.concat(managedDependencies.stream(), root.asList().stream().filter(d -> d.getFile() != null))
        .map(d -> toFile(d, mavenCentral))
        .filter(Objects::nonNull)
        .toList();
  }

  private File toFile(DependencyNode node, MavenRepository repo) {
    if(node.getFile() != null) {
      return node.getFile();
    }
    if(StringUtils.isBlank(node.getGroup())) {
      return null;
    }
    MavenCoordinates coord = new MavenCoordinates(node.getGroup(), node.getArtifact(), node.getVersion());
    try {
      return repo.fetchFile(coord, ".jar");
    } catch(Exception e) {
      throw new ScriptException("failed to fetch jar file for dependency " + coord, e);
    }
  }

}

