/*
 * MIT License
 *
 * Copyright (c) 2020 Philipp Schuster (phip1611@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.phip1611;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Processes a user specified docx file and writes the
 * transformed document into the specified location.
 *
 * This IS NOT a test JUnit should execute but a test for
 * humans (to execute from your IDE). This way you can
 * easily specify a path and check the processed .docx.
 */
public class ProcessDocxUserFileTest {

    private static final String SOURCE_DOCX_PATH = "src/test/resources/source.docx";
    //private static final String DEST_DOCX_PATH = "/Users/phip1611/Desktop/test-processed.docx";
    private static final String DEST_DOCX_PATH = "C:/Users/plsh/Desktop/test-processed.docx";

    @Test
    // Unignore to execute and test this;
    // but don't check this into git without ignore!
    @Ignore
    public void processDocxTest() {
        Map<String, String> placeholderMap = new HashMap<>();
        // Line breaks works as well
        placeholderMap.put("${NAME}", "Phi\nlipp");
        placeholderMap.put("${SURNAME}", "Schuster");
        placeholderMap.put("${PLACE_OF_BIRTH}", "GERMANY");

        try {
            // this max take 4 seconds but this happens only once (internal heatup of data structures)
            // https://stackoverflow.com/questions/18975049/how-to-decrease-docx4j-load-time
            WordprocessingMLPackage sourceDocxDoc = WordprocessingMLPackage.load(new File(SOURCE_DOCX_PATH));

            Docx4JSRUtil.searchAndReplace(sourceDocxDoc, placeholderMap);

            sourceDocxDoc.save(new File(DEST_DOCX_PATH));
        } catch (Docx4JException e) {
            e.printStackTrace();
            Assert.fail("Exception occurred!");
        }
    }


}