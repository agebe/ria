package io.github.agebe.script;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import io.github.agebe.script.lang.Result;

public class SymbolTable {

  // TODO add support for imports and static imports
  // TODO add support for function alias foo -> System.out.println
  private List<String> importList = new ArrayList<>();

  private Map<String, VarSymbol> variables = new HashMap<>();

  public SymbolTable() {
    super();
    importList.add("java.lang.*");
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

  public Result resolveAsResult(String name) {
    Symbol s = resolve(name);
    return s!=null?s.toResult():null;
  }

  private ClassSymbol cls(String name) {
    try {
      Class<?> cls = Class.forName(name);
      return cls!=null?new ClassSymbol(cls):null;
    } catch(ClassNotFoundException e) {
      return null;
    }
  }

  private VarSymbol staticField(String name) {
    // e.g. like java.lang.System.out
    final Class<?> cls = findClass(StringUtils.split(name, '.'));
    if(cls == null) {
      return null;
    }
    String[] parts = StringUtils.split(StringUtils.removeStartIgnoreCase(name, cls.getName()), '.');
    System.out.println(Arrays.toString(parts));
    Field f = null;
    Class<?> wcls = cls;
    for(String s : parts) {
      f = getField(wcls, s);
      if(f == null) {
        return null;
      }
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

  private Class<?> findClass(String[] parts) {
    for(int i=0;i<parts.length;i++) {
      Class<?> cls = findClass(StringUtils.join(parts, '.', 0, i));
      if(cls != null) {
        return cls;
      }
    }
    return null;
  }

  private Class<?> findClass(String name) {
    try {
      return Class.forName(name);
    } catch(ClassNotFoundException e) {
      return null;
    }
  }

}
