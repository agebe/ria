package org.rescript.run;

import java.util.ArrayDeque;
import java.util.Deque;

import org.rescript.ScriptException;
import org.rescript.statement.Breakable;
import org.rescript.statement.Continueable;
import org.rescript.statement.Function;

public class Stackframe {

  private Function function;

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

}
