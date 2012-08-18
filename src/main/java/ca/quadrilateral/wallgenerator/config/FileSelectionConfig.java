/*
Copyright 2011 Daniel Morton. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY Daniel Morton ''AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Daniel Morton OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the
authors and should not be interpreted as representing official policies, either expressed
or implied, of Daniel Morton.
*/

package ca.quadrilateral.wallgenerator.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSelectionConfig {
	private File firstStorySourceFile = null;
	private File secondStorySourceFile = null;
	private File thirdStorySourceFile = null;
	private File destinationDirectory = null;
	private String baseOutputFileName = null;
	private String storyMethod = "Tiled";
	private int stories = 1;
	
	public File getFirstStorySourceFile() {
		return this.firstStorySourceFile;
	}
	public void setFirstStorySourceFile(File firstStorySourceFile) {
		this.firstStorySourceFile = firstStorySourceFile;
	}
	public File getSecondStorySourceFile() {
		return this.secondStorySourceFile;
	}
	public void setSecondStorySourceFile(File secondStorySourceFile) {
		this.secondStorySourceFile = secondStorySourceFile;
	}
	public File getThirdStorySourceFile() {
		return this.thirdStorySourceFile;
	}
	public void setThirdStorySourceFile(File thirdStorySourceFile) {
		this.thirdStorySourceFile = thirdStorySourceFile;
	}
	public File getDestinationDirectory() {
		return this.destinationDirectory;
	}
	public void setDestinationDirectory(File destinationDirectory) {
		this.destinationDirectory = destinationDirectory;
	}
	public String getBaseOutputFileName() {
		return this.baseOutputFileName;
	}
	public void setBaseOutputFileName(String baseOutputFileName) {
		this.baseOutputFileName = baseOutputFileName;
	}
	public int getStories() {
		return this.stories;
	}
	public void setStories(int stories) {
		this.stories = stories;
	}
	public String getStoryMethod() {
		return this.storyMethod;
	}
	public void setStoryMethod(String storyMethod) {
		this.storyMethod = storyMethod;
	}
	
	public boolean hasValidSourceFiles() {
		if (this.storyMethod.equals("Tiled") || this.storyMethod.equals("Single File")) {
			if (this.firstStorySourceFile == null) {
				return false;
			} else {
				return this.firstStorySourceFile.canRead();
			}
		} else {
			boolean foundValidFile = false;
			for(File file : getSourceFiles()) {
				if (file != null && file.canRead()) {
					foundValidFile = true;
				}
			}
			return foundValidFile;			
		}
	}
	
	public List<File> getSourceFiles() {
		final List<File> sourceFiles = new ArrayList<File>();
		sourceFiles.add(this.firstStorySourceFile);
		sourceFiles.add(this.secondStorySourceFile);
		sourceFiles.add(this.thirdStorySourceFile);
		return sourceFiles;
	}
	
	public boolean hasValidTargetDirectory() {
		return (destinationDirectory.exists());		
	}
}
