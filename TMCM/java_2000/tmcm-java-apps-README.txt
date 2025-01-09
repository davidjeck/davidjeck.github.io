
README file for the tmcm-java-apps archive                  March 2000
------------------------------------------

This archive contains Java applets and sample input files for the
applets.  The applets are meant to help teach some of the basic concepts
of computer science.  They were written for use with my textbook, The Most 
Complex Machine, but they can also be used independently of the book.
For more information, see:  http://math.hws.edu/TMCM/java/
(If you want to understand the applets, you will have to look at
the material at this address.)

The material in this archive can be freely redistributed and used for
non-commercial purposes.

Several Java "applications" are included here that will let you run the
applets without a Web browser.  One big advantage of doing this is that
you will be able to save and load files.  For example, you could create
sample files to use with the applets on your own Web pages.   There are
three applications.  One simply makes the applets available, without
loading any sample files.  Another loads the applets with the sample
data files used in the labs worksheets from http://math.hws.edu/TMCM/java/.
The third application loads the applets with the sample files and 
tutorial examples from the applet information pages at the same address.

(The idea for the latter two applications is that you could read the
Web material in a Web browser at the same time that you run the applets
using the applications.  This will let you use files with the applets.
When an applet is run in a Web browser, it probably won't be able to
save and load files.  If you've downloaded the labs and tutorials
for use on your own computer, you can view them in a Web browser,
but in that case, the applets in the Web browser will be even more limited.
They probably won't even be able to load the sample files that are
used in the labs and tutorials!)

All the sample files are included in this archive.  They have names
that end with ".txt", and they are really just plain text files that
you can read with a text editor, if you want.  I know it's messy to have
all these files in one directory, but I couldn't find a way to get the
applets to work reliably with the sample files, unless the files are
in the same directory with the applets.

The applets themselves are contained in the file tmcm.jar.  This is
a "Java archive file".  In addition to the applets, this file contains
the three applications described above.  The names of these applications
are "tmcm.Apps", "tmcm.Labs", and "tmcm.Tutorials".  (The funny names
arise because the applications are in the Java "package" named tmcm.)

When you run one of these applications, a window will pop up.  The window
contains several buttons.  Click on a button to launch one of the applets.
For the applications tmcm.Labs and tmcm.Tutorials, the applets will 
load the appropriate sample files.  For tmcm.Apps, the applets do not
load any files.  You can move tmcm.jar to another location and still
use it to run tmcm.Apps.  However, if you want to run tmcm.Labs or
tmcm.Tutorials, the tmcm.jar file must be in the same directory as
the sample files.


To run the applications, you need the tmcm.jar file plus some additional
Java software.  Here are specific instructions for various platforms:

  For Windows:
  -----------
      If you have Microsoft Internet Explorer with Java support, then
  you should be able to use the "jview" command in a DOS Window.  To run
  the applications in tmcm.jar, open a DOS window and change to the
  tmcm-java-apps directory.  This is the directory that you got when
  you unzipped this archive.  (Tip: Open a directory window for this
  directory.  Then select the "Run" command from the Start menu and
  enter "command" in the dialog box that pops up.  This will open a
  DOS window, already set to use tmcm-java-apps as its working directory.)
  You have to tell jview to put tmcm.jar on its "classpath".  This
  is done by adding the option "-cp tmcm.jar" to the command.  So, the
  commands for running the three applications are:
  
          jview  -cp tmcm.jar  tmcm.Apps
          jview  -cp tmcm.jar  tmcm.Labs
          jview  -cp tmcm.jar  tmcm.Tutorials
          
  Alternatively, if you have the JDK (Java Development Kit) installed on
  your computer, you can use the JDK's "java" command to run the 
  applications.  This is similar to the jview command, but unfortunately,
  you also have to specify the location of the standard system classes.
  These are contained in a file named "classes.zip" in one of the JDK
  directories.  You need the full path name of this file, such as
  C:\jdk1.1.8\lib\classes.zip.  (This is the correct name if you using
  version 1.1.8 of the jdk and did the default installation.  For other
  versions of the jdk, only the numbers should be different.)  Once you've
  found this file, the commands for running the applications are:
  
      java  -classpath tmcm.jar;C:\jdk1.1.8\lib\classes.zip  tmcm.Apps
      java  -classpath tmcm.jar;C:\jdk1.1.8\lib\classes.zip  tmcm.Labs
      java  -classpath tmcm.jar;C:\jdk1.1.8\lib\classes.zip  tmcm.Tutorials
      
  You can download the JDK for Windows from Sun Microsystem's Web
  site at:   http://java.sun.com/products/jdk/1.1/
      
      
  For Macintosh:
  -------------    
      You need to have the MRJ (Macintosh Runtime for Java) installed on
  your Macintosh in order to run the applications in tmcm.jar.  This might
  have been installed with your original system.  If not, it can be 
  downloaded from:   http://www.apple.com/java/   (I think you will need
  version 2.1.4 or higher, but I haven't tried it with earlier versions.)
      The Macintosh version of the tmcm-java-apps archive includes
  three double-clickable applications.  Double-click the program named
  "Run TMCM Applets" to run tmcm.Apps.  You can move this program to
  another location, as long as you include a copy of the tmcm.jar file
  in the same directory.  The other two programs need both tmcm.jar and
  all the sample files.  Double-click the program named
  "Run With Lab Examples" to run tmcm.Labs, and double-click the
  program named "Run With Tutorial Examples" to run tmcm.Tutorials.
  

  For Liunx and UNIX:
  ------------------
     If you have the JDK (Java Development Kit) installed on
  your computer, you can use the JDK's "java" command to run the 
  applications.  The JDK is included in most Linux
  distributions, although it might not have been installed by
  default.  JDK for Solaris can be downloaded from the Sun
  Website,   http://java.sun.com/products/jdk/1.1/
  For other versions of UNIX... you're on your own.
     To use the java command with tmcm.jar, you will need to know
  the location of the standard system classes.  These are contained in
  a file named "classes.zip" in the "lib" subdirectory of the JDK
  installation.  You need the full path name of this file.  On my
  SuSE Linux system, this is /usr/lib/java/lib/classes.zip.  If you
  have trouble finding it on your system, try using the command
  "type java" to find out the full path name of the java command.
  The java command is in the "bin" subdirectory of the JDK installation.
  Once you've found the classes.zip file, you can use the following
  commands to run the applications.  Substitute the appropriate
  pathname for my "/usr/lib/java/lib/classes.zip":
  
     java  -classpath tmcm.jar:/usr/lib/java/lib/classes.zip  tmcm.Apps
     java  -classpath tmcm.jar:/usr/lib/java/lib/classes.zip  tmcm.Labs
     java  -classpath tmcm.jar:/usr/lib/java/lib/classes.zip  tmcm.Tutorials

  (If you plan to use these commands often, I would suggest making a
  shell script or an alias.)

----------------------------------------------
David Eck
Department of Mathematics and Computer Science
Hobart and William Smith Colleges
Geneva, NY   14456    USA
Email:  eck@hws.edu
WWW:    http://math.hws.edu/eck/
