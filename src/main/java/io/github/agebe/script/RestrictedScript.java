package io.github.agebe.script;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

import io.github.agebe.script.antlr.ScriptParser;

public class RestrictedScript {

  private ScriptParser.ScriptContext tree;

  RestrictedScript(ScriptParser.ScriptContext tree) {
    this.tree = tree;
  }

  public Object run() {
//    ParseTreeWalker.DEFAULT.walk(new LogScriptListener(), tree);
    ParseTreeWalker.DEFAULT.walk(new ScriptExecutor(), tree);
    return null;
  }

  public <T> T runReturning(Class<T> type) {
    return null;
  }

  // TODO also add support for generic types, like e.g. List<String>

  public boolean evalPredicate() {
    run();
    // TODO implement
    return false;
  }

  public double evalDouble() {
    run();
 // TODO implement
    return 0;
  }

  public float evalFloat() {
    run();
 // TODO implement
    return 0;
  }

  public long evalLong() {
    run();
 // TODO implement
    return 0;
  }

  public int evalInt() {
    run();
 // TODO implement
    return 0;
  }

}
