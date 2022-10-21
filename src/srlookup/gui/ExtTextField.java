/*
 * The MIT License
 *
 * Copyright 2015 Vegard Løkken.
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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;

/**
 * ExtTextField
 *
 * Extended JTextField
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class ExtTextField extends JTextField implements KeyListener, CaretListener {

    private String placeholder;
    private Color selectionColor;
    private Color textColor;
    private boolean isShowingPlaceholder;

    public ExtTextField() {
        setupTextField();
    }

    public ExtTextField(String text) {
        super(text);

        setupTextField();
    }

    public ExtTextField(int columns) {
        super(columns);

        setupTextField();
    }

    public ExtTextField(String text, int columns) {
        super(text, columns);

        setupTextField();
    }

    public ExtTextField(Document doc, String text, int columns) {
        super(doc, text, columns);

        setupTextField();
    }

    private void setupTextField() {
        placeholder = null;

        addKeyListener(this);
        addCaretListener(this);
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;

        if (getText().isEmpty())
            showPlaceholder();
    }

    private void showPlaceholder() {
        if (isShowingPlaceholder)
            return;

        selectionColor = getSelectionColor();
        textColor = getForeground();
        isShowingPlaceholder = true;

        setText(getPlaceholder());
        setCaretPosition(0);
        setSelectionColor(new Color(1, 1, 1, 0));
        setForeground(Color.GRAY);
    }

    private void hidePlaceholder() {
        setText("");
        setSelectionColor(selectionColor);
        setForeground(textColor);
        isShowingPlaceholder = false;
    }

    @Override
    public String getText() {
        if (isShowingPlaceholder)
            return "";

        return super.getText();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        boolean isBackspace = (int)e.getKeyChar() == 8;

        if (isShowingPlaceholder && !e.isActionKey() && !isBackspace)
            hidePlaceholder();
        else if (getText().length() == 0)
            showPlaceholder();
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void caretUpdate(CaretEvent e) {

        // Force caret to position 0 if placeholder
        if (isShowingPlaceholder && e.getDot() > 0)
            setCaretPosition(0);
    }

}
