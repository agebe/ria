# ria

ria is an interpreted [JVM](https://en.wikipedia.org/wiki/Java_virtual_machine) scripting language. The syntax is quite close to Java with some Javascript, Groovy and Gradle mixed in. Scripts can be run from the CLI and executed with the native launcher or integrated into your Java programs. The script system uses the same data types as Java and script are meant to use Java types (e.g. java.util.List). It comes with builtin dependency management that is similar to Gradles to easily use libraries published in any maven repository. The firewall feature allows to define limits on what the script can do. This might be interesting If you want to allow e.g. customers to write configuration scripts that run on your JVM without being able exit the JVM or do other nasty things.

# Requirements

ria requires a Java 17+ JVM to run.

## Native launcher

To execute scripts from the CLI download the native launcher from this git repo. The only supported platform is Linux AMD64 at the moment. The native launcher comes either as a minimal version (ria-0.1.0-linux-amd64) that download and caches required libraries from maven central. If you do not have internet access to maven central in your environment use ria-all-0.1.0-linux-amd64.

```bash
wget -O ria https://github.com/agebe/ria/releases/download/0.1.0/ria-0.1.0-linux-amd64
chmod +x ria
sudo mv ria /usr/local/bin
```

## Maven/Gradle dependency

To integrate the script engine into your Java program add the following dependency to your build tool.

### Maven
```xml
<dependency>
  <groupId>io.github.agebe</groupId>
  <artifactId>ria-engine</artifactId>
  <version>0.1.0</version>
</dependency>
```

### Gradle
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

The script language syntax is so similar to Javas that this section mostly points out the differences between Java syntax and the script language syntax.

## Keywords

### arrayof

### function

### javasrc

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

## Operators

## Statements and Expressions

### object scope expression

### try-catch-finally

## Lambda

## Generics

# Dependencies

TODO, write documentation

# Java Types

TODO, write documentation

# Firewall

TODO, write documentation

# Debugging

TODO, write documentation
