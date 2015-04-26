/*
 * The MIT License
 *
 * Copyright 2015 Vegard Løkken
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package srlookup.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import srlookup.core.Definition;

/**
 * DefinitionBrowser
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class DefinitionBrowser extends JDialog {

    private Definition definition;

    JEditorPane htmlView;
    JButton closeButton;

    public DefinitionBrowser(Frame owner) {
        this(owner, null);
    }

    public DefinitionBrowser(Frame owner, Definition definition) {
        super(owner, "SRLookup", true);

        this.definition = definition;

        setupGUI();
    }

    private void setupGUI() {
        htmlView = new JEditorPane();
        htmlView.setContentType("text/html");
        
        
        closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        add(htmlView, BorderLayout.CENTER);
        add(closeButton, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public void open() {
        if (definition != null) {
            htmlView.setText(definition.getBokmaal());
        
            pack();
            setLocationRelativeTo(null);

            setVisible(true);
        }
    }

    public Definition getDefinition() {
        return definition;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

}
