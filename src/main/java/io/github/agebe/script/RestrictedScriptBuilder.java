package io.github.agebe.script;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.apache.commons.lang3.StringUtils;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.util.JImmutables;

import io.github.agebe.script.antlr.ScriptLexer;
import io.github.agebe.script.antlr.ScriptParser;

public class RestrictedScriptBuilder {

  private String script;

  private boolean showErrorsOnConsole;

  private ScriptParser.ScriptContext scriptCtx;

  private JImmutableList<String> importList = JImmutables.list("java.lang.*");

  private JImmutableList<String> importStaticList = JImmutables.list();

  private JImmutableMap<String, String> functionAlias = JImmutables.map();

  public RestrictedScriptBuilder() {
    super();
  }

  private RestrictedScriptBuilder(
      String script,
      boolean showErrorsOnConsole,
      ScriptParser.ScriptContext scriptCtx,
      JImmutableList<String> importList,
      JImmutableList<String> importStaticList,
      JImmutableMap<String, String> functionAlias) {
    super();
    this.script = script;
    this.showErrorsOnConsole = showErrorsOnConsole;
    this.scriptCtx = scriptCtx;
    this.importList = importList;
    this.importStaticList = importStaticList;
    this.functionAlias = functionAlias;
  }

  public boolean isShowErrorsOnConsole() {
    return showErrorsOnConsole;
  }

  public RestrictedScriptBuilder setShowErrorsOnConsole(boolean showErrorsOnConsole) {
    return new RestrictedScriptBuilder(
        script,
        showErrorsOnConsole,
        scriptCtx,
        importList,
        importStaticList,
        functionAlias);
  }

  // TODO set variables
  // TODO import/allow function, make it so that e.g. StringUtils.isBlank can be called as if it were statically imported
  // e.g. isBlank("")

  // TODO allow/restrict var def
  // TODO allow/restrict function calls (e.g. only allow operators)

  // on every method call return a new RestrictedScriptBuilder, keep it immutable.
  // this way can reuse the already parsed script and e.g. imported function
  // and only have to e.g. setup variables again for a subsequent run

  public String getScript() {
    return script;
  }

  public RestrictedScriptBuilder setScript(String script) {
    return new RestrictedScriptBuilder(
        script,
        showErrorsOnConsole,
        null,
        importList,
        importStaticList,
        functionAlias);
  }

  public RestrictedScriptBuilder addImport(String imp) {
    return new RestrictedScriptBuilder(
        script,
        showErrorsOnConsole,
        null,
        importList.insert(imp),
        importStaticList,
        functionAlias);
  }

  public RestrictedScriptBuilder addStaticImport(String imp) {
    return new RestrictedScriptBuilder(
        script,
        showErrorsOnConsole,
        null,
        importList,
        importStaticList.insert(imp),
        functionAlias);
  }

  public RestrictedScriptBuilder addFunctionAlias(String alias, String target) {
    return new RestrictedScriptBuilder(
        script,
        showErrorsOnConsole,
        null,
        importList,
        importStaticList,
        functionAlias.assign(alias, target));
  }

  public void parse() {
    if(StringUtils.isBlank(script)) {
      throw new ScriptException("no script has been setup");
    }
    ScriptLexer lexer = new ScriptLexer(CharStreams.fromString(script));
    // antlr uses the ConsoleErrorListener by default
    lexer.removeErrorListeners();
    if(showErrorsOnConsole) {
      lexer.addErrorListener(ConsoleErrorListener.INSTANCE);
    }
    lexer.addErrorListener(new SyntaxExceptionErrorListener());
    //  lexer.getAllTokens().forEach(System.out::println);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ScriptParser parser = new ScriptParser(tokens);
    parser.removeErrorListeners();
    if(showErrorsOnConsole) {
      parser.addErrorListener(ConsoleErrorListener.INSTANCE);
    }
    parser.addErrorListener(new SyntaxExceptionErrorListener());
    scriptCtx = parser.script();
  }

  public RestrictedScript create() {
    if(StringUtils.isBlank(script)) {
      throw new ScriptException("no script");
    }
    if(scriptCtx == null) {
      parse();
    }
    return new RestrictedScript(scriptCtx,
        new SymbolTable(
            this.importList.getList(),
            this.importStaticList.getList(),
            this.functionAlias.getMap()));
  }

}
