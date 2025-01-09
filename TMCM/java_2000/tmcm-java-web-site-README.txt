
README file for the tmcm-java-web-site archive              March 2000
----------------------------------------------

The tmcm-java directory in this archive contains Web pages and
applets that are also available on line at:   

            http://math.hws.edu/eck/TMCM/java/

The Web pages include some lab worksheets that were written to
be used with my introductory computer science textbook, The Most
Complex Machine.  They can also be used on their own.  There are
also some information/tutorial pages for each applet.  The
main Web page is index.html, and it includes links to all the
other pages.

If you would like to use any of the material in this archive
for commercial purposes, please contact me for permission.
Contact information is given below.

The applets can be freely used for any non-commercial purpose.

The lab worksheets can be used for any private, non-commercial
purpose, but I ask that they not be used as an official part of
a course unless my textbook is adopted for that course.  (However,
I will consider making exceptions to this.)

You are welcome to post the entire, unmodified contents of
this archive on your own Web server.

You are also welcome to create non-commercial Web pages that
use the applets.  If you do this, you might want to create
your own sample input files for the applets.  To do that, you
will probably want to download the stand-alone application
version of the applets.  That version is in an archive named
tmcm-java-apps which is available through a link at the
address:  http://math.hws.edu/TMCM/newjava/DownloadingAndInfo.html


USING THE APPLETS ON YOUR OWN WEB PAGES:
---------------------------------------

To use one of the applets on a Web page, that page must have
access to the compiled Java program for that applet.  You can
find these programs in the directory named "classes" inside the 
tmcm-java directory.  The classes are in ".zip" files.  There is
one zip file for each applet:  DataReps.zip, xComputer.zip,
xLogicCircuits.zip, and so on.  To use one of the applets
on a Web page, you can copy the corresponding zip file into
the same directory as the HTML source file for your Web page.
The HTML source file will have an <applet> tag to load the
applet.  This applet tag must refer to the zip file and to
the applet class.  For example, the applet class for the
xLogicCircuits applet is "tmcm.xLogicCircuitsApplet.class".
An <applet> tag for using this applet has the form:

    <p align=center>
    <applet archive="xLogicCircuits.zip" 
            code="tmcm.xLogicCircuitsApplet.class"
            height=380 width=500>
    </applet>
    </p>

The classes for the other applets are named similarly:
tmcm.DataRepsApplet.class, tmcm.xComputerApplet.class, and
so on.  In addition to these applets, which appear right on
the web page, there are "launcher" versions of the applets.
In the launcher version, only a button appears on the Web
page.  When the user clicks the button, the applet is opened
in a separate window.  The names for the launcher versions
are tmcm.DataRepsLauncher.class, tmcm.xLogicCircuitsLauncher.class
and so on.  For example, to use the launcher version of
xLogicCircuits, you could use the <applet> tag

    <p align=center>
    <applet archive="xLogicCircuits.zip"
            code="tmcm/xLogicCircuitsLauncher.class"
            width=180 height=30>
    </applet>
    </p>

Some of the applets can load sample input files.  Such files
can be created using the "Save" button of one of the applets.
However, this button will generally not be functional when you
are running the applet in a Web browser.  If you want to use
the "Save" button, get the application version of the applets,
mentioned above.  (Or, if you have the Java Development Kit,
try running the applet with the appletviewer command.)

To be used by an applet, a sample input file should be in the
same directory as the HTML source file for the Web page that
contains the applet.  The names of the sample input files must
be specified as "params" in the <applet> tag.  For example, the
xLogicCircuits applet can read one sample file.  The file
is specified in a param named "LOAD".  For example, if
you want xLogicCircuits to load a sample file named
"SampleCircuits.txt", use the applet tag:

    <p align=center>
    <applet archive="xLogicCircuits.zip" 
            code="tmcm.xLogicCircuitsApplet.class"
            height=380 width=500>
        <param name = "LOAD" value = "SampleCircuits.txt">
    </applet>
    </p>

The param name, "LOAD", must be given in uppercase letters, as
shown.  Param names are case-sensitive.  You can also use an
input file with the launcher version of the applet:

    <p align=center>
    <applet archive="xLogicCircuits.zip"
            code="tmcm.xLogicCircuitsLauncher.class"
            width=180 height=30>
       <param name="LOAD" value="SampleCircuits.txt">
    </applet>
    </p>
    
The xComputer, xTuringMachine, xTurtle, and xModels applets can
load several sample files.  The files must be specified using
the param names "URL", "URL1", "URL2", and so on.  You have to
be careful to use the right names, without any omissions.
(If there is no URL2, for example, the applet won't even check for
URL3.)  For example, to use four sample input files with the
launcher version of the xComputer applet, you could use the
following tag on your web page.

    <applet archive="xComputer.zip"
            code="tmcm.xComputerLauncher.class"
            width=150 height=30>
       <param name="URL" value="SimpleCounter.txt">
       <param name="URL1" value="MultiplyByAdding.txt">
       <param name="URL2" value="ThreeNPlusOne.txt">
       <param name="URL3" value="ListSum.txt">
    </applet>


The preceding tags assume that the zip files and sample input
files are in the same directory with the HTML source file of
the Web page.  It's possible to put them in other directories.
If the zip file is not in the directory with the HTML file, then
you must specify a codebase in the applet tag.  The codebase
in an <applet> tag is the directory that contains the compiled
Java code for the applet.  It is specified relative to the
directory that contains the Web page.  For example:

    <p align=center>
    <applet codebase="../classes/" archive="xLogicCircuits.zip"
            code="tmcm.xLogicCircuitsLauncher.class"
            width=180 height=30>
       <param name="LOAD" value="SampleCircuit.txt">
    </applet>
    </p>

Here, the codebase directory is found by going up to the directory
that contains the Web page (specified as "../") and then looking
in that directory for a directory named "classes/".  (I've found
that the "/" at the end of the directory name is necessary, at least
for some browsers.)

It's a good idea to have just one copy of a .zip file, even if you
are going to use the applet on several Web pages.  Then, if the
user visits several of those pages, the Web browser will only have
to download one zip file.  That's why I put all the zip files
in one "classes" directory on my own site.

You can also have sample input files in a different directory
from the Web page.  Just include the directory name in the
name of the input file.  For example, "samples/SampleCircuits.txt"
or "../models/FirstModel.txt".  The name should be given relative
to the directory that contains the Web page.


----------------------------------------------
David Eck
Department of Mathematics and Computer Science
Hobart and William Smith Colleges
Geneva, NY   14456    USA
Email:  eck@hws.edu
WWW:    http://math.hws.edu/eck/
