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
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.event.MouseInputListener;

/**
 * HyperlinkLabel
 *
 * Extended JLabel
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class HyperlinkLabel extends JLabel implements MouseInputListener {
    private static String HTML_FORMAT = "<html><a href=''>%s</a></html>";

    private String text;
    private String url;

    public HyperlinkLabel(String url) {
        this(null, url, null);
    }

    public HyperlinkLabel(String text, String url) {
        this(text, url, null);
    }

    public HyperlinkLabel(String text, String url, String tooltip) {
        super(text);
        this.text = text;
        this.url = url;

        setForeground(Color.BLUE.darker());
        setToolTipText(tooltip);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(this);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Browser.open(url);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setText(String.format(HTML_FORMAT, text));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setText(text);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

}
