
README file for the tmcm-java-source archive            March, 2000
--------------------------------------------

This archive contains the source code for a set of Java applets.
The applets were written for use with a textbook, The Most Complex
Machine, by David Eck.  They are meant to help teach some basic
concepts of computer science.  For more information about the
applets and for a set of lab worksheets that use the applets,
see:    http://math.hws.edu/TMCM/java/

*** NOTE ***  This source code was not originally written with
the intent of making it public.  There are very few comments in
the code.  Some parts of the code are a bit strange because they
were translated from Pascal programs.  So, take the code for
what it's worth...

The source code is contained in the directory named "tmcm".
All the classes for the applets belong to the package named
"tmcm" and to its sub-packages.  The sub-packages correspond
to subdirectories in the tmcm directory.

The applets were written in Java 1.0, so they use many
deprecated methods whose use is discouraged in Java 1.1 and
later.  When the source code is compiled, you might get 
warning messages about deprecated methods.  There will also
be MANY warning messages to the effect that a class should
not be used outside the file where it is defined, unless the
name of the class agrees with the name of the file where
it is defined.  (The development system that I used when I
wrote the applets didn't enforce this rule.)  This could 
cause a problem if you try to compile the files one at a
time, but it's OK as long you compile all the files
in a directory at the same time (or compile them in the
right order).  The warning messages are not errors and
will not stop the files from being compiled.

The code can be compiled with the "javac" command from
the JDK (Java Development Kit).  To use this command with
classes that are defined in the tmcm package or one of
its sub-packages, you must be in the directory that contains
the tmcm directory, and you have to specify the full path
to the file or files you want to compile.  For example,
to compile all the files in the package tmcm.xSortLab,
you would say

        javac tmcm/xSortLab/*.java      in Linux or UNIX
   or
        javac tmcm\xSortLab\*.java      in a DOS command window.
        
For convenience, I have included script files that will compile
all the classes in the tmcm file.  For Linux/UNIX, there is
a shell script named "compile.sh".  For DOS/Windows, there
is a DOS batch file named "compile.bat".  These scripts
will also build a .jar archive containing all the compiled
class files.  You can get more information by reading the
script files themselves.

(Note for Macintosh users:  Theoretically, it should be possible
to compile the files using Apple's SDK for Java, but I have not
been able to make it work.  The applets were originally written
with CodeWarrior for Macintosh.)

----------------------------------------------        
David Eck
Department of Mathematics and Computer Science
Hobart and William Smith Colleges
Geneva, NY   14456   USA
Email:  eck@hws.edu
WWW:    http://math.hws.edu/eck/
