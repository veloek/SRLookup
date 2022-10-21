/*
 * The MIT License
 *
 * Copyright 2015-2022 Vegard Løkken
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import srlookup.api.APIConstants;
import srlookup.api.Suggestion;
import srlookup.core.SRDict;

/**
 * Main
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class GUI extends JFrame implements SuggestionsReceiver {
    public static final String VERSION = "0.2.3";

    private String lastText;

    private ExtTextField input;
    private JList<Suggestion> suggestionsList;
    private JScrollPane scrollPane;
    private JPanel credits;

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

        suggestionsList = new JList<Suggestion>();
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
                JList<?> list = (JList<?>)evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());

                    if (index >= 0)
                        onListItemSelected(index);
                }
            }
        });
        scrollPane = new JScrollPane(suggestionsList);

        String creditsTxt = "SRLookup v%s Credit: Språkrådet / UiB";
        JLabel creditsLabel = new JLabel(String.format(creditsTxt, VERSION));
        creditsLabel.setFont(new Font(null, Font.PLAIN, 9));

        String creditsLinkTxt = "ordbokene.no";
        String creditsLinkUrl = "https://ordbokene.no/";
        String creditsLinkTooltip = "Visit Ordbøkene.no";
        JLabel creditsLink = new HyperlinkLabel(creditsLinkTxt, creditsLinkUrl, creditsLinkTooltip);
        creditsLink.setFont(new Font(null, Font.ITALIC, 9));

        credits = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        credits.add(creditsLabel);
        credits.add(creditsLink);

        add(input, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void onInputChanged(String newText) {
        if (!newText.equals(lastText)) {
            if (newText.length() >= 2)
                fetchSuggestions(newText);
            else
                setSuggestions(new Suggestion[0]);

            lastText = newText;
        }
    }

    private void onListItemSelected(int index) {

        Suggestion item = (Suggestion)suggestionsList.getModel().getElementAt(index);
        String word = item.getWord();
        SRDict dictionary = item.getDict();

        browseDefinition(word, dictionary);
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void fetchSuggestions(String query) {

        // Invalidate last thread
        if (apiThread != null && !apiThread.isDone())
            apiThread.setInvalid(true);

        apiThread = new SuggestionsFetcher(query, this);
        apiThread.start();
    }

    private void setSuggestions(Suggestion[] suggestions) {
        if (suggestions.length > 0) {
            DefaultListModel<Suggestion> listModel = new DefaultListModel<Suggestion>();

            for (Suggestion suggestion : suggestions) {
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
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Caught exception while encoding query: " +
                    e.getMessage());
            return;
        }

        String url = String.format("%s/%s/%s",
                APIConstants.DEFINITION_URL,
                dict,
                encodedQuery);

          Browser.open(url);
    }

    @Override
    public void receiveSuggestions(Suggestion[] suggestions) {
        setSuggestions(suggestions);
    }

}
