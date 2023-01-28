package org.ria.run;

import java.util.ArrayDeque;
import java.util.Deque;

import org.ria.Features;
import org.ria.ScriptException;
import org.ria.firewall.DefaultFirewall;
import org.ria.firewall.Firewall;
import org.ria.statement.Function;
import org.ria.symbol.SymbolTable;
import org.ria.value.Value;
import org.ria.value.VoidValue;

public class ScriptContext {

  private static class Context {

    private Value lastResult = VoidValue.VOID;

    private boolean returnFlag;

    private Deque<Stackframe> functionStack = new ArrayDeque<>();

    // for parameter and return value passing into/from functions
    private Deque<Value> stack = new ArrayDeque<>();

    private boolean exit;

  }

  private SymbolTable symbols;

  private FunctionInvoker functions;

  private ThreadLocal<Context> contexts = ThreadLocal.withInitial(Context::new);

  private Firewall firewall;

  private Features features;

  public ScriptContext(SymbolTable symbols, Firewall firewall, Features features) {
    super();
    this.symbols = symbols;
    this.functions = new FunctionInvoker(this);
    this.firewall = firewall;
    this.features = features;
  }

  public Value getLastResult() {
    return contexts.get().lastResult;
  }

  public void setLastResult(Value lastResult) {
    if(lastResult == null) {
      throw new ScriptException("last result can not be null");
    }
    contexts.get().lastResult = lastResult;
  }

  public SymbolTable getSymbols() {
    return symbols;
  }

  public FunctionInvoker getFunctions() {
    return functions;
  }

  public boolean isReturnFlag() {
    return contexts.get().returnFlag;
  }

  public void setReturnFlag(boolean returnFlag) {
    contexts.get().returnFlag = returnFlag;
  }

  public void enterFunction(Function function) {
    contexts.get().functionStack.push(new Stackframe(function));
  }

  public void exitFunction(Function function) {
    Stackframe frame = contexts.get().functionStack.pop();
    if(frame.getFunction() != function) {
      throw new ScriptException("expected function '%s' but got '%s'".formatted(function, frame.getFunction()));
    }
  }

  public Function currentFunction() {
    return contexts.get().functionStack.peek().getFunction();
  }

  public Stackframe getCurrentFrame() {
    return contexts.get().functionStack.peek();
  }

  public Deque<Value> getStack() {
    return contexts.get().stack;
  }

  public StackTraceElement[] getStackTrace() {
    return contexts.get().functionStack
        .stream()
        // TODO if the script was executed from a file the file name should appear in the stack trace instead of 'inline' 
        .map(sf -> new StackTraceElement("script", sf.getFunction().getName(), "inline", sf.getLine()))
        .toArray(StackTraceElement[]::new);
  }

  public void setExit() {
    contexts.get().exit = true;
  }

  public boolean isExit() {
    return contexts.get().exit;
  }

  public Firewall getFirewall() {
    return firewall;
  }

  public void setFirewall(DefaultFirewall firewall) {
    this.firewall = firewall;
  }

  public Features getFeatures() {
    return features;
  }

  public void setFeatures(Features features) {
    this.features = features;
  }

}
