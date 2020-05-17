Author: Zachary Venables
Files submitted: myinterpreter.java
	file contains a myinterpreter class that is made up of classes of non terminals.

One thing you may want to note while grading, is that the function object calls a seperate recursive tree, to keep the tables 
of variable exclusive from the main running program.


----------------
Run instructions
----------------
This is a java program using 3 separate files, if you know how to package, then you can just compile and run
using "java project.myinterpreter args0 args1 etc..."

all files have package name "project"
To package the files, Start with the Core.java file and type:
javac Core.java
then:
javac -d . Core.java

Now compile Scanner.java with : 
javac Scanner.java
then: 
javac -d . Scanner.java

Finally compile myinterpreter.java:
javac myinterpreter.java
then:
javac -d . myinterpreter.java

now you can run the project by typing the command: 
java project.myinterpreter args0 args1
