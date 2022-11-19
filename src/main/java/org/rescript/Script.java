package org.rescript;

import org.rescript.parser.Parser;
import org.rescript.run.ScriptRunner;
import org.rescript.statement.Function;
import org.rescript.symbol.SymbolTable;
import org.rescript.symbol.VarSymbol;
import org.rescript.value.Value;

public class Script {

  private SymbolTable symbols;

  private String script;

  private Function entry;

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

  public int evalChar(String script) {
    return reparse(script).evalChar();
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
      this.entry = new Parser().parse(script);
      this.symbols.getScriptSymbols().setMain(entry);
    }
    return this;
  }

}
