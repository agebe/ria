package io.github.agebe.script;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.util.JImmutables;

import io.github.agebe.script.parser.Parser;
import io.github.agebe.script.symbol.SymbolTable;

public class RestrictedScriptBuilder {

  private SymbolTable symbols;

  private JImmutableList<String> importList = JImmutables.list("java.lang.*");

  private JImmutableList<String> importStaticList = JImmutables.list();

  private JImmutableMap<String, String> functionAlias = JImmutables.map();

  public RestrictedScriptBuilder() {
    super();
  }

  private RestrictedScriptBuilder(
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

  public RestrictedScriptBuilder setScript(String script) {
    return setScript(script, false);
  }

  public RestrictedScriptBuilder setScript(String script, boolean showErrorsOnConsole) {
    return new RestrictedScriptBuilder(
        new Parser(showErrorsOnConsole).parse(script),
        importList,
        importStaticList,
        functionAlias);
  }

  public RestrictedScriptBuilder addImport(String imp) {
    return new RestrictedScriptBuilder(
        symbols,
        importList.insert(imp),
        importStaticList,
        functionAlias);
  }

  public RestrictedScriptBuilder addStaticImport(String imp) {
    return new RestrictedScriptBuilder(
        symbols,
        importList,
        importStaticList.insert(imp),
        functionAlias);
  }

  public RestrictedScriptBuilder addFunctionAlias(String alias, String target) {
    return new RestrictedScriptBuilder(
        symbols,
        importList,
        importStaticList,
        functionAlias.assign(alias, target));
  }

  public RestrictedScript create() {
    if(symbols == null) {
      throw new ScriptException("no script");
    }
    return new RestrictedScript(new SymbolTable(
        symbols,
        this.importList,
        this.importStaticList,
        this.functionAlias));
  }

}
