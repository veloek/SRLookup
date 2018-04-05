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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
            URL url = new URL(APIConstants.SUGGESTIONS_URL + "?spr=" + dict + "&query=" + encoded);

            json = getResponse(url, "ISO-8859-1");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return json;
        }
    }

    private static String getResponse(URL url, String encoding) throws Exception {
        HttpURLConnection conn = openConnection(url, true);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), encoding));

        String inputLine;
        StringBuilder sb = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        in.close();

        return sb.toString();
    }

    private static HttpURLConnection openConnection(URL url, boolean followRedirect) throws Exception {
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setReadTimeout(5000);
        conn.addRequestProperty("Accept", "application/json");
        conn.addRequestProperty("User-Agent", "SRLookup v" + srlookup.gui.GUI.VERSION);

        int status = conn.getResponseCode();
        switch (status) {
            case HttpURLConnection.HTTP_OK:
                return conn;
            case HttpURLConnection.HTTP_MOVED_TEMP:
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_SEE_OTHER:
                System.out.println("Following: " + conn.getHeaderField("Location"));
                return followRedirect ? openConnection(new URL(conn.getHeaderField("Location")), followRedirect) : conn;
            default:
                throw new Exception(String.format("Error while fetching %s: %d %s", url.toString(), status, conn.getResponseMessage()));
        }
    }
}
