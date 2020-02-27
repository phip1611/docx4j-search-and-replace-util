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
        private int start;

        /**
         * Index inside source string where occurrence ends.
         */
        private int end;

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
