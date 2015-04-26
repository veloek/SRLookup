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

package srlookup.api;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import srlookup.core.SRDict;

/**
 * SRFetcher
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class SRFetcher {

    public static String getSuggestionsJson(SRDict dict, String query) {
        String json = null;

        try {
            String encoded = URLEncoder.encode(query, "UTF-8");
            URL url = new URL(APIConstants.SUGGESTIONS_URL + "?spr=" + dict + "&query=" + query);

            json = getResponse(url, "ISO-8859-1");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return json;
        }
    }

    public static String getDefinitionHtml(String word) {
        String html = null;

        try {
            String encoded = URLEncoder.encode(word, "UTF-8");
            URL url = new URL(APIConstants.DEFINITION_URL + "?OPP=" + encoded);

            html = getResponse(url, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return html;
        }
    }

    private static String getResponse(URL url, String encoding) throws Exception {
        InputStream is = url.openStream();

        Reader reader = new InputStreamReader(is, encoding);
        StringBuilder sb = new StringBuilder();

        int length;
        char[] buffer = new char[4096];
        while ((length = reader.read(buffer, 0, buffer.length)) > 0) {
            sb.append(buffer, 0, length);
        }

        return sb.toString();
    }
}
