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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import srlookup.api.APIConstants;
import srlookup.core.SRDict;

/**
 * Main
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class GUI extends JFrame implements SuggestionsReceiver {
    private static final String VERSION = "0.1.4";

    private String lastText;

    private ExtTextField input;
    private JList suggestionsList;
    private JScrollPane scrollPane;
    private JLabel credits;

    private Fetcher apiThread;

    public GUI() {
        super("SRLookup");
        
        lastText = "";
        apiThread = null;

        // Add custom key event dispatcher to allow us to have esc-to-quit
        KeyboardFocusManager manager =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new QuitOnEscape(this));

        setupGUI();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setupGUI() {
        
        // Frameless with a custom border
        setUndecorated(true);

        input = new ExtTextField();
        input.setPlaceholder("Søk...");
        input.setFont(new Font(null, Font.PLAIN, 32));
        input.setPreferredSize(new Dimension(300, 50));
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = input.getText().toLowerCase();
                onInputChanged(text);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();

                if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_DOWN) {
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
                    
                    if (index >= 0)
                        onListItemSelected(index);
                }
            }
        });
        suggestionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    
                    if (index >= 0)
                        onListItemSelected(index);
                }
            }
        });
        scrollPane = new JScrollPane(suggestionsList);

        String creditsTxt = "SRLookup v%s Copyright © 2015 Vegard Løkken";
        credits = new JLabel(String.format(creditsTxt, VERSION),
                SwingConstants.CENTER);
        credits.setFont(new Font(null, Font.ITALIC, 9));        
        credits.setBorder(input.getBorder());

        add(input, BorderLayout.NORTH);
        
        pack();
        setLocationRelativeTo(null);
    }

    private void onInputChanged(String newText) {
        if (!newText.equals(lastText)) {
            if (newText.length() >= 2)
                fetchSuggestions(newText);
            else
                setSuggestions(new String[0]);

            lastText = newText;
        }
    }

    private void onListItemSelected(int index) {
        
        String item = (String)suggestionsList.getModel().getElementAt(index);

        // Find word part and dictionary part
        String regex = "(.+)\\s\\(([^\\)]+)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(item);

        if (matcher.find()) {
            String word = matcher.group(1);
            String dict = matcher.group(2);

            SRDict dictionary;
            switch (dict) {
                case "bm":
                    dictionary = SRDict.Bokmaal;
                    break;
                case "nn":
                    dictionary = SRDict.Nynorsk;
                    break;
                default:
                    dictionary = SRDict.Both;
            }

            browseDefinition(word, dictionary);
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
        if (suggestions.length > 0) {
            DefaultListModel listModel = new DefaultListModel();

            for (String suggestion : suggestions) {
                listModel.addElement(suggestion);
            }

            suggestionsList.setModel(listModel);
            
            add(scrollPane, BorderLayout.CENTER);
            add(credits, BorderLayout.SOUTH);
            pack();
        } else {
            remove(scrollPane);
            remove(credits);
            pack();
        }
    }

    private void browseDefinition(String query, SRDict dict) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();

            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    String encoded = URLEncoder.encode(query, "UTF-8");

                    URI uri = new URI(APIConstants.DEFINITION_URL +
                            "?OPP=" + encoded + "&" + dict + "=+");

                    desktop.browse(uri);
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void receiveSuggestions(String[] suggestions) {
        setSuggestions(suggestions);
    }

}
