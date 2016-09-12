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
package org.gedcom4j.validate;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.StringWithCustomTags;
import org.gedcom4j.model.Submission;
import org.gedcom4j.model.Submitter;
import org.gedcom4j.model.Trailer;
import org.gedcom4j.parser.GedcomParser;
import org.gedcom4j.validate.Validator.Finding;
import org.junit.Test;

/**
 * Test for {@link GedcomValidator}
 * 
 * @author frizbog1
 */
public class ValidatorGedcomStructureTest extends AbstractValidatorTestCase {

    /**
     * The name of the file used for stress-testing the parser
     */
    private static final String SAMPLE_STRESS_TEST_FILENAME = "sample/TGC551.ged";

    /**
     * Test autorepairing - shouldn't need to do anything on a new Gedcom
     */
    @Test
    public void testEmptyGedcom() {
        Gedcom g = new Gedcom();

        // Go validate
        Validator v = new Validator(g);
        v.validate();
        assertTrue("There should not be findings on a new, empty Gedcom structure", v.getResults().getAllFindings().isEmpty());
    }

    /**
     * Test for character set in header
     */
    @Test
    public void testTrailer() {
        Submitter s = new Submitter();
        s.setXref("@SUBM0001@");
        s.setName(new StringWithCustomTags("test"));
        gedcom.getSubmitters().put(s.getXref(), s);
        gedcom.setSubmission(new Submission("@SUBN0001@"));
        gedcom.getHeader().setSubmitter(s);

        gedcom.setTrailer(null);
        validator.validate();
        assertFindingsContain(Severity.ERROR, gedcom, ProblemCode.MISSING_REQUIRED_VALUE.getCode(), "trailer");

        gedcom.setTrailer(new Trailer());
        validator.validate();
        assertNoIssues();
    }

    /**
     * Validate the stress test file
     * 
     * @throws IOException
     *             if the file can't be read
     * @throws GedcomParserException
     *             if the file can't be parsed
     */
    @Test
    public void testValidateStressTestFile() throws IOException, GedcomParserException {
        // Load a file
        GedcomParser p = new GedcomParser();
        p.load(SAMPLE_STRESS_TEST_FILENAME);
        assertTrue(p.getErrors().isEmpty());
        validator = new Validator(p.getGedcom());
        validator.validate();
        for (Finding f : validator.getResults().getAllFindings()) {
            System.out.println("assertFindingsContain(Severity." + f.getSeverity() + ", " + f.getItemOfConcern() + ", " + f
                    .getProblemCode() + ", " + f.getFieldNameOfConcern() + ");");
        }
    }

}
