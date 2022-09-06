package io.github.agebe.script.run;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.agebe.script.ScriptException;
import io.github.agebe.script.parser.AstNode;
import io.github.agebe.script.parser.FunctionCallStatement;
import io.github.agebe.script.parser.Statement;
import io.github.agebe.script.symbol.SymbolTable;

public class ScriptRunner {

  private static final Logger log = LoggerFactory.getLogger(ScriptRunner.class);

  private SymbolTable symbols;

  private Expressions expressions;

  private Value lastResult;

  public ScriptRunner(SymbolTable symbols) {
    super();
    this.symbols = symbols;
    this.expressions = new Expressions(symbols);
  }

  public Value run() {
    return run(symbols.getEntryPoint());
  }

  private Value run(AstNode current) {
    lastResult = new VoidValue();
    while(current != null) {
      log.debug("exec next statement '{}'", current);
      Value v = executeStatement(current.getStmt());
      if(v != null) {
        lastResult = v;
      }
      current = getPath(current, v);
    }
    log.debug("exit script run");
    return lastResult;
  }

  private AstNode getPath(AstNode node, Value val) {
    if(Objects.isNull(node.getTrueNode()) && Objects.isNull(node.getFalseNode())) {
      return null;
    } else if(Objects.isNull(node.getFalseNode())) {
      return node.getTrueNode();
    } else if(Objects.isNull(node.getTrueNode())) {
      return node.getFalseNode();
    } else {
      if(val == null) {
        throw new ScriptException("no result on statement '%s'".formatted(node.getStmt()));
      }
      if(!val.isBoolean()) {
        throw new ScriptException("no boolean result on statement '%s' but '%s'".formatted(
            node.getStmt(), val.type()));
      }
      return node.next(val.toBoolean());
    }
  }

  private Value executeStatement(Statement stmt) {
    if(stmt instanceof FunctionCallStatement) {
      return expressions.execute(((FunctionCallStatement)stmt).getFunction());
    }
    throw new ScriptException("statement execution not implemeneted '%s'".formatted(stmt));
  }

}
