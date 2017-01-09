/*
 * Copyright (c) 2009-2016 Matthew R. Harrah
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.gedcom4j.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.exception.ParserCancelledException;
import org.gedcom4j.model.InMemoryGedcom;
import org.gedcom4j.parser.event.ParseProgressEvent;
import org.gedcom4j.parser.event.ParseProgressListener;
import org.junit.Test;

/**
 * Test getting progress from the parser and being able to cancel
 * 
 * @author frizbog
 */
public class ParserProgressAndCancellationTest implements ParseProgressListener {

    /**
     * Number of notifications received
     */
    int notificationCount = 0;

    /**
     * The parser being tested
     */
    private GedcomParser gp = new GedcomParser(new InMemoryGedcom());

    /**
     * How many notifications to cancel after
     */
    private int cancelAfter = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public void progressNotification(ParseProgressEvent e) {
        notificationCount++;
        if (notificationCount >= cancelAfter) {
            gp.cancel();
        }
    }

    /**
     * Test getting notifications and cancelling the parsing of an ascii file
     * 
     * @throws IOException
     *             if the data cannot be written
     * @throws GedcomParserException
     *             if the data cannot be parsed
     */
    @Test(expected = ParserCancelledException.class)
    public void testCancellation() throws IOException, GedcomParserException {
        gp = new GedcomParser(new InMemoryGedcom());
        cancelAfter = 5;
        gp.registerParseObserver(this);
        gp.load("sample/willis-ascii.ged");
    }

    /**
     * Test getting notifications and cancelling the parsing of an ascii file
     * 
     * @throws IOException
     *             if the data cannot be written
     * @throws GedcomParserException
     *             if the data cannot be parsed
     */
    @Test
    public void testNoCancellation() throws IOException, GedcomParserException {
        gp = new GedcomParser(new InMemoryGedcom());
        cancelAfter = Integer.MAX_VALUE;
        gp.registerParseObserver(this);
        gp.load("sample/willis-ascii.ged");
        assertEquals(40, notificationCount);
    }

}
