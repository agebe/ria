/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria.run;

import java.io.File;
import java.nio.file.Path;
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

  private Path scriptFile;

  public ScriptContext(SymbolTable symbols, Firewall firewall, Features features, Path scriptFile) {
    super();
    this.symbols = symbols;
    this.functions = new FunctionInvoker(this);
    this.firewall = firewall;
    this.features = features;
    this.scriptFile = scriptFile;
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

  public Path getScriptFile() {
    return scriptFile;
  }

  public void setScriptFile(Path scriptFile) {
    this.scriptFile = scriptFile;
  }

  public File getScriptDirectory() {
    return scriptFile!=null?scriptFile.toFile().getParentFile():null;
  }

}
