Wall Generator Source

Wall Generator is a tool for generating wall art files for DungeonCraft, a modern open-source version of Forgotten Realms Unlimited Adentures by SSI.


Wall Generator is licensed under the Simplified BSD License.  Please see the /etc/licenses/wallgen-license.txt file for more details.

Wall Generator is build using Apache Maven 3.  Apache Maven 2 should also build the software correctly.

Two libraries not available from standard maven repositories are included in the /etc/lib folder.  These files are part of the Java Advanced Imaging API:

jai_core-1.1.2_01.jar
jai_codec-1.1.2_01.jar

In order to build Wall Generator, these two jar files must be installed in your local Maven repository using the following POM snippets:

    <dependency>
      <groupId>com.sun.media.jai</groupId>
      <artifactId>jai_codec</artifactId>
      <version>1.1.2_01</version>
    </dependency>

    <dependency>
      <groupId>com.sun.media.jai</groupId>
      <artifactId>jai_core</artifactId>
      <version>1.1.2_01</version>
    </dependency>

A batch and shell script are included in the /etc/lib directory to automatically install these files into your local Maven Repo, assuming you have the Maven executable in your command path.

After the required artifacts are installed into your local Maven repository you are ready to build the software.

To build the software, having the Maven executable in your command path, simply type:

mvn clean install

from the root of the source folder (where the pom.xml file is located).
