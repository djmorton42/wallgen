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

package ca.quadrilateral.wallgenerator.ui;

import info.clearthought.layout.TableLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;


public class PreviewDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private double[][] layoutDescriptor = null;
    private TableLayout layout = null;

    private JButton doneButton = new JButton("Done");
    private ImagePreviewPanel imagePreviewPanel = null;

    public PreviewDialog(BufferedImage image, JFrame owner) {
        super(owner, "Image Preview", true);
        this.setSize(image.getWidth() + 50, image.getHeight() + 100);

        layoutDescriptor = new double[][] {{5, TableLayout.FILL, image.getWidth(), TableLayout.FILL, 5 }, {5, TableLayout.FILL, image.getHeight(), TableLayout.FILL, TableLayout.PREFERRED, 5 }};
        layout = new TableLayout(layoutDescriptor);
        this.getContentPane().setLayout(layout);
        imagePreviewPanel = new ImagePreviewPanel(image);

        this.add(imagePreviewPanel, "2, 2");
        this.add(doneButton, "1, 4, 3, 5, r, f");

        this.doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                PreviewDialog.this.setVisible(false);
                PreviewDialog.this.dispose();
            }
        });
    }
}
