package org.rescript.symbol;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;
import org.rescript.value.ObjValue;
import org.rescript.value.SymbolValue;
import org.rescript.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO this class requires refactor/rewrite
public class JavaSymbols {

  private static final Logger log = LoggerFactory.getLogger(JavaSymbols.class);

  private List<String> imports;

  private List<String> staticImports;

  private Map<String, String> functionAlias;

  public JavaSymbols() {
    super();
    this.imports = new ArrayList<>();
    this.staticImports = new ArrayList<>();
    this.functionAlias = new HashMap<>();
    imports.add("java.lang.*");
    functionAlias.put("println", "System.out.println");
  }

  public void addImport(String imp) {
    imports.add(imp);
  }

  public void addStaticImport(String imp) {
    staticImports.add(imp);
  }

  public void addFunctionAlias(String alias, String function) {
    functionAlias.put(alias, function);
  }

  // ----------------------------------------------------------------------
  // resolve from here ----------------------------------------------------
  // ----------------------------------------------------------------------

  public Value resolveTypeOrStaticMember(String ident) {
    // the ident string is something like Boolean.TRUE
    // or variable.counter or java.lang.System or just System (resolve from imports)
    // so it seem we have to resolve the first part of the identifier
    // which could be
    // 1. variable
    // 2. class name (also imported)
    // 3. static member (static import)
    // after the first part is resolved following the dotted identifiers should be the same for all 3 cases
    // what follows could be
    // - fields
    // - static fields
    // - inner classes
    // in case of a variable we have a type and an object otherwise just a type
    // in any order

    // TODO not sure in which order the resolve needs to be done...
    log.debug("resolve '{}'", ident);
    List<String> split = RUtils.splitTypeName(ident);
    String s0 = split.get(0);
    // check FQCN
    Class<?> fqcn = RUtils.forName(ident);
    if(fqcn != null) {
      // in this case there is nothing remaining
      return new ObjValue(fqcn, null);
    }
    for(int i=1;i<split.size();i++) {
      String s = split.stream().limit(i).collect(Collectors.joining("."));
      Class<?> cls = RUtils.forName(s);
      if(cls != null) {
        return resolveRemaining(split.stream().skip(i).toList(), new ObjValue(cls, null));
      }
    }
    // check type from imports
    for(String imp : imports) {
      LinkedList<String> impSplit = RUtils.splitTypeName(imp);
      String impLast = impSplit.getLast();
      if("*".equals(impLast)) {
        impSplit.removeLast();
        String probe = impSplit.stream().collect(Collectors.joining(".")) + "." + s0;
        Class<?> cls = RUtils.forName(probe);
        if(cls != null) {
          return resolveRemaining(split.stream().skip(1).toList(), new ObjValue(cls, null));
        }
      } else if(StringUtils.equals(s0, impLast)) {
        Class<?> cls = RUtils.forName(imp);
        if(cls != null) {
          return resolveRemaining(split.stream().skip(1).toList(), new ObjValue(cls, null));
        } else {
          throw new ScriptException("class import '%s' not found".formatted(imp));
        }
      }
    }
    // check static imports
    // TODO DRY
    for(String imp : staticImports) {
      log.debug("static import '{}'", imp);
      LinkedList<String> impSplit = RUtils.splitTypeName(imp);
      String impLast = impSplit.getLast();
      if("*".equals(impLast)) {
        impSplit.removeLast();
        String probe = impSplit.stream().collect(Collectors.joining("."));
        Class<?> cls = RUtils.forName(probe);
        if(cls != null) {
          // it could be a static field or a static inner type, check both
          Field f = RUtils.findStaticField(cls, s0);
          if(f != null) {
            return resolveRemaining(split.stream().skip(1).toList(), new SymbolValue(new FieldSymbol(f, null)));
          }
          Class<?> innerCls = RUtils.innerClass(cls, s0);
          if(innerCls != null) {
            return resolveRemaining(split.stream().skip(1).toList(), new ObjValue(innerCls, null));
          }
        } else {
          throw new ScriptException("static import '%s', class '%s' not found".formatted(imp, probe));
        }
      } else if(StringUtils.equals(s0, impLast)) {
        log.debug("XXX");
        impSplit.removeLast();
        String probe = impSplit.stream().collect(Collectors.joining("."));
        Class<?> cls = RUtils.forName(probe);
        log.debug("probe '{}', '{}'", probe, cls);
        if(cls != null) {
          Field f = RUtils.findStaticField(cls, s0);
          log.debug("static field for '{}', '{}'", s0, f);
          if(f != null) {
            return resolveRemaining(split.stream().skip(1).toList(), new SymbolValue(new FieldSymbol(f, null)));
          }
          Class<?> innerCls = RUtils.innerClass(cls, s0);
          if(innerCls != null) {
            return resolveRemaining(split.stream().skip(1).toList(), new ObjValue(innerCls, null));
          }
          throw new ScriptException("static import '%s' does not have static field/type '%s'".formatted(cls.getName(), s0));
        } else {
          throw new ScriptException("static import class '%s' not found".formatted(probe));
        }
      }
    }
    return null;
  }

  public Value resolveRemaining(List<String> ident, Value val) {
    if(ident.isEmpty()) {
      return val;
    }
    Value current = val;
    for(String s : ident) {
      log.debug("resolve remaining '{}' on type '{}'", s, current.type());
      Field staticField = RUtils.findStaticField(current.type(), s);
      if(staticField != null) {
        log.debug("found static field '{}' on type '{}'", s, current.type());
        current = new SymbolValue(new FieldSymbol(staticField, null));
        continue;
      }
      if(current.val() != null) {
        Field field = RUtils.findField(current.type(), s);
        if(field != null) {
          log.debug("found field '{}' on type '{}'", s, current.type());
          current = new SymbolValue(new FieldSymbol(field, current.val()));
          continue;
        }
      }
      Class<?> inner = RUtils.innerClass(current.type(), s);
      if(inner != null) {
        current = new ObjValue(inner, null);
        continue;
      }
      log.debug("could not resolve '{}' on type '{}'", s, current.type());
      return null;
    }
    log.debug("resolved to '{}'", current);
    return current;
  }

  public Class<?> resolveType(String identifier) {
    return resolveTypeInternal(identifier);
  }

  public JavaMethodSymbol resolveFunction(String name) {
    return resolveFunctionInternal(name);
  }

  // ----------------------------------------------------------------------
  // resolve function here ------------------------------------------------
  // ----------------------------------------------------------------------
  public JavaMethodSymbol resolveFunctionInternal(String name) {
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
    return staticImports.stream()
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
    if(tr == null) {
      return null;
    }
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
    cls = imports.stream()
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

// ------------------------------------------------------
// resolve type -----------------------------------------
// ------------------------------------------------------
  private Class<?> resolveTypeInternal(String name) {
    log.debug("enter resolveType '{}'", name);
    log.debug("probing '{}'...", name);
    Class<?> cls = RUtils.findClass(name);
    if(cls != null) {
      log.debug("found '{}' for name '{}'", cls.getName(), name);
      return cls;
    }
    for(String im : imports) {
      LinkedList<String> split = RUtils.splitTypeName(im);
      if(StringUtils.equals(name, split.getLast())) {
        Class<?> c = RUtils.findClass(im);
        log.debug("found '{}' for name '{}'", c.getName(), im);
        return c;
      } else if(StringUtils.equals("*", split.getLast())) {
        split.removeLast();
        String joined = split.stream().collect(Collectors.joining("."));
        log.debug("probing '{}.{}", joined, name);
        Class<?> c = RUtils.findClass(joined, name);
        if(c != null) {
          log.debug("found '{}'", c.getName());
          return c;
        }
      }
    }
    // TODO also check static imports?
    return null;
  }


}
