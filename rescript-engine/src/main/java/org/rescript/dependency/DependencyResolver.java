package org.rescript.dependency;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    BlockStatement block = new BlockStatement(0);
    FunctionParameter p0 = new FunctionParameter(
        ctx -> ctx.getSymbols().getScriptSymbols().resolveVar("baseDir").getVal());
    NewOp n = new NewOp(FileTreeDependency.class.getName(), List.of(p0));
    block.addStatement(new ExpressionStatement(0, n));
    Function f = new Function(0);
    f.setName("fileTree");
    f.setParameterNames(List.of("baseDir"));
    f.setStatements(block);
    return f;
  }

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
    // TODO add support for variables and variable replacements in the dependencies e.g. for versions
    expressions.stream()
    .map(expr -> expr.eval(ctx))
    .map(val -> {
      if(val.val() instanceof String s) {
        return new MultiFormatDependency(s);
      } else if(val.val() instanceof Dependency d) {
        return d;
      } else {
        throw new ScriptException(
            "dependency expression evaluated to '%s'".formatted(val.type())
            + " but expected an instanceof String or Dependency");
      }
    })
    .forEach(dep -> root.addChildren(dep.resolve()));
    // make sure to resolve different versions of the same group:artifact
    // dependency node might require a type so jar and pom dependencies can be distinguished
    // https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#importing-dependencies
    // transitive dependencies
    // https://en.wikipedia.org/wiki/Breadth-first_search
    LinkedList<DependencyNode> queue = new LinkedList<>();
    // store group:artifact id in explored so we don't add the same library in different versions
    // as described below, first dependency wins
    // https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html
    Map<String, String> explored = new HashMap<>();
    List<DependencyNode> managedDependencies = new ArrayList<>();
    root.asList()
    .stream()
    .filter(DependencyNode::isManaged)
    .forEach(d -> {
      String gaId = d.groupArtifactId();
      if(!explored.containsKey(gaId)) {
        explored.put(gaId, d.getVersion());
        queue.add(d);
      }
    });
    // ignore the optional flag on all direct dependencies
    // also direct dependency can not be excluded
    managedDependencies.addAll(queue);
//    managedDependencies.forEach(System.out::println);
    while(!queue.isEmpty()) {
      DependencyNode v = queue.remove();
      PomDependency resolver = new PomDependency(v.id(), mavenCentral);
      List<DependencyNode> transitiveDependencies = resolver.resolve();
      v.addChildren(transitiveDependencies);
      for(DependencyNode transitive : transitiveDependencies) {
        if(transitive.isOptional()) {
//          System.out.println("ignoring optional transitive dependeny '%s' from this path ".formatted(transitive.id()) + "TODO");
        } else if(v.isExcluded(transitive)) {
//          System.out.println("transitive dependeny '%s' is excluded from this path ".formatted(transitive.id()) + "TODO");
        } else {
          if(!explored.containsKey(transitive.groupArtifactId())) {
            explored.put(transitive.groupArtifactId(), transitive.getVersion());
            queue.add(transitive);
            managedDependencies.add(transitive);
          } else {
            // in this case even if a dependency is already explored we have to go down the dependency path anyway
            // because it may not contain the same exclusions.
            // make sure to use the same version as the explored dependency though
            // i think the dependency node needs to be merged. id from the explored, but exclusion list from the current node
            DependencyNode n2 = new DependencyNode(
                transitive.getGroup(),
                transitive.getArtifact(),
                explored.get(transitive.groupArtifactId()),
                transitive.getExclusions(),
                false);
            v.addChild(n2);
            queue.add(n2);
          }
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

