package org.rescript.run;

import java.util.ArrayDeque;
import java.util.Deque;

import org.rescript.ScriptException;
import org.rescript.statement.Function;
import org.rescript.statement.Statement;
import org.rescript.symbol.SymbolTable;
import org.rescript.value.Value;
import org.rescript.value.VoidValue;

public class ScriptContext {

  private SymbolTable symbols;

  private FunctionCaller functions;

  private Statement current;

  private Value lastResult = VoidValue.VOID;

  private boolean returnFlag;

  private Deque<Stackframe> functionStack = new ArrayDeque<>();

  // for parameter and return value passing into/from functions
  private Deque<Value> stack = new ArrayDeque<>();

  public ScriptContext(SymbolTable symbols) {
    super();
    this.symbols = symbols;
    this.functions = new FunctionCaller(this);
    this.current = symbols.getScriptSymbols().getMain();
  }

  public Statement getCurrent() {
    return current;
  }

  public void setCurrent(Statement current) {
    this.current = current;
  }

  public Value getLastResult() {
    return lastResult;
  }

  public void setLastResult(Value lastResult) {
    this.lastResult = lastResult;
  }

  public SymbolTable getSymbols() {
    return symbols;
  }

  public FunctionCaller getFunctions() {
    return functions;
  }

  public boolean isReturnFlag() {
    return returnFlag;
  }

  public void setReturnFlag(boolean returnFlag) {
    this.returnFlag = returnFlag;
  }

  public void enterFunction(Function function) {
    functionStack.push(new Stackframe(function));
  }

  public void exitFunction(Function function) {
    Stackframe frame = functionStack.pop();
    if(frame.getFunction() != function) {
      throw new ScriptException("expected function '%s' but got '%s'".formatted(function, frame.getFunction()));
    }
  }

  public Function currentFunction() {
    return functionStack.peek().getFunction();
  }

  public Stackframe getCurrentFrame() {
    return functionStack.peek();
  }

  public Deque<Value> getStack() {
    return stack;
  }

}
