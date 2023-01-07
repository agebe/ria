package org.rescript.symbol.script;

import java.util.List;

import org.rescript.ScriptException;
import org.rescript.parser.Type;
import org.rescript.run.ScriptContext;
import org.rescript.statement.Function;
import org.rescript.symbol.VarSymbol;
import org.rescript.value.MethodValue;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptSymbols {

  private static final Logger log = LoggerFactory.getLogger(ScriptSymbols.class);

  private Function main;

  private ScriptScopeNode root;

  private ThreadLocal<ScopeNode> current = ThreadLocal.withInitial(() -> root);

  private ScriptContext ctx;

  public ScriptSymbols() {
    super();
    root = new ScriptScopeNode();
    defineVar("println", new MethodValue(System.out.getClass(), System.out, "println"));
  }

  public void defineVar(String name, Value val) {
    defineVar(name, val, null);
  }

  public ScopeNode getCurrentScope() {
    return current.get();
  }

  public void setCurrentScope(ScopeNode scope) {
    current.set(scope);
  }

  public void defineVar(String name, Value val, Type type) {
    current.get().defineVar(name, val, type, ctx);
  }

  public void assignVar(String name, Value val) {
    current.get().assignVar(name, val);
  }

  public void defineOrAssignVarRoot(String name, Value val) {
    VarSymbol s = root.getVarSymbol(name);
    if(s != null) {
      s.setVal(val);
    } else {
      root.defineVar(name, val, null, ctx);
    }
  }

  public VarSymbol unsetRoot(String name) {
    return root.unset(name);
  }

  public Value getVariable(String name) {
    VarSymbol sym = resolveVar(name);
    return sym!=null?sym.getVal():null;
  }

  public VarSymbol resolveVar(String ident) {
    return current.get().getVarSymbol(ident);
  }

  /**
   * returns true if a variable or field is defined
   * @param ident
   * @return true if a variable or field is defined
   */
  public boolean isDefined(String ident) {
    return resolveVar(ident) != null;
  }

  public void enterScope() {
    ScopeNode parent = current.get();
    current.set(new ScriptScopeNode(parent));
  }

  public void enterObjectScope(Object o) {
    ScopeNode parent = current.get();
    current.set(new ObjectScopeNode(o, parent, ctx));
  }

  public void exitScope() {
    if(current.get().getParent() == null) {
      throw new ScriptException("can't exit root scope");
    } else {
      current.set(current.get().getParent());
    }
  }

  public Function getMain() {
    return main;
  }

  public void setMain(Function main) {
    this.main = main;
  }

  public List<Function> findFunctions(String functionName) {
    log.debug("find function '%s'".formatted(functionName));
    Function current = ctx.currentFunction();
    log.debug("check current");
    if(current.matchesName(functionName)) {
      // recursive call
      log.debug("is current");
      return List.of(current);
    }
    log.debug("check nested");
    List<Function> nested = current.getNestedFunctions()
        .stream()
        .filter(f -> f.matchesName(functionName))
        .toList();
    if(!nested.isEmpty()) {
      log.debug("is nested");
      return nested;
    }
    log.debug("check parent");
    Function parent = current.getParent();
    if(parent != null) {
      if(parent.matchesName(functionName)) {
        log.debug("is parent");
        return List.of(parent);
      } else {
        log.debug("check sibling");
        List<Function> sibling = parent.getNestedFunctions()
            .stream()
            .filter(f -> f.matchesName(functionName))
            .toList();
        if(!sibling.isEmpty()) {
          return sibling;
        }
      }
    }
    // TODO should we go up the tree all the way to the root to find the method?
    log.debug("script function not found");
    return List.of();
  }

  public ScriptContext getCtx() {
    return ctx;
  }

  public void setCtx(ScriptContext ctx) {
    this.ctx = ctx;
  }

}
