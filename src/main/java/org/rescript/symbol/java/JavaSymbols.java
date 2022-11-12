package org.rescript.symbol.java;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.rescript.ScriptException;
import org.rescript.value.IntValue;
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

  public JavaSymbols() {
    super();
    this.imports = new ArrayList<>();
    this.staticImports = new ArrayList<>();
    imports.add("java.lang.*");
  }

  public void addImport(String imp) {
    imports.add(imp);
  }

  public void addStaticImport(String imp) {
    staticImports.add(imp);
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
        // also try inner class, but shouldn't this be a static import?
        probe = impSplit.stream().collect(Collectors.joining(".")) + "$" + s0;
        cls = RUtils.forName(probe);
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
      if("length".equals(s) && current.isArray()) {
        current = new IntValue(current.toArray().length());
        continue;
      }
      throw new ScriptException("could not resolve '%s' on type '%s'".formatted(s, current.type()));
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
    return staticImports.stream()
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

  private Class<?> resolvePrimitive(String name) {
    if("double".equals(name)) {
      return double.class;
    } else if("float".equals(name)) {
      return float.class;
    } else if("long".equals(name)) {
      return long.class;
    } else if("int".equals(name)) {
      return int.class;
    } else if("char".equals(name)) {
      return char.class;
    } else if("short".equals(name)) {
      return short.class;
    } else if("byte".equals(name)) {
      return byte.class;
    } else if("boolean".equals(name)) {
      return boolean.class;
    } else if("char".equals(name)) {
      return char.class;
    } else if("void".equals(name)) {
      return void.class;
    } else {
      return null;
    }
  }

  private Class<?> resolveTypeInternal(String name) {
    log.debug("enter resolveType '{}'", name);
    Class<?> primitiveType = resolvePrimitive(name);
    if(primitiveType != null) {
      return primitiveType;
    }
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
    for(String im : staticImports) {
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
    return null;
  }

}
