# ria

ria is a interpreted [JVM](https://en.wikipedia.org/wiki/Java_virtual_machine) scripting language. The syntax is quite close to Java with some Javascript, Groovy and Gradle mixed in. Scripts can be run from the CLI and executed with the native launcher or integrated into your java programs. It comes with builtin dependency management that is similar to Gradles. The firewall feature allows to define limits on what the script can do. This might be interesting If you want to allow e.g. customers to write configuration scripts that run on your JVM without being able exit the JVM or do other nasty things.

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

The second line simply outputs 'hello, world!' to the console. The seasoned java developer probably noticed 2 things here: The println function and the single quote enclosed strings.

The println function is simply a variable that is setup by the script engine automatically. It points to System.out.println method so println and System.out.println are interchangeable. The script engine allows to assign method reference to variables. Let's try this:

```java
println(typeof println);
println(println);
println(System.out::println);
var p = System.out::println;
p('printed with my own System.out::println method reference!');
```

Java uses the single quotes as character literals. Since strings are used more often and the script could be embedded in Java source code the script language uses double and single quotes for enclosing strings (no difference). If this was not the case embedded script in java would have to escape the double quotes like so:

```java
String myScript = "println(\"hello world\");";
```

Not much is lost however by not having a char literal. The script engine uses a more aggressive cast approach than java. For example to define a char typed variable you could do this:
```java
char myChar = "a";
println(typeof myChar);
```
The script engine simply looks at the expected target type and attempts a type conversion automatically. This would obviously fail for strings that can not be converted to a single charater.

More script examples can be found in the [script examples directory](https://github.com/agebe/ria/tree/main/ria-engine/src/test/resources/org/ria/example) or for java embedded scripts look at the [500+ JUnit tests](https://github.com/agebe/ria/tree/main/ria-engine/src/test/java/org/ria).

# Syntax

Keywords, Literals, Operators, Statements and Expressions

TODO, write documentation

# Dependencies

TODO, write documentation

# Java Types

TODO, write documentation

# Firewall

TODO, write documentation

# Debugging

TODO, write documentation
