package org.rescript.symbol;

import static org.javimmutable.collections.util.JImmutables.list;
import static org.javimmutable.collections.util.JImmutables.map;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.rescript.ScriptException;
import org.rescript.parser.AstNode;
import org.rescript.run.Value;
import org.rescript.run.VoidValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO this class requires refactor/rewrite
public class SymbolTable {

  private static final Logger log = LoggerFactory.getLogger(SymbolTable.class);

  private JImmutableList<String> importList;

  private JImmutableList<String> importStaticList;

  private JImmutableMap<String, String> functionAlias;

  private Map<String, VarSymbol> variables = new HashMap<>();

  private AstNode entryPoint;

  public SymbolTable() {
    this(null);
  }

  public SymbolTable(AstNode entryPoint) {
    this(list(), list(), map(), entryPoint);
  }

  public SymbolTable(
      SymbolTable symbols,
      JImmutableList<String> importList,
      JImmutableList<String> importStaticList,
      JImmutableMap<String, String> functionAlias) {
    this(
        importList.insertAll(symbols.importList),
        importStaticList.insertAll(symbols.importStaticList),
        symbols.functionAlias.assignAll(functionAlias),
        symbols.getEntryPoint()
        );
  }

  public SymbolTable(JImmutableList<String> importList, JImmutableList<String> importStaticList,
      JImmutableMap<String, String> functionAlias, AstNode entryPoint) {
    super();
    this.importList = importList;
    this.importStaticList = importStaticList;
    this.functionAlias = functionAlias;
    this.entryPoint = entryPoint;
  }

  private JImmutableList<String> imports() {
    return importList.insertFirst("java.lang.*");
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
    ClassSymbol innerCls = innerClass(name);
    if(innerCls != null) {
      return innerCls;
    }
    VarSymbol sf = staticMember(name);
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
      Class<?> cls = findClassOrInnerClass(className);
      if(cls != null) {
        log.debug("resolved function alias '{}' to class '{}', method '{}'", name, cls.getName(), methodName);
        return new JavaMethodSymbol(cls, methodName, null);
      } else {
        VarSymbol vs = staticMember(className);
        if(vs != null) {
          log.debug("resolved function alias '{}' to class '{}', method '{}'",
              name,
              vs.getVal().type(),
              methodName);
          return new JavaMethodSymbol(vs.getVal().type(), methodName, vs.getVal().val());
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

  private Class<?> findClassOrInnerClass(String name) {
    Class<?> cls = findClass(name);
    return cls!=null?cls:findInnerClass(name);
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

  private ClassSymbol cls(String name) {
    Class<?> cls = findClass(name);
    return cls!=null?new ClassSymbol(cls):null;
  }

  // TODO make a pair type in util package?
  private static class TR {
    private Class<?> cls;
    private String[] path;
    public TR(Class<?> cls, String[] path) {
      super();
      this.cls = cls;
      this.path = path;
    }
    @Override
    public String toString() {
      return "TR [cls=" + cls + ", path=" + Arrays.toString(path) + "]";
    }
  }

  private TR findTypeReturnRemaining(String name) {
    Class<?> cls = findClass(name);
    if(cls != null) {
      return new TR(cls, new String[0]);
    }
    String[] parts = StringUtils.split(name, '.');
    for(int i=1;i<parts.length;i++) {
      String probeClass = StringUtils.join(parts, '.', 0, i);
      cls = findClass(probeClass);
      log.debug("probe class '{}', result: '{}'", probeClass, cls);
      if(cls != null) {
        String[] path = new String[parts.length-i];
        System.arraycopy(parts, i, path, 0, parts.length-i);
        return new TR(cls, path);
      }
    }
    return null;
  }

  private VarSymbol staticMember(String name) {
    // e.g. like java.lang.System.out
    TR tr = findTypeReturnRemaining(name);
    log.debug("found class '{}', path to follow '{}'", tr.cls.getName(), Arrays.toString(tr.path));
    if(tr.path.length == 0) {
      return null;
    }
    Class<?> current = tr.cls;
    Field f = null;
    for(String p : tr.path) {
      Class<?> inner = getInnerClass(current, p);
      if(inner != null) {
        log.debug("found inner class '{}', cls '{}'", p, inner);
        f = null;
        current = inner;
      } else {
        f = getField(current, p);
        if(f == null) {
          log.debug("field '{}' not found on class '{}'", p, current.getName());
          return null;
        }
        current = f.getType();
      }
    }
    if(f != null) {
      try {
        return new VarSymbol(tr.path[tr.path.length-1], Value.of(f.getType(), f.get(null)));
      } catch(Exception e) {
        new ScriptException("failed to access field '%s'".formatted(name), e);
      }
    }
    return null;
  }

  private Class<?> getInnerClass(Class<?> cls, String name) {
    Class<?>[] classes = cls.getDeclaredClasses();
    return Arrays.stream(classes).filter(c -> c.getSimpleName().equals(name)).findFirst().orElse(null);
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
    cls = imports().stream()
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
      return null;
    }
    return cls;
  }

  private ClassSymbol innerClass(String name) {
    log.debug("find inner class '{}'", name);
    Class<?> cls = findInnerClass(name);
    if(cls != null) {
      log.debug("found inner class '{}', cls '{}'", name, cls);
      return new ClassSymbol(cls);
    } else {
      log.debug("inner class not found '{}'", name);
      return null;
    }
  }

  private Class<?> findInnerClass(String name) {
    // try to find inner class
    TR tr = findTypeReturnRemaining(name);
    if(tr != null) {
      log.debug("find inner, name '{}', '{}'", name, tr);
      Class<?> current = tr.cls;
      for(String p : tr.path) {
        
        Class<?> inner = getInnerClass(current, p);
        if(inner == null) {
          log.debug("find inner failed on '{}', current '{}'", p, current);
          return null;
        }
        current = inner;
      }
      if(current != null) {
        log.debug("found class '{}' for name '{}' (resolved from imports)", current, name);
        return current;
      }
    }
    log.debug("class not found '{}'", name);
    return null;
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

  public SymbolTable merge(SymbolTable symbols) {
    return new SymbolTable(
        importList.insertAll(symbols.importList),
        importStaticList.insertAll(symbols.importStaticList),
        functionAlias.insertAll(symbols.functionAlias),
        symbols.entryPoint
        );
  }

  public void defineVar(String name, Value val) {
    VarSymbol v = variables.putIfAbsent(name, new VarSymbol(name, val!=null?val:new VoidValue()));
    if(v != null) {
      throw new ScriptException("variable '%s' already defined".formatted(name));
    }
  }

}
