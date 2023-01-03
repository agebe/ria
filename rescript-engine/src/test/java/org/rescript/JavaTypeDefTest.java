package org.rescript;

import org.junit.jupiter.api.Disabled;
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
dependencies {
  'com.google.code.gson:gson:2.10'
}

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

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
  @Disabled // TODO add support for interface types
  public void implementsTest3() {
    new Script().run("""
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
        """);
  }

  @Test
  public void extendsAndImplementsTest() {
    new Script().run("""
        public class A {
        }
        
        public class B extends A {
        }
        
        println(new B());
        """);
  }

}
