package org.rescript;

import org.rescript.dependency.DependencyResolver;
import org.rescript.parser.Parser;
import org.rescript.parser.ParserListener;
import org.rescript.run.ScriptRunner;
import org.rescript.statement.Function;
import org.rescript.symbol.SymbolTable;
import org.rescript.symbol.VarSymbol;
import org.rescript.value.Value;

public class Script {

  private SymbolTable symbols;

  private String script;

  private Function entry;

  private boolean showErrorsOnConsole;

  private String[] arguments;

  // the class loader to load types used by the script
  private ClassLoader scriptClassLoader = this.getClass().getClassLoader();

  public Script() {
    this(null, null);
  }

  public Script(String script) {
    this(script, null);
  }

  public Script(String script, SymbolTable symbols) {
    super();
    this.script = script;
    this.symbols = symbols!=null?symbols:new SymbolTable();
  }

  private Value runVal() {
    ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
    try {
      parse(script);
      setupArguments();
      // some libraries like e.g. kafka prefer to use the context class loader
      // so set it up here but restore to the previous context class loader when the script is done executing
      Thread.currentThread().setContextClassLoader(this.symbols.getJavaSymbols().getClassLoader());
      return new ScriptRunner(symbols).run();
    } finally {
      Thread.currentThread().setContextClassLoader(ctxLoader);
    }
  }

  private void setupArguments() {
    if(this.arguments != null) {
      String[] a = new String[arguments.length-1];
      System.arraycopy(arguments, 1, a, 0, arguments.length-1);
      for(int i=0;i<a.length;i++) {
        this.setVariable("$"+i, a[i]);
      }
      this.setVariable("$", a);
    } else {
      this.setVariable("$", new String[0]);
    }
  }

  public Object run() {
    Value v = runVal();
    return v!=null?v.val():null;
  }

  public Object runUnwrapException() throws Throwable {
    try {
      return run();
    } catch(CheckedExceptionWrapper e) {
      throw e.getCause();
    }
  }

  public Object run(String script) {
    return reparse(script).run();
  }

  public Object runUnwrapException(String script) throws Throwable {
    try {
      return run(script);
    } catch(CheckedExceptionWrapper e) {
      throw e.getCause();
    }
  }

  public <T> T runReturning(Class<T> type) {
    Value val = runVal();
    return val.isNull()?null:type.cast(val.val());
  }

  public <T> T runReturning(String script, Class<T> type) {
    return reparse(script).runReturning(type);
  }

  // TODO also add support for generic types, like e.g. List<String>

  public boolean evalPredicate() {
    return runVal().toBoolean();
  }

  public boolean evalPredicate(String script) {
    return reparse(script).evalPredicate();
  }

  public double evalDouble() {
    return runVal().toDouble();
  }

  public double evalDouble(String script) {
    return reparse(script).evalDouble();
  }

  public float evalFloat() {
    return runVal().toFloat();
  }

  public float evalFloat(String script) {
    return reparse(script).evalFloat();
  }

  public long evalLong() {
    return runVal().toLong();
  }

  public long evalLong(String script) {
    return reparse(script).evalLong();
  }

  public int evalInt() {
    return runVal().toInt();
  }

  public int evalInt(String script) {
    return reparse(script).evalInt();
  }

  public char evalChar() {
    return runVal().toChar();
  }

  public char evalChar(String script) {
    return reparse(script).evalChar();
  }

  public short evalShort() {
    return runVal().toShort();
  }

  public short evalShort(String script) {
    return reparse(script).evalShort();
  }

  public byte evalByte() {
    return runVal().toByte();
  }

  public byte evalByte(String script) {
    return reparse(script).evalByte();
  }

  public void setVariable(String name, Object val) {
    symbols.getScriptSymbols().defineOrAssignVarRoot(name, Value.of(val));
  }

  public Object getVariable(String name) {
    Value val = symbols.getScriptSymbols().getVariable(name);
    return val!=null?val.val():null;
  }

  public Object unsetVariable(String name) {
    VarSymbol s = symbols.getScriptSymbols().unsetRoot(name);
    return s!=null?s.getObjectOrNull():null;
  }

  private Script reparse(String script) {
    this.entry = null;
    return parse(script);
  }

  private Script parse(String script) {
    if(this.entry == null) {
      ParserListener listener = new Parser(showErrorsOnConsole).parse(script);
      this.entry = listener.getMainFunction();
      this.symbols.getScriptSymbols().setMain(entry);
      ClassLoader loader = new DependencyResolver().resolveAll(listener.getDependencies(), scriptClassLoader);
      this.symbols.getJavaSymbols().setClassLoader(loader);
    }
    return this;
  }

  public boolean isShowErrorsOnConsole() {
    return showErrorsOnConsole;
  }

  public void setShowErrorsOnConsole(boolean showErrorsOnConsole) {
    this.showErrorsOnConsole = showErrorsOnConsole;
  }

  public String[] getArguments() {
    return arguments;
  }

  public void setArguments(String[] arguments) {
    this.arguments = arguments;
  }

  public ClassLoader getScriptClassLoader() {
    return scriptClassLoader;
  }

  public void setScriptClassLoader(ClassLoader scriptClassLoader) {
    this.scriptClassLoader = scriptClassLoader;
  }

}
