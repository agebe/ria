# ria

ria is an interpreted [JVM](https://en.wikipedia.org/wiki/Java_virtual_machine) scripting language. The syntax is quite close to Java with a little Javascript, Groovy and Gradle mixed in. Scripts can be run from the CLI and executed with the native launcher or integrated into your Java programs. The script system uses the same data types as Java and scripts are meant to use the JCL (Java Class Library) like e.g. java.util.List. It comes with built-in dependency management that is similar to Gradles to easily use libraries published in any maven repository. The firewall feature allows to define limits on what the script can do. This might be interesting If you want to allow e.g. customers to write configuration scripts that run on your JVM without e.g. being able exit the JVM.

# Requirements

ria requires a Java 17+ JVM to run.

# Native launcher

To execute scripts from the CLI download the native launcher from this git repo. Linux amd64 is the only supported platform at the moment. The native launcher comes either as a minimal version (ria-0.1.0-linux-amd64) that downloads and caches required libraries from maven central. If you do not have internet access to maven central in your environment use ria-all-0.1.0-linux-amd64.

```bash
wget -O ria https://github.com/agebe/ria/releases/download/0.1.0/ria-0.1.0-linux-amd64
chmod +x ria
sudo mv ria /usr/local/bin
```

# Maven/Gradle dependency

To integrate the script engine into your Java program add the following dependency to your build tool.

## Maven
```xml
<dependency>
  <groupId>io.github.agebe</groupId>
  <artifactId>ria-engine</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Gradle
```gradle
implementation 'io.github.agebe:ria-engine:0.1.0'
```

# Hello World

Let's jump right in and write some example scripts.

## From the CLI

Create the file hello.ria with the following contents.

```java
#!/usr/bin/env ria
println('hello, world!');
```

If you make your script file executable you can just execute it as is otherwise use

```bash
$ ria hello.ria
hello, world!
```

The language as 2 types of line comments, the java style // and the script style # sign which is used in the first line of the hello world program. The first line tells the program loader which interpeter to use to execute the script, also known as [shebang](https://en.wikipedia.org/wiki/Shebang_(Unix)). Since the first line is a comment for the script engine it will ignore it and hence it is optional.

The second line simply outputs 'hello, world!' to the console. If you have a Java background you might have noticed that the script does not have a surrounding class nor main method, the println function and the single quote enclosed strings. The script language does not have its own classes/methods but you can break your script code up into functions though. Every script statement that is not part of a function is implicitly wrapped into the main function. More on functions is documented a bit furhter down in this README (TODO).

The println function is simply a variable that is setup by the script engine automatically before the script is run. It points to System.out.println method so println and System.out.println are interchangeable. The script engine allows to assign method reference to variables. Let's try this:

```java
println(typeof println);
println(println);
println(System.out::println);
var p = System.out::println;
p('printed with my own System.out::println method reference!');
```

If you execute the above code you should see something like this

```java
method
method java.io.PrintStream::println, on object java.io.PrintStream@b3ca52e
method java.io.PrintStream::println, on object java.io.PrintStream@b3ca52e
printed with my own System.out::println method reference!
```

Java uses the single quotes for character literals. Since strings are used more often and the script could be embedded in Java source code the script language uses double and single quotes for enclosing strings (no difference). If this was not the case embedded script in java would have to escape the double quotes like so:

```java
String myScript = "println(\"hello world\");";
```

Not much is lost however by not having a char literal. The script engine uses a more aggressive cast approach than java. For example to define a char typed variable you could do this:
```java
char myChar = "a";
println(typeof myChar);
```
The script engine simply looks at the expected target type and attempts a type conversion automatically. This would obviously fail for strings that can not be converted to a single charater.

More script examples can be found in the [script examples directory](https://github.com/agebe/ria/tree/main/ria-engine/src/test/resources/org/ria/example) or for Java embedded scripts look at the [500+ JUnit tests](https://github.com/agebe/ria/tree/main/ria-engine/src/test/java/org/ria).

# Syntax

The script language syntax is so similar to Java's that this section mostly points out the differences between the two. The script has 2 sections a header and a body. The optional header is used to define additional imports and dependencies. The script body contains the statements to be executed. Java imports the java.lang package by default but the script language runtime imports most commonly used packages from the JCL (Java Class Library) and also all packages from direct dependencies.

## Header

The script header is used to define imports and dependencies.

Dependencies refer to libraries that are published in a maven repository. By default the script runtime will use maven central to download dependencies but this can be customized e.g. to add additional repositories or use your own repository only. The syntax follows Gradle like so:

```javascript
dependencies {
  'org.apache.commons:commons-lang3:3.12.0'
  'commons-io:commons-io:2.11.0'
}
```
In gradle you need to specify the configuration like 'implementation' or 'runtime' but you have to omit this in the script as dependencies are 'runtime' only. If you specify a string dependency (as in the example above) the script runtime tries to determine the type of dependency automatically. Currently 4 types are supported: gradle short, [maven](ria-engine/src/test/resources/org/ria/example/maven.ria), [local file](ria-engine/src/test/resources/org/ria/example/logback.ria) and [local directory](ria-engine/src/test/resources/org/ria/example/filetree.ria) (aka filetree). When you specify a file or filetree dependency the file(s) appear(s) as is in the classpath except for jar/zip files which are extracted and the contents are made available on the classpath.

If the dependency resolves to a jar file (either from a maven repository or local) the java packages are all added the the imports automatically if the jar contains packages. Try this:
```javascript
dependencies {
  'org.apache.commons:commons-lang3:3.12.0'
}
println($imports);
```
$imports is a special variable that is setup by the script runtime. It is a string containing all the imports that are available to the script. You can use it to e.g. troubleshoot your imports. The above script should print the following.
```java
import java.lang.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.math.*;
import java.text.*;
import java.time.*;
import java.time.chrono.*;
import java.time.format.*;
import java.time.temporal.*;
import java.time.zone.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import org.apache.commons.lang3.*;
import org.apache.commons.lang3.arch.*;
import org.apache.commons.lang3.builder.*;
import org.apache.commons.lang3.compare.*;
import org.apache.commons.lang3.concurrent.*;
import org.apache.commons.lang3.concurrent.locks.*;
import org.apache.commons.lang3.event.*;
import org.apache.commons.lang3.exception.*;
import org.apache.commons.lang3.function.*;
import org.apache.commons.lang3.math.*;
import org.apache.commons.lang3.mutable.*;
import org.apache.commons.lang3.reflect.*;
import org.apache.commons.lang3.stream.*;
import org.apache.commons.lang3.text.*;
import org.apache.commons.lang3.text.translate.*;
import org.apache.commons.lang3.time.*;
import org.apache.commons.lang3.tuple.*;
```

If no repositories are defined the script runtime uses maven central as if it were defined like so:
```java
repositories {
  mavenCentral()
}
```
If you'd like to add repositories do this:
```javascript
repositories {
  'https://my-own-repo.com'
  mavenCentral()
}
```

Imports have the same syntax and semantics as in Java and static imports are supported too. Java has a single default import which is all classes from the java.lang package. The script runtime does this too but it also adds commonly used packages from the JCL. The reason is simply for convenience as writing the script will most likely happen in a text editor without IDE support (unless you write IDE support, please let me know if you do). In the script runtime the java.lang.* import always comes first, then come all your custom imports you write down in the header and finally come all additional imports. The additional imports are defined in the class [org.ria.Options](ria-engine/src/main/java/org/ria/Options.java). The script language defines additional keywords (see below) which can sometimes clash with package names. As an example the package java.util.function contains the function keyword. The script runtime fails when importing this package with an exception. A workaround is to use a string import instead like so:

```java
import "java.util.function.*";
```

Note that the java.util.function package is also a default import so no need to write it down in the header.

The package keyword is reserved in the script language but not supported. The script does not produce a type nor byte code so there is no point supporting the package keyword imho.

## Type System

## Literals

The script has all of the Java literals too except for the char literal. Single quotes are used for string literals and there is no difference when enclosing strings in double or single quotes. Java textblocks are also supported and they work the same way like so:

```java
var myTextBlock = """
  row1
  row2""";
```

To be consistent with single line String the textblocks can also be enclosed with 3 single quotes.

ria script has a list, array and map literal.

```java
  var myList = [1,2,3];
  var myArray = arrayof [1,2,3];
  var myMap = ['key1':'value1','key2':'value2'];
```

The list literal creates a mutable ArrayList and the map literal a mutable LinkedHashMap. When using the array literal the array has the type of the common super class following [this approach from StackOverflow](https://stackoverflow.com/a/9797689)

## Keywords

All Java keywords are reserved in the script language and can't be used as identifiers but not all keywords are supported like e.g. public/protected/private. Also there are some new keywords and they are described below.

### arrayof
The arrayof operator is used to create an array without specyfing a type.
```java
var myArray = arrayof [1,2,3];
```
produces an int array.

### function

Functions allow to break up your code into reusable code blocks. The syntax follows the Javascript syntax.

```javascript
function square(number) {
  return number * number;
}
println(square(2));
```

The first 3 lines define the function square and the last line calls the function. As you can see in the example return and parameter types are omitted in the function declaration. The return statement is optional as the function always returns the last evaluated expression but if used it forces the function to exit.

Similar to Javascript functions can be nested to create private inner functions.

The script language uses the term function instead of method as they exist outside of an enclosing type as nicely described on [stackoverflow](https://stackoverflow.com/a/155655/20615256).

### javasrc
The script is interpreted only and no byte code is created. This should be sufficient in most cases when you use the JCL (Java Class Library) or any other Java library but sometimes you might need to create a custom type to make the libary work for you like e.g. register a custom type. There are a few ways how you could go about this. An obvious solution would be to create a jar file including your custom types and add it to the classpath for your script to use. If you'd rather embed Java code directly into your script this is where the javasrc keyword comes in.

```java
javasrc '''
public class A {
  public void sayHello() {
    System.out.println("hello from Java");
  }
}''';
new A().sayHello();
```

To define Java types in your script all you need to do is write the javasrc keyword followed by an expression that evaluates to a string followed by an semicolon. The string is handed over to the Java Compiler and the compiled class is added to the claspath automatically for your script to use. This way you can create custom classes, interfaces, enums, records and annotations from inside your script. Note that you can't reference any script variables or call any script functions from within the embedded java code.

The javasrc statement can appear anywhere in the script that allows for statements so you can put them e.g. next to where they are used. However, the script runtime collects all the javasrc statements and compiles them when the script header exits in the order they were defined.

Tip: To make all the script imports available to your Java types do this:
```java
javasrc $imports + '''
public class A {
  public List<Object> myList = new ArrayList<>();
}''';
```

### typeof

The typeof operator returns a string indicating the type of the operand's value. This is the same as [Javascript's typeof](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/typeof).

### var and val

This is used for declaring variables/values with type inference. The var keyword declares a variable and the type is inferred from the assignment. The val keyword does the same as var but only allows a single assignment like 'final' in Java. For variables declared with var the type can change over the variables lifetime as shown in the example below.

```java
var a;
println(typeof a);
a = "a";
println(typeof a);
a = 1;
println(typeof a);
```
prints:

```
java.lang.Object
java.lang.String
int
```

Variables can be made immutable by declaring them with the val keyword. The assignment can follow immediately or some time later in the code but only the first assignment is allowed. The script language does not support Java's final keyword.

Find more about this [here](https://stackoverflow.com/a/49427377/20615256) or [here](https://mail.openjdk.org/pipermail/platform-jep-discuss/2016-December/000066.html)

## Multi assign

## Operators

## Statements and Expressions

### Object scope expression

### Exceptions and try-catch-finally

## Lambda

## Generics

# Dependencies

TODO Repositories and dependencies and the classpath

# Firewall

TODO, write documentation

# Debugging

TODO, write documentation
