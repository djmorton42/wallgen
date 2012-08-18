Wall Generator (Wallgen)

v{version} {date}

By Daniel Morton

Wallgen is a tool used to convert an image file in GIF, JPG, BMP or PNG format into an image
suitable for use in applications that render 3D-like, first person perspectives like Dungeon
Craft (http://uaf.sourceforge.net/).

Wallgen is free, open source software, licensed under the Simplified BSD license.  The source 
code is available for download from www.quadrilateral.ca/wallgen.

Special thanks to Eric Cone for a great deal of testing and for providing the necessary
configuration information to enable hyper-wall and square viewport wallset generation.

Please send feedback and bug reports to djmorton@quadrilateral.ca

Icons used under the Creative Commons License v3.0 from http://www.famfamfam.com/lab/icons/silk/

Usage
*****

Wallgen requires that a copy of the Java 6 Runtime Environment be installed on your system.
The Java Runtime can be downloaded from 
http://www.oracle.com/technetwork/java/javase/downloads/index.html

A shell script and batch file are included in the distribution for use in executing the
application under Linux or Windows.  Both scripts require that the Java executable be in
the command path.  To find out if you have Java in your command path, type 'java -version'
from a command prompt.  If you receive a message informing you of the version of Java you
are running, you have Java in your command path.  If you do not wish to add Java to your
command path, you may execute the application by running the following command:

Linux:

/PATH_TO_JAVA_EXECUTABLE/java -jar wall-generator-{version}.one-jar.jar

Windows:

DRIVE_LETTER:\PATH_TO_JAVA_EXECUTABLE\java.exe -jar wall-generator-{version}.one-jar.jar


CHANGE LOG
**********

[0.9.0]
- Initial Release

[0.9.1]
- Increased height of File Chooser panel
- Transparency is now respected when darkening distant walls

[0.9.2]
- Replaced resizing with third party imaging library
- Added options for Unshapen Masking, Softening, and various interpolation methods for scaling

[0.9.3]
- Fixed a bug where the distance darkening was not correctly operating independantly for near 
    walls and far walls
- Fixed a bug where some images, particularly on windows, would display oddly if the 'Resample' 
    image scaling method was using in combination with softening.

[0.9.4]
- Added the ability to specify the color used for blending with distance walls to create the 
    darkening effect
- Added icons for many of the command buttons
- Added support for 2 and 3 story walls in Hyper Wall format, using either a single image 
    vertically tiled, individually specified images or a single pre-sized images.

[0.9.5]
- Fixed bug where standard wall formats were not rendering transparent regions and outlines
    correctly
- Added image preview to image source selection dialog
- Added Nimbus Look and Feel for Java Virtual Machines that support it
- Added the ability to save and load wall generation settings for later reuse.
- Added the ability to specify that settings should be used by default and loaded at startup
    time when saving presets
- Overhauled the UI for wall generation settings
