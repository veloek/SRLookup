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

import srlookup.core.Definition;
import srlookup.core.Services;

/**
 * DefinitionFetcher
 *
 * @author Vegard Løkken <vegard@loekken.org>
 */
public class DefinitionFetcher extends Fetcher {
    private DefinitionReceiver receiver;

    public DefinitionFetcher(String query, DefinitionReceiver receiver) {
        super(query);
        this.receiver = receiver;
    }

    public DefinitionReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(DefinitionReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void run() {
        Definition definition = Services.getDefinition(getQuery());
        System.out.println("bm: " + definition.getBokmaal());
        System.out.println("nn: " + definition.getNynorsk());
        if (!isInvalid())
            receiver.receiveDefinition(definition);

        setDone(true);
    }
}
