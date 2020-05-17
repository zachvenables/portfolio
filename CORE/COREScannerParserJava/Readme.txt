--------
Project 1
--------

This is a simple scanner for the Core programming language

-------
Run
------
Compile the Main.java file, then run Main and as a command line argument, type the filename that you want to scan.

--------
Known Bugs
--------

	When encountering an error, the program will print an error to the screen when it is scanned in.  Because
the scanner builds a list of all the tokens first, the program will not terminate until the main functions 
attempts to access the EOS token that is passed back from the scanner.