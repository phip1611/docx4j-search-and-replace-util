package de.phip1611;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class StringFindUtilTest {

    @Test
    public void testFindAllOccurrencesInStringSimple() {
        List<StringFindUtil.FoundResult> foo = StringFindUtil.findAllOccurrencesInString("${FOO}", "${FOO}");

        Assert.assertEquals(1, foo.size());
        Assert.assertEquals(0, foo.get(0).getStart());
        Assert.assertEquals(5, foo.get(0).getEnd());
    }

    @Test
    public void testFindAllOccurrencesInStringComplex() {
        String string = "Hallo ${FOO} abc ${FOO} cbca ${BAR} afaf";
        List<StringFindUtil.FoundResult> foo = StringFindUtil.findAllOccurrencesInString(string, "${FOO}");
        List<StringFindUtil.FoundResult> bar = StringFindUtil.findAllOccurrencesInString(string, "${BAR}");

        Assert.assertEquals(2, foo.size());
        Assert.assertEquals(6, foo.get(0).getStart());
        Assert.assertEquals(11, foo.get(0).getEnd());
        Assert.assertEquals(17, foo.get(1).getStart());
        Assert.assertEquals(22, foo.get(1).getEnd());

        Assert.assertEquals(1, bar.size());
        Assert.assertEquals(29, bar.get(0).getStart());
        Assert.assertEquals(34, bar.get(0).getEnd());
    }

}