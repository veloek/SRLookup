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
import java.awt.Desktop;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.net.URLEncoder;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import srlookup.api.APIConstants;

/**
 * Main
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class GUI extends JFrame implements
        SuggestionsReceiver/*, DefinitionReceiver*/ {
    private static final String VERSION = "0.1.0";

    private String lastText;

    private JTextField input;
    private JList suggestionsList;
    private final DefinitionBrowser definitionBrowser;

    private Fetcher apiThread;

    public GUI() {
        lastText = "";
        apiThread = null;

        // Add custom key event dispatcher to allow us to have esc-to-quit
        KeyboardFocusManager manager =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new QuitOnEscape(this));

        definitionBrowser = new DefinitionBrowser(this);

        setupGUI();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setupGUI() {
        setUndecorated(true);

        input = new JTextField();
        input.setEditable(true);
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = input.getText();
                onInputChanged(text);
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    if (suggestionsList.getModel().getSize() > 0) {
                        suggestionsList.requestFocus();
                        suggestionsList.setSelectedIndex(0);
                    }
                }
            }
        });

        suggestionsList = new JList();
        suggestionsList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    int index = suggestionsList.getSelectedIndex();
                    if (index >= 0) {
                        String query =
                                (String) suggestionsList.getModel().getElementAt(index);

                        query = query.replaceAll("\\s\\([^\\)]+\\)", "");
                        
                        //fetchDefinition(query);
                        browseDefinition(query);
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(suggestionsList);

        String creditsTxt = "SRLookup v%s Copyright © 2015 Vegard Løkken";
        JLabel credits = new JLabel(String.format(creditsTxt, VERSION), SwingConstants.CENTER);
        Font origFont = credits.getFont();
        credits.setFont(new Font(origFont.getName(), Font.ITALIC, 10));

        add(input, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(credits, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }

    private void onInputChanged(String newText) {
        if (!newText.equals(lastText)) {
            if (newText.length() > 2)
                fetchSuggestions(newText);
            else
                setSuggestions(new String[0]);

            lastText = newText;
        }
    }

    private void fetchSuggestions(String query) {
        
        // Invalidate last thread
        if (apiThread != null && !apiThread.isDone())
            apiThread.setInvalid(true);

        apiThread = new SuggestionsFetcher(query, this);
        apiThread.start();
    }

    private void setSuggestions(String[] suggestions) {
        DefaultListModel listModel = new DefaultListModel();

        for (String suggestion : suggestions) {
            listModel.addElement(suggestion);
        }

        suggestionsList.setModel(listModel);
    }
/*
    private void fetchDefinition(String query) {

        // Invalidate last thread
        if (apiThread != null && !apiThread.isDone())
            apiThread.setInvalid(true);

        apiThread = new DefinitionFetcher(query, this);
        apiThread.start();
    }
*/
    private void browseDefinition(String query) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();

            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    String encoded = URLEncoder.encode(query, "UTF-8");
                    URI uri = new URI(APIConstants.DEFINITION_URL + "?OPP=" + encoded);
                    desktop.browse(uri);
                    closeApplication();
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void closeApplication() {
        this.dispatchEvent(new WindowEvent(this,
                    WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public void receiveSuggestions(String[] suggestions) {
        setSuggestions(suggestions);
    }
/*
    @Override
    public void receiveDefinition(Definition definition) {
        definitionBrowser.setDefinition(definition);
        definitionBrowser.open();
    }
*/
}
