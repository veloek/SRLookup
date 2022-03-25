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

package srlookup.cli;

import javax.swing.SwingUtilities;

import srlookup.api.Suggestion;
import srlookup.core.SRDict;
import srlookup.core.Services;
import srlookup.gui.GUI;

/**
 * Program
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class Program {
    public static void main(String[] args) {

        if (args.length == 0) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    new GUI();
                }
            });
        } else {
            String query = args[0];

            Suggestion[] suggestions = Services.getSuggestions(SRDict.Both, query);

            for (Suggestion suggestion : suggestions) {
                System.out.println(suggestion);
            }
        }
    }
}
