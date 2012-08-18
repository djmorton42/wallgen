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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.quadrilateral.wallgenerator.Controller;
import ca.quadrilateral.wallgenerator.WallGenerator;

public class WallGeneratorFrame extends JFrame {
	private static final Logger log = LoggerFactory.getLogger(WallGeneratorFrame.class);
    private static final long serialVersionUID = 1L;

    private Controller controller = null;

    public WallGeneratorFrame() {
        super("WallGenerator v{version}");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(900, 650);

        final FileChooserPanel fileChooserPanel = new FileChooserPanel();
        final OptionPanel optionPanel = new OptionPanel();

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(fileChooserPanel, BorderLayout.NORTH);
        this.getContentPane().add(optionPanel, BorderLayout.CENTER);

        double[][] buttonPanelLayoutDescriptor = new double[][] {{5, TableLayout.FILL, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5},{5, TableLayout.PREFERRED, 5}};
        TableLayout buttonPanelLayout = new TableLayout(buttonPanelLayoutDescriptor);
        JPanel buttonPanel = new JPanel(buttonPanelLayout);

        JButton exitButton = new JButton("Exit");
        JButton reloadConfigButton = new JButton("Reload Config");
        buttonPanel.add(new JLabel("Wall Generator - By Daniel Morton (djmorton@quadrilateral.ca) - www.quadrilateral.ca/wallgen"), "1, 1, l, f");
        buttonPanel.add(reloadConfigButton, "2, 1");
        buttonPanel.add(exitButton, "4, 1");

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                WallGeneratorFrame.this.setVisible(false);
                log.info("Wall Generation Application Terminated");
                System.exit(0);
            }
        });

        reloadConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    WallGenerator.getConfigManager().reloadConfig();
                    controller.clearGeneratedImages();
                    JOptionPane.showMessageDialog(WallGeneratorFrame.this, "Configuration file reloaded!", "Configuration File Reloaded!", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(WallGeneratorFrame.this, "Error reloading config.xml file", "Config Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);


        this.controller = new Controller(this, fileChooserPanel, optionPanel, WallGenerator.getConfigManager());

        this.controller.loadDefaultSettingsFile();
    }

}
