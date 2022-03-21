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

package srlookup.core;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Parser
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class Parser {
    public static String[] parseSuggestionsJson(String suggestionsJson) {
        JSONObject json = new JSONObject(suggestionsJson);
        JSONObject answer = json.getJSONObject("a");
        JSONArray exact = answer.optJSONArray("exact");

        if (exact == null) {
            return new Suggestion[0];
        }

        String[] suggestions = new String[exact.length()];
        for (int i=0; i<exact.length(); i++) {
            JSONArray e = exact.getJSONArray(i);

            String word = e.getString(0);

            JSONArray d = e.getJSONArray(1);
            String[] dict = new String[d.length()];

            for (int j=0; j<d.length(); j++) {
                dict[j] = d.getString(j);
            }

            String suggestion = word + " (" + String.join(", ", dict) + ")";
            suggestions[i] = suggestion;
        }

        return suggestions;
    }
}
