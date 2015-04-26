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

package srlookup.core;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Parser
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class Parser {
    public static String[] parseSuggestionsJson(String suggestionsJson) {
        JSONObject json = new JSONObject(suggestionsJson);
        JSONArray arr = json.getJSONArray("suggestions");

        String[] suggestions = new String[arr.length()];
        for (int i=0; i<arr.length(); i++) {
            suggestions[i] = arr.getString(i);
        }

        return suggestions;
    }

    public static Definition parseDefinitionHtml(String definitionHtml) {
        Document doc = Jsoup.parse(definitionHtml);

        doc.select(".oppsgramordklasse").remove();
        doc.select(".oppsgramordklassevindu").remove();

        Elements bokmaalSection = doc.select("#kolonnebm"),
                nynorskSection = doc.select("#kolonnenn");

        Elements bokmaalArticles = bokmaalSection.select(".artikkel"),
                nynorskArticles = nynorskSection.select(".artikkel");

        StringBuilder bokmaal = new StringBuilder(),
                nynorsk = new StringBuilder();

        for (Element el : bokmaalArticles) {
            bokmaal.append(el.select(".artikkelinnhold").toString());
        }

        for (Element el : nynorskArticles) {
            nynorsk.append(el.select(".artikkelinnhold").toString());
        }

        return new Definition(bokmaal.toString(), nynorsk.toString());
    }
}
