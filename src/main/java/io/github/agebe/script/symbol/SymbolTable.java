package io.github.agebe.script.symbol;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.util.JImmutables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.agebe.script.LangType;
import io.github.agebe.script.ScriptException;
import io.github.agebe.script.Value;
import io.github.agebe.script.parser.AstNode;
import io.github.agebe.script.parser.Result;

public class SymbolTable {

  private static final Logger log = LoggerFactory.getLogger(SymbolTable.class);

  private JImmutableList<String> importList;

  private JImmutableList<String> importStaticList;

  private JImmutableMap<String, String> functionAlias;

  private Map<String, VarSymbol> variables = new HashMap<>();

  private AstNode entryPoint;

  public SymbolTable(AstNode entryPoint) {
    super();
    this.entryPoint = entryPoint;
    importList = JImmutables.list();
    importStaticList = JImmutables.list();
    functionAlias = JImmutables.map();
  }

  public SymbolTable(
      SymbolTable symbols,
      JImmutableList<String> importList,
      JImmutableList<String> importStaticList,
      JImmutableMap<String, String> functionAlias) {
    super();
    this.entryPoint = symbols.getEntryPoint();
    // use the ones from the builder API first so they take precedence
    this.importList = importList.insertAll(symbols.importList);
    this.importStaticList = importStaticList.insertAll(symbols.importStaticList);
    // function aliases from the builder API take precedence
    this.functionAlias = symbols.functionAlias.assignAll(functionAlias);
    this.variables = new HashMap<>(symbols.variables);
  }

  public AstNode getEntryPoint() {
    return entryPoint;
  }

  public void setEntryPoint(AstNode entryPoint) {
    this.entryPoint = entryPoint;
  }

  public Symbol resolve(String name) {
    VarSymbol variable = variables.get(name);
    if(variable != null) {
      return variable;
    }
    ClassSymbol cls = cls(name);
    if(cls != null) {
      return cls;
    }
    VarSymbol sf = staticField(name);
    if(sf != null) {
      return sf;
    }
    throw new SymbolNotFoundException(name);
  }

  public JavaMethodSymbol resolveFunction(String name) {
    String target = functionAlias.get(name);
    if(target != null) {
      log.debug("found function alias for '{}', '{}'", name, target);
      String className = StringUtils.substringBeforeLast(target, ".");
      String methodName = StringUtils.substringAfterLast(target, ".");
      Class<?> cls = findClass(className);
      if(cls != null) {
        log.debug("resolved function alias '{}' to class '{}', method '{}'", name, cls.getName(), methodName);
        return new JavaMethodSymbol(cls, methodName, null);
      } else {
        VarSymbol vs = staticField(className);
        if(vs != null) {
          log.debug("resolved function alias '{}' to class '{}', method '{}'",
              name,
              vs.getVal().getCls(),
              methodName);
          return new JavaMethodSymbol(vs.getVal().getCls(), methodName, vs.getVal().getObj());
        }
      }
    }
    // TODO check script functions next
    return importStaticList.stream()
        .map(imp -> resolveStaticImport(imp, name))
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
  }

  private JavaMethodSymbol resolveStaticImport(String staticImport, String functionName) {
    String[] parts = StringUtils.split(staticImport, '.');
    String last = parts[parts.length-1];
    if(functionName.equals(last)) {
      String className = StringUtils.substringBeforeLast(staticImport, ".");
      Class<?> cls = findClass(className);
      return cls!=null?new JavaMethodSymbol(cls, functionName, null):null;
    } else if("*".equals(last)) {
      String className = StringUtils.substringBeforeLast(staticImport, ".");
      Class<?> cls = findClass(className);
      if((cls != null) && hasMethod(cls, functionName)) {
        return new JavaMethodSymbol(cls, functionName, null);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  private boolean hasMethod(Class<?> cls, String methodName) {
    return Arrays.stream(cls.getMethods())
        .anyMatch(m -> StringUtils.equals(m.getName(), methodName));
  }

  public Result resolveAsResult(String name) {
    Symbol s = resolve(name);
    return s!=null?s.toResult():null;
  }

  private ClassSymbol cls(String name) {
    Class<?> cls = findClass(name);
    return cls!=null?new ClassSymbol(cls):null;
  }

  private VarSymbol staticField(String name) {
    // e.g. like java.lang.System.out
    Class<?> cls = null;
    String[] parts = StringUtils.split(name, '.');
    String[] fields = null;
    if(parts.length == 0) {
      return null;
    }
    for(int i=1;i<parts.length;i++) {
      cls = findClass(StringUtils.join(parts, '.', 0, i));
      if(cls != null) {
        fields = new String[parts.length-i];
        System.arraycopy(parts, i, fields, 0, parts.length-i);
        // TODO get the rest of the array as fields to follow
        break;
      }
    }
    if(cls == null) {
      return null;
    }
    log.debug("found class '{}', fields to follow '{}'", cls.getName(), Arrays.toString(fields));
    if(fields.length == 0) {
      return null;
    }
    Field f = null;
    Class<?> wcls = cls;
    for(String s : fields) {
      f = getField(wcls, s);
      if(f == null) {
        log.debug("field '{}' not found on class '{}'", s, wcls.getName());
        return null;
      }
      log.debug("field '{}' found on class '{}', type '{}'", s, wcls.getName(), f.getType());
      wcls = f.getType();
    }
    if(f != null) {
      try {
        // TODO add support for primitive typed fields
        return new VarSymbol(parts[parts.length-1], LangType.OBJ, Value.ofObj(f.get(null), f.getType()));
      } catch(Exception e) {
        new ScriptException("failed to access field '%s'".formatted(name), e);
      }
    }
    return null;
  }

  private Field getField(Class<?> cls, String name) {
    try {
      return cls.getField(name);
    } catch (NoSuchFieldException e) {
      return null;
    } catch (SecurityException e) {
      throw new ScriptException("failed to get field", e);
    }
  }

  private Class<?> findClass(String name) {
    Class<?> cls = findClassExact(name);
    if(cls != null) {
      log.debug("found class '{}'", cls.getName());
      return cls;
    }
    cls = importList.stream()
        .map(imp -> fromImport(name, imp))
        .filter(Objects::nonNull)
        .map(this::findClassExact)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
    if(cls != null) {
      log.debug("found class '{}' for name '{}' (resolved from imports)", cls.getName(), name);
    } else {
      log.debug("class not found '{}'", name);
    }
    return cls;
  }

  private String fromImport(String clsName, String importName) {
    String[] parts = StringUtils.split(importName, '.');
    String last = parts[parts.length-1];
    if("*".equals(last)) {
      return StringUtils.join(parts, '.', 0, parts.length-1) + "." + clsName;
    } else if(last.equals(clsName)) {
      return importName;
    } else {
      return null;
    }
  }

  private Class<?> findClassExact(String name) {
    try {
      return Class.forName(name);
    } catch(ClassNotFoundException e) {
      return null;
    }
  }

}
