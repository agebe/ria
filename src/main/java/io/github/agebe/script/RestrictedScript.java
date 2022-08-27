package io.github.agebe.script;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

import io.github.agebe.script.antlr.ScriptParser;

public class RestrictedScript {

  private ScriptParser.ScriptContext tree;

  RestrictedScript(ScriptParser.ScriptContext tree) {
    this.tree = tree;
  }

  public Object run() {
    ParseTreeWalker.DEFAULT.walk(new MyScriptListener(), tree);
    return null;
  }

  public <T> T runReturning(Class<T> type) {
    return null;
  }

  // TODO also add support for generic types, like e.g. List<String>

  public boolean evalPredicate() {
    return true;
  }

  public double evalDouble() {
    return 0;
  }

  public float evalFloat() {
    return 0;
  }

  public long evalLong() {
    return 0;
  }

  public int evalInt() {
    return 0;
  }

}
