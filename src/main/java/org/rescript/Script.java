package org.rescript;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

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
    try {
      parse(script);
      return new ScriptRunner(symbols).run();
    } catch(Exception e) {
      throw new ScriptException("script execution failed", e);
    }
  }

  public Object run() {
    Value v = runVal();
    return v!=null?v.val():null;
  }

  public Object run(String script) {
    return reparse(script).run();
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

  private URL toUrl(File f) {
    try {
      return f.toURI().toURL();
    } catch (MalformedURLException e) {
      throw new ScriptException("failed to convert file '%s' to url".formatted(f.getAbsolutePath()), e);
    }
  }

  private Script parse(String script) {
    if(this.entry == null) {
      ParserListener listener = new Parser(showErrorsOnConsole).parse(script);
      this.entry = listener.getMainFunction();
      this.symbols.getScriptSymbols().setMain(entry);
      List<URL> resolved = listener.getDependencies()
          .stream()
          .flatMap(dep -> dep.resolve().stream())
          .map(this::toUrl)
          .toList();
      if(!resolved.isEmpty()) {
        this.symbols.getJavaSymbols().setClassLoader(new URLClassLoader(
            "scriptClassLoader",
            resolved.toArray(new URL[0]),
            this.getClass().getClassLoader()));
      }
    }
    return this;
  }

  public boolean isShowErrorsOnConsole() {
    return showErrorsOnConsole;
  }

  public void setShowErrorsOnConsole(boolean showErrorsOnConsole) {
    this.showErrorsOnConsole = showErrorsOnConsole;
  }

}
