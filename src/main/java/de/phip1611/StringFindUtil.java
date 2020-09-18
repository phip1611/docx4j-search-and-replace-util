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

import java.util.ArrayList;
import java.util.List;

/**
 * Utility-class to find strings in strings.
 *
 * @author Philipp Schuster (phip1611@gmail.com)
 */
public class StringFindUtil {

    /**
     * Finds all occurrences of a string inside a string.
     * Returns a list that contains all (startIndex, endIndex)-pairs.
     *
     * @param data Source string
     * @param search Search string
     * @return List with all occurrences.
     */
    public static List<FoundResult> findAllOccurrencesInString(String data, String search) {
        List<FoundResult> list = new ArrayList<>();
        String remaining = data;
        int totalIndex = 0;
        while (true) {
            int index = remaining.indexOf(search);
            if (index == -1) {
                break;
            }

            int throwAwayCharCount = index + search.length();
            remaining = remaining.substring(throwAwayCharCount);

            list.add(new FoundResult(totalIndex + index, search));

            totalIndex += throwAwayCharCount;
        }
        return list;
    }

    /**
     * This class is a pair of beginIndex and endIndex of
     * string found inside another string.
     */
    public static class FoundResult {

        /**
         * Index inside source string where occurrence starts.
         */
        private final int start;

        /**
         * Index inside source string where occurrence ends.
         */
        private final int end;

        /**
         * Constructor.
         * .
         * @param start Index inside source string where occurrence starts.
         * @param searchString Source string
         */
        public FoundResult(int start, String searchString) {
            this.start = start;
            this.end = start + searchString.length() - 1;
        }

        /**
         * Returns {@link #start}.
         *
         * @return {@link #start}.
         */
        public int getStart() {
            return start;
        }

        /**
         * Returns {@link #end}.
         *
         * @return {@link #end}.
         */
        public int getEnd() {
            return end;
        }

    }

}
