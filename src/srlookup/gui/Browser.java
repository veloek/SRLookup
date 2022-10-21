/*
 * The MIT License
 *
 * Copyright 2017 Vegard Løkken
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

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

/**
 * Browser
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
class Browser {
    private static final List<String> LINUX_BROWSERS = Arrays.asList("xdg-open",
            // Fallbacks
            "firefox", "mozilla", "netscape", "chrome", "chromium",
            "opera", "epiphany", "konqueror", "links", "lynx");

    private enum OS {
        WINDOWS,
        MAC,
        LINUX
    }

    private static OS currentOS() throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0) {
            return OS.WINDOWS;
        } else if (os.indexOf("mac") >= 0) {
            return OS.MAC;
        } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            return OS.LINUX;
        } else {
            throw new Exception("Unknown OS");
        }
    }

    public static void open(String url) {
        OS os;
        try {
            os = currentOS();
        } catch (Exception e) {
            System.err.println("Error while opening URL: " + e.getMessage());
            return;
        }

        Runtime rt = Runtime.getRuntime();

        try {
            switch (os) {
                case WINDOWS:
                    rt.exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", url });
                    break;
                case MAC:
                    rt.exec(new String[] { "open", url });
                    break;
                case LINUX:
                    String separator = String.format(" \"%s\" || ", url);
                    String browsers = String.join(separator, LINUX_BROWSERS);
                    String cmd = String.format("%s \"%s\"", browsers, url);
                    rt.exec(new String[] { "sh", "-c", cmd });
                    break;
            }
        } catch (IOException e) {
            System.err.println("Caught exception while trying to browse url: " +
                    e.getMessage());
        }
    }
}
