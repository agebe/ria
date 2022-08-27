package io.github.agebe.script;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;

import io.github.agebe.script.antlr.ScriptLexer;
import io.github.agebe.script.antlr.ScriptParser;

public class RestrictedScriptBuilder {

//  private String script;

  private ScriptParser.ScriptContext tree;

  public RestrictedScriptBuilder(String script) {
    // TODO add flag for console error listeners
//    this.script = script;
    ScriptLexer lexer = new ScriptLexer(CharStreams.fromString(script));
    // antlr uses the ConsoleErrorListener by default
    // showing here how to remove it for later
    lexer.removeErrorListeners();
    // add it back in for now ...
    // TODO throw exception on lexer error
    lexer.addErrorListener(ConsoleErrorListener.INSTANCE);
//    lexer.getAllTokens().forEach(System.out::println);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ScriptParser parser = new ScriptParser(tokens);
    // same is with the parser error listener ...
    //parser.getErrorListeners().forEach(System.out::println);
    // TODO throw exception on parser error
    ErrorListener errorListener = new ErrorListener();
    parser.addErrorListener(errorListener);
    tree = parser.script();
  }

  // TODO set variables
  // TODO import/allow function, make it so that e.g. StringUtils.isBlank can be called as if it were statically imported
  // e.g. isBlank("")

  // TODO allow/restrict var def
  // TODO allow/restrict function calls (e.g. only allow operators)

  // on every method call return a new RestrictedScriptBuilder, keep it immutable.
  // this way can reuse the already parsed script and e.g. imported function
  // and only have to e.g. setup variables again for a subsequent run

  public RestrictedScript create() {
    return new RestrictedScript(tree);
  }

}
