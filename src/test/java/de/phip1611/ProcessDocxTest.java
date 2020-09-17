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
 * This is not a test JUnit should execute but a test for
 * humans (to execute from your IDE). This way you can
 * easily specify a path and check the processed .docx.
 */
public class ProcessDocxTest {

    private static final String SOURCE_DOCX_PATH = "src/test/resources/source.docx";
    private static final String DEST_DOCX_PATH = "/Users/phip1611/Desktop/test-processed.docx";

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
            Assert.fail("Exception occurred!");
        }
    }


}