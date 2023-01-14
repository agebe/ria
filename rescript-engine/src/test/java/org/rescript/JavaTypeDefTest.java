package org.rescript;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class JavaTypeDefTest {

  @Test
  public void simple() {
    new Script().run("""
        import org.example.Foo;
        public class org.example.Foo {
          int a = 1;
          public Foo(int b) {
            a = b;
            char c = '}';
            // }
            String foo = \"\"\"
             abc
             }
            \"\"\";
          }
          public static void sayHello() {
            System.out.println("hello from org.example.Foo");
          }
        }
        public class Bar {
          @Override
          public String toString() {
            return "bar to string";
          }
        }
        println('test');
        Foo.sayHello();
        var foo = new Foo(1);
        println(typeof foo);
        println(new Bar());
        """);
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

public class ListOfInstantTypeToken extends TypeToken<List<Instant>> {
}

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
}
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
        public class A {
        }
        
        public class B extends A {
        }
        
        println(new B());
        """);
  }

  @Test
  public void extendsTest2() {
    new Script().run("""
        public abstract class A<T> {
          abstract T create();
        }
        public class B extends A<String> {
          @Override
          String create() {
            return "created by B";
          }
          @Override
          public String toString() {
            return create();
          }
        }
        println(new B());
        """);
  }

  @Test
  public void implementsTest4() {
    new Script().run("""
        public class B implements Consumer<List<String>> {
          @Override
          public void accept(List<String> o) {
            System.out.println(o);
          }
        }
        new B().accept(List.of(1,2,3));
        """);
  }

  @Test
  public void implementsTest() {
    new Script().run("""
        public class A implements Consumer<String> {
          public void accept(String s) {
            System.out.println(s);
          }
        }
        new A().accept("my message");
        """);
  }

  @Test
  public void implementsTest2() {
    new Script().run("""
        public class A implements Consumer<String>, Supplier<Integer> {
          @Override
          public void accept(String s) {
            System.out.println(s);
          }
          @Override
          public Integer get() {
            return 42;
          }
        }
        new A().accept("my message");
        """);
  }

  @Test
  public void interface1() {
    assertEquals("foobar", new Script().run("""
        public interface I1 {
          String foo();
        }
        public interface I2 {
          String bar();
        }
        public class B implements I1, I2 {
          @Override
          public String foo() {
            return "foo";
          }
          @Override
          public String bar() {
            return "bar";
          }
        }
        var b = new B();
        println(b.foo() + b.bar());
        return b.foo() + b.bar();
        """));
  }

  @Test
  public void interface2() {
    assertEquals("foobar", new Script().run("""
        public interface I1 {
          String foo();
        }
        public interface I2 extends I1 {
          String bar();
        }
        public class B implements I2 {
          @Override
          public String foo() {
            return "foo";
          }
          @Override
          public String bar() {
            return "bar";
          }
        }
        var b = new B();
        println(b.foo() + b.bar());
        return b.foo() + b.bar();
        """));
  }

  @Test
  public void interface3() {
    new Script().run("""
        @SuppressWarnings(value = {"unchecked", "foo"})
        @FunctionalInterface
        public interface I1 {
          String create();
        }
        """);
  }

  @Test
  public void extendsAndImplementsTest() {
    new Script().run("""
        @SuppressWarnings(value = {"unchecked", "foo"})
        public abstract class A<T> {
          public abstract T create();
        }
        public class B extends A<Map<List<String>, String>> implements Consumer<String> {
          @Override
          public Map<List<String>, String> create() {
            return null;
          }
          @Override
          public void accept(String s) {
          }
        }
        println(new B());
        """);
  }

  @Test
  public void enumTest() {
    new Script().run("""
        @SuppressWarnings(value = {"unchecked", "foo"})
        public enum E1 {
          A,
          B,
          C,
          ;
        }
        public class B {
          private E1 e = E1.C;
        }
        println(E1.A);
        """);
  }

  @Test
  public void recordTest() {
    new Script().run("""
        @SuppressWarnings(value = {"unchecked", "foo"})
        public record RecordTest(String s1, int i2) {
        }
        println(new RecordTest("record-test", 42));
        """);
  }

  @Test
  public void annotationTest() {
    new Script().run("""
        import static java.lang.annotation.RetentionPolicy.RUNTIME;
        import java.lang.annotation.Retention;
        @Retention(RUNTIME)
        public @interface TestAnnotation {
        }
        """);
  }

  @Test
  public void annotationTest2() {
    // FIXME if "default Integer.class;" below is replaced with "default { };" the test fails
    // it seems to be related to the javaTypeDefBody rule not detecting the end of the annotation body correctly
    new Script().run("""
        import static java.lang.annotation.RetentionPolicy.RUNTIME;
        import java.lang.annotation.Documented;
        import java.lang.annotation.Retention;
        @Documented
        @Retention(RUNTIME)
        public @interface NotEmpty {
          String message() default "{org.hibernate.validator.constraints.NotEmpty.message}";
          Class<?>[] groups() default Integer.class; //{ };
          // FIXME this breaks the test but it should work
          //Class<?>[] groups() default { };
          @Retention(RUNTIME)
          @Documented
          public @interface List {
            NotEmpty[] value();
          }
        }
        @NotEmpty.List({
            @NotEmpty( message = "Person name should not be empty",
                       groups=String.class),
            @NotEmpty( message = "Company name should not be empty",
                       groups=String.class),
        })
        public class A {
        }
        """);
  }

}
