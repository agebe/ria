package org.rescript;

import org.rescript.parser.Parser;
import org.rescript.run.ScriptRunner;
import org.rescript.statement.Function;
import org.rescript.symbol.SymbolTable;
import org.rescript.symbol.VarSymbol;
import org.rescript.value.Value;

public class Script {

  private SymbolTable symbols = new SymbolTable();

  private String script;

  public Script() {
    super();
  }

  public Script(String script) {
    this.script = script;
  }

  private Value runVal() {
    try {
      if(script != null) {
        parse(script);
        script=null;
      }
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
    return parse(script).run();
  }

  public <T> T runReturning(Class<T> type) {
    Value val = runVal();
    return val.isNull()?null:type.cast(val.val());
  }

  public <T> T runReturning(String script, Class<T> type) {
    return parse(script).runReturning(type);
  }

  // TODO also add support for generic types, like e.g. List<String>

  public boolean evalPredicate() {
    return runVal().toBoolean();
  }

  public boolean evalPredicate(String script) {
    return parse(script).evalPredicate();
  }

  public double evalDouble() {
    return runVal().toDouble();
  }

  public double evalDouble(String script) {
    return parse(script).evalDouble();
  }

  public float evalFloat() {
    return runVal().toFloat();
  }

  public float evalFloat(String script) {
    return parse(script).evalFloat();
  }

  public long evalLong() {
    return runVal().toLong();
  }

  public long evalLong(String script) {
    return parse(script).evalLong();
  }

  public int evalInt() {
    return runVal().toInt();
  }

  public int evalInt(String script) {
    return parse(script).evalInt();
  }

  public char evalChar() {
    return runVal().toChar();
  }

  public int evalChar(String script) {
    return parse(script).evalChar();
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

  private Script parse(String script) {
    Function entry = new Parser().parse((this.script!=null?this.script:"")+script);
    this.symbols.getScriptSymbols().setMain(entry);
    this.script = null;
    return this;
  }

}
