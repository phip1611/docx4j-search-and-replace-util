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

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

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