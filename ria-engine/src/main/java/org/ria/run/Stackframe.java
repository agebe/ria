package org.ria.run;

import java.util.ArrayDeque;
import java.util.Deque;

import org.ria.ScriptException;
import org.ria.statement.Breakable;
import org.ria.statement.Continueable;
import org.ria.statement.Function;

public class Stackframe {

  private Function function;

  private int line;

  private Deque<Breakable> breakStack = new ArrayDeque<>();

  private Deque<Continueable> continueStack = new ArrayDeque<>();

  public Stackframe(Function function) {
    super();
    this.function = function;
  }

  public Function getFunction() {
    return function;
  }

  public void pushBreakable(Breakable breakable) {
    breakStack.push(breakable);
  }

  public Breakable popBreakable(Breakable breakable) {
    Breakable b = breakStack.pop();
    if(breakable != null) {
      if(breakable != b) {
        throw new ScriptException("internal, wrong breakable");
      }
    }
    return b;
  }

  public Breakable peekBreakable() {
    return breakStack.peek();
  }

  public void pushContinueable(Continueable continueable) {
    continueStack.push(continueable);
  }

  public Continueable popContinueable(Continueable continueable) {
    Continueable c = continueStack.pop();
    if(continueable != null) {
      if(continueable != c) {
        throw new ScriptException("internal, wrong continueable");
      }
    }
    return c;
  }

  public Continueable peekContinueable() {
    return continueStack.peek();
  }

  public int getLine() {
    return line;
  }

  public void setLine(int line) {
    this.line = line;
  }

}
