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

package ca.quadrilateral.wallgenerator.filechooser;

import java.io.File;

import javax.swing.JFileChooser;

public class TargetDirectoryFileChooser extends JFileChooser {
	private static final long serialVersionUID = 1L;

	public TargetDirectoryFileChooser() {
		final String executionDirectory = System.getProperty("user.dir");

		this.setMultiSelectionEnabled(false);
        this.setDialogType(JFileChooser.OPEN_DIALOG);
        this.setDialogTitle("Select Output Directory");
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.setCurrentDirectory(new File(executionDirectory));
	}
}
