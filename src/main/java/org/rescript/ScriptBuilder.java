package org.rescript;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.util.JImmutables;
import org.rescript.parser.Parser;
import org.rescript.symbol.SymbolTable;

public class ScriptBuilder {

  private SymbolTable symbols;

  private JImmutableList<String> importList = JImmutables.list();

  private JImmutableList<String> importStaticList = JImmutables.list();

  private JImmutableMap<String, String> functionAlias = JImmutables.map();

  public ScriptBuilder() {
    super();
  }

  private ScriptBuilder(
      SymbolTable symbols,
      JImmutableList<String> importList,
      JImmutableList<String> importStaticList,
      JImmutableMap<String, String> functionAlias) {
    super();
    this.symbols = symbols;
    this.importList = importList;
    this.importStaticList = importStaticList;
    this.functionAlias = functionAlias;
  }

  // TODO set variables
  // TODO import/allow function, make it so that e.g. StringUtils.isBlank can be called as if it were statically imported
  // e.g. isBlank("")

  // TODO allow/restrict var def
  // TODO allow/restrict function calls (e.g. only allow operators)

  // on every method call return a new RestrictedScriptBuilder, keep it immutable.
  // this way can reuse the already parsed script and e.g. imported function
  // and only have to e.g. setup variables again for a subsequent run

//  public String getScript() {
//    return script;
//  }

  public ScriptBuilder setScript(String script) {
    return setScript(script, false);
  }

  public ScriptBuilder setScript(String script, boolean showErrorsOnConsole) {
    return new ScriptBuilder(
        new Parser(showErrorsOnConsole).parse(script),
        importList,
        importStaticList,
        functionAlias);
  }

  public ScriptBuilder addImport(String imp) {
    return new ScriptBuilder(
        symbols,
        importList.insert(imp),
        importStaticList,
        functionAlias);
  }

  public ScriptBuilder addStaticImport(String imp) {
    return new ScriptBuilder(
        symbols,
        importList,
        importStaticList.insert(imp),
        functionAlias);
  }

  public ScriptBuilder addFunctionAlias(String alias, String target) {
    return new ScriptBuilder(
        symbols,
        importList,
        importStaticList,
        functionAlias.assign(alias, target));
  }

  public Script create() {
    if(symbols == null) {
      throw new ScriptException("no script");
    }
    return new Script(new SymbolTable(
        symbols,
        this.importList,
        this.importStaticList,
        this.functionAlias));
  }

}
