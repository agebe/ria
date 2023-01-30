/*
 * Copyright 2023 Andre Gebers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ria;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class JavaTypeDefTest {

  @Test
  public void simple() {
    new Script().run("""
        javasrc '''
        public class Foo {
        }
        ''';
        """);
  }

  @Test
  public void simple2() {
    String s = (String)new Script().run("""
        javasrc '''
        package bar;
        public class Foo {
          public String hello() {
            return "hello from java";
          }
        }
        ''';
        new bar.Foo().hello();
        """);
    assertEquals("hello from java", s);
  }

  @Test
  public void gsonTypeAdapterGenerics() {
    new Script().run("""
// just showing how to exclude some packages from the automatic package import
// but not necessary in this case
options {
  importDependenciesFilter.add('com.google.gson.internal.*');
}

dependencies {
  'com.google.code.gson:gson:2.10'
}

javasrc $imports + '''
public class ListOfInstantTypeToken extends TypeToken<List<Instant>> {
}''';

javasrc $imports + '''
public class InstantTypeAdapter extends TypeAdapter<Instant> {

  @Override
  public void write(JsonWriter out, Instant value) throws IOException {
    if(value != null) {
      out.value(value.toString());
    } else {
      out.nullValue();
    }
  }

  @Override
  public Instant read(JsonReader in) throws IOException {
    return Instant.parse(in.nextString());
  }
}''';

var json = '''
[
  '2023-01-01T18:21:31.801080912Z',
  '2023-01-01T18:22:00Z'
]
''';

var builder = new GsonBuilder();
builder.registerTypeAdapter(Instant.class, new InstantTypeAdapter());
var gson = builder.create();
var l = gson.fromJson(json, new ListOfInstantTypeToken().getType());
println(typeof l.get(0));
println(l.get(0));
        """);
  }

  @Test
  public void extendsTest() {
    new Script().run("""
        javasrc '''
        public class A {
        }''';
        
        javasrc '''
        public class B extends A {
        }''';
        
        println(new B());
        """);
  }

  @Test
  public void extendsTest2() {
    new Script().run("""
        javasrc '''
        public abstract class A<T> {
          abstract T create();
        }''';
        javasrc '''
        public class B extends A<String> {
          @Override
          String create() {
            return "created by B";
          }
          @Override
          public String toString() {
            return create();
          }
        }''';
        println(new B());
        """);
  }

  @Test
  public void implementsTest4() {
    new Script().run("""
        javasrc $imports + '''
        public class B implements Consumer<List<String>> {
          @Override
          public void accept(List<String> o) {
            System.out.println(o);
          }
        }''';
        new B().accept(List.of(1,2,3));
        """);
  }

  @Test
  public void implementsTest() {
    new Script().run("""
        javasrc $imports + '''
        public class A implements Consumer<String> {
          public void accept(String s) {
            System.out.println(s);
          }
        }''';
        new A().accept("my message");
        """);
  }

  @Test
  public void implementsTest2() {
    new Script().run("""
        javasrc $imports + '''
        public class A implements Consumer<String>, Supplier<Integer> {
          @Override
          public void accept(String s) {
            System.out.println(s);
          }
          @Override
          public Integer get() {
            return 42;
          }
        }''';
        new A().accept("my message");
        """);
  }

  @Test
  public void interface1() {
    assertEquals("foobar", new Script().run("""
        javasrc '''
        public interface I1 {
          String foo();
        }''';
        javasrc '''
        public interface I2 {
          String bar();
        }''';
        javasrc '''
        public class B implements I1, I2 {
          @Override
          public String foo() {
            return "foo";
          }
          @Override
          public String bar() {
            return "bar";
          }
        }''';
        var b = new B();
        println(b.foo() + b.bar());
        return b.foo() + b.bar();
        """));
  }

  @Test
  public void interface2() {
    assertEquals("foobar", new Script().run("""
        javasrc '''
        public interface I1 {
          String foo();
        }''';
        javasrc '''
        public interface I2 extends I1 {
          String bar();
        }''';
        javasrc '''
        public class B implements I2 {
          @Override
          public String foo() {
            return "foo";
          }
          @Override
          public String bar() {
            return "bar";
          }
        }''';
        var b = new B();
        println(b.foo() + b.bar());
        return b.foo() + b.bar();
        """));
  }

  @Test
  public void interface3() {
    new Script().run("""
        javasrc '''
        @SuppressWarnings(value = {"unchecked", "foo"})
        @FunctionalInterface
        public interface I1 {
          String create();
        }''';
        """);
  }

  @Test
  public void extendsAndImplementsTest() {
    new Script().run("""
        javasrc '''
        @SuppressWarnings(value = {"unchecked", "foo"})
        public abstract class A<T> {
          public abstract T create();
        }''';
        javasrc $imports + '''
        public class B extends A<Map<List<String>, String>> implements Consumer<String> {
          @Override
          public Map<List<String>, String> create() {
            return null;
          }
          @Override
          public void accept(String s) {
          }
        }''';
        println(new B());
        """);
  }

  @Test
  public void enumTest() {
    new Script().run("""
        javasrc '''
        @SuppressWarnings(value = {"unchecked", "foo"})
        public enum E1 {
          A,
          B,
          C,
          ;
        }''';
        javasrc '''
        public class B {
          private E1 e = E1.C;
        }''';
        println(E1.A);
        """);
  }

  @Test
  public void recordTest() {
    new Script().run("""
        javasrc '''
        package records;
        @SuppressWarnings(value = {"unchecked", "foo"})
        public record RecordTest(String s1, int i2) {
        }''';
        println(new records.RecordTest("record-test", 42));
        """);
  }

  @Test
  public void annotationTest() {
    new Script().run("""
        javasrc '''
        import static java.lang.annotation.RetentionPolicy.RUNTIME;
        import java.lang.annotation.Retention;
        @Retention(RUNTIME)
        public @interface TestAnnotation {
        }''';
        """);
  }

  @Test
  public void annotationTest2() {
    new Script().run("""
        javasrc '''
        package foo.bar.myannotation;
        import static java.lang.annotation.RetentionPolicy.RUNTIME;
        import java.lang.annotation.Documented;
        import java.lang.annotation.Retention;
        @Documented
        @Retention(RUNTIME)
        public @interface NotEmpty {
          String message() default "{org.hibernate.validator.constraints.NotEmpty.message}";
          Class<?>[] groups() default { };
          @Retention(RUNTIME)
          @Documented
          public @interface List {
            NotEmpty[] value();
          }
        }''';
        javasrc '''
        import foo.bar.myannotation.NotEmpty;
        @NotEmpty.List({
            @NotEmpty( message = "Person name should not be empty",
                       groups=String.class),
            @NotEmpty( message = "Company name should not be empty",
                       groups=String.class),
        })
        public class A {
        }''';
        """);
  }

  @Test
  @Disabled
  public void annotationTest3() {
    new Script().run("""
        javasrc '''
        import static java.lang.annotation.RetentionPolicy.RUNTIME;
        import java.lang.annotation.Retention;
        @Retention(RUNTIME)
        public @interface TestAnnotation {
          String value();
        }''';
        // FIXME org.ria.java.JavaTypeSource.getName only splits by whitespace and uses the first name after
        // class, interface, enum or record. if any of those keywords appear in e.g. strings of annotation then the
        // name detection fails.
        javasrc '''
        @TestAnnotation("implements interface Foo and ...")
        public class A {
        }''';
        """);
  }

}
