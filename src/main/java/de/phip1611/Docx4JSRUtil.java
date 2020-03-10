package de.phip1611;

import static de.phip1611.StringFindUtil.findAllOccurrencesInString;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.JAXBElement;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to search and replace text in Docx4J-parsed documents.
 *
 * @author Philipp Schuster (phip1611@gmail.com)
 */
public class Docx4JSRUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(Docx4JSRUtil.class);

    /**
     * Searches for all occurrences of the placeholders in the parsed docx-document and replaces all of them
     * inside the {@link WordprocessingMLPackage}-object.
     *
     * @param docxDocument Docx4J-parsed document
     * @param replaceMap Map with all placeholders and their new values.
     */
    // more info: https://stackoverflow.com/questions/19676282/docx4j-find-and-replace/60384502#60384502
    public static void searchAndReplace(WordprocessingMLPackage docxDocument, Map<String, String> replaceMap) {
        // All Text-objects in correct order
        List<Text> texts = getAllElementsOfType(docxDocument.getMainDocumentPart(), Text.class);
        String completeString = getCompleteString(texts);
        if (completeString.isEmpty()) {
            return;
        }

        List<TextMetaItem> metaItemList = buildMetaItemList(texts);

        // with this array we can lookup for each index in completeString what is the corresponding
        // TextMetaItem
        TextMetaItem[] stringIndicesLookupArray = buildIndexToTextMetaItemArray(metaItemList);

        // build a list of all replace commands that is ordered from the last one to the first
        // this is important so that replacements won't invalidate indices of other ReplaceCommands
        List<ReplaceCommand> replaceCommandList = buildAllReplaceCommands(completeString, replaceMap);

        // execute all (in correct order)
        replaceCommandList.forEach(rc -> executeReplaceCommand(texts, rc, stringIndicesLookupArray));
    }

    /**
     * Recursive function returning all Docx4J-objects of a specific type.
     *
     * @param obj Docx4J-parsed document
     * @param clazz Class we want to look for
     * @param <T> Class we want to look for
     * @return List with all objects of the desired type in correct order
     */
    @SuppressWarnings("checkstyle:ParameterAssignment")
    public static <T> List<T> getAllElementsOfType(Object obj, Class<T> clazz) {
        // Code inspired by https://stackoverflow.com/questions/19676282
        // Thanks!
        List<T> result = new ArrayList<>();
        if (obj instanceof JAXBElement) {
            obj = ((JAXBElement<?>) obj).getValue();
        }
        if (obj.getClass().equals(clazz)) {
            result.add((T) obj);
        } else if (obj instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) obj).getContent();
            for (Object child : children) {
                result.addAll(getAllElementsOfType(child, clazz));
            }
        }
        return result;
    }

    /**
     * Returns the full string back. This string can be understood as the full information that is inside a docx-document.
     *
     * @param texts all Text-objects
     * @return Complete string, never null
     */
    public static String getCompleteString(List<Text> texts) {
        return texts.stream()
                .map(Text::getValue)
                .filter(Objects::nonNull) // can be null if we set it to null during replace
                .reduce(String::concat)
                .orElse("");
    }

    /**
     * Builds {@link TextMetaItem}-list for all Text-objects.
     *
     * @param texts all Text-objects
     * @return {@link TextMetaItem}-list for all Text-objects
     */
    public static List<TextMetaItem> buildMetaItemList(List<Text> texts) {
        int index = 0;
        int iteration = 0;
        List<TextMetaItem> list = new ArrayList<>();
        for (Text text : texts) {
            int length = text.getValue().length();
            list.add(new TextMetaItem(index, index + length - 1, text, iteration));
            index += length;
            iteration++;
        }
        return list;
    }

    /**
     * Builds an array with the length of the complete string of TextMetaItem. This way we can lookup for every
     * index in the complete string what's the responsible Text-objects is.
     *
     * @param metaItemList list with all {@link TextMetaItem}s
     * @return array with the length of the complete string of TextMetaItem
     */
    public static TextMetaItem[] buildIndexToTextMetaItemArray(List<TextMetaItem> metaItemList) {
        int currentStringIndicesToTextIndex = 0;
        // + 1, because inside the loop we use "<" instead of "<="; max is inclusive
        int max = metaItemList.get(metaItemList.size() - 1).getEnd() + 1;

        TextMetaItem[] arr = new TextMetaItem[max];

        for (int i = 0; i < max; i++) {
            TextMetaItem currentTextMetaItem = metaItemList.get(currentStringIndicesToTextIndex);
            arr[i] = currentTextMetaItem;
            if (i >= currentTextMetaItem.getEnd()) {
                currentStringIndicesToTextIndex++;
            }
        }
        return arr;
    }

    /**
     * Builds a list of {@link ReplaceCommand} for a single search and replace entry.
     * It's a list because a search-value can have multiple occurrences.
     *
     * @param completeString complete string
     * @param searchAndReplaceEntry Entry with search value and replace value
     */
    private static List<ReplaceCommand> buildReplaceCommandsForOnePlaceholder(String completeString, Map.Entry<String, String> searchAndReplaceEntry) {
        return findAllOccurrencesInString(completeString, searchAndReplaceEntry.getKey()).stream()
                .map(fmi -> new ReplaceCommand(searchAndReplaceEntry.getValue(), fmi))
                .collect(toList());
    }

    /**
     * Builds the list of all {@link ReplaceCommand} for all placeholders and the new values.
     *
     * @param completeString complete string
     * @param replaceMap Map with search and replace values
     * @return all {@link ReplaceCommand} for all placeholders and the new values
     */
    public static List<ReplaceCommand> buildAllReplaceCommands(String completeString, Map<String, String> replaceMap) {
        return replaceMap.entrySet().stream()
                .map(e -> buildReplaceCommandsForOnePlaceholder(completeString, e))
                .flatMap(Collection::stream)
                // important to sort!!!!!
                // we sort from the end to the beginning so that replacements won't invalidate indices of other
                // commands
                .sorted()
                .collect(toList());
    }

    /**
     * Executes a single {@link ReplaceCommand}. It's important that all replace commands are executed in the right order.
     * The right order means from the last to the first one so that indices of other commands won't get invalidated
     * by replacements.
     *
     * @param texts all Text-objects
     * @param replaceCommand {@link ReplaceCommand} to execute
     * @param arr Lookup-Array from index in complete string to TextMetaItem
     */
    public static void executeReplaceCommand(List<Text> texts, ReplaceCommand replaceCommand, TextMetaItem[] arr) {
        TextMetaItem tmi1 = arr[replaceCommand.getFoundResult().getStart()];
        TextMetaItem tmi2 = arr[replaceCommand.getFoundResult().getEnd()];

        if (tmi1.getPosition() == tmi2.getPosition()) {
            // do replacement inside a single Text-object

            String t1 = tmi1.getText().getValue();
            int beginIndex = tmi1.getPositionInsideTextObject(replaceCommand.getFoundResult().getStart());
            int endIndex = tmi2.getPositionInsideTextObject(replaceCommand.getFoundResult().getEnd());

            String keepBefore = t1.substring(0, beginIndex);
            String keepAfter = t1.substring(endIndex + 1);

            // Update Text-object
            tmi1.getText().setValue(keepBefore + replaceCommand.getNewValue() + keepAfter);
        } else {
            // null all Text-objects that may be in between

            if (tmi2.getPosition() - tmi1.getPosition() > 1) {
                int upperBorder = tmi2.getPosition();
                int lowerBorder = tmi1.getPosition() + 1;
                for (int i = lowerBorder; i < upperBorder; i++) {
                    texts.get(i).setValue(null);
                }
            }

            // do replacement across two Text-objects

            String t1 = tmi1.getText().getValue();
            String t2 = tmi2.getText().getValue();

            // indices inside Text-objects (relative to their start)
            int beginIndex = tmi1.getPositionInsideTextObject(replaceCommand.getFoundResult().getStart());
            int endIndex = tmi2.getPositionInsideTextObject(replaceCommand.getFoundResult().getEnd());

            String newValue;
            if (replaceCommand.getNewValue() == null) {
                LOGGER.warn("replaceCommand.getNewValue() is null! Using '' instead!");
                newValue = "";
            } else {
                newValue = replaceCommand.getNewValue();
            }

            t1 = t1.substring(0, beginIndex); // keep this, throw away part of place-holder
            t1 = t1.concat(newValue); // concat new value
            t2 = t2.substring(endIndex + 1); // keep this, throw away part of place-holder

            // Update Text-objects
            tmi1.getText().setValue(t1);
            tmi2.getText().setValue(t2);
        }
    }

    /**
     * Describes information about a single {@link Text}-object inside the List of Text-objects.
     */
    public static class TextMetaItem {

        /**
         * Position/index of the Text-object inside the list of all Text-objects.
         */
        private int position;

        /**
         * Index in complete string where the Text-object starts.
         */
        private int start;

        /**
         * Index in complete string where the Text-object ends.
         */
        private int end;

        /**
         * The Text-object.
         */
        private Text text;

        /**
         * Constructor.
         *
         * @param start Index in complete string where the Text-object starts.
         * @param end Index in complete string where the Text-object ends.
         * @param text Text-object
         * @param position Position/index of the Text-object inside the list of all Text-objects.
         */
        public TextMetaItem(int start, int end, Text text, int position) {
            this.start = start;
            this.end = end;
            this.text = text;
            this.position = position;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public Text getText() {
            return text;
        }

        public int getPosition() {
            return position;
        }

        /**
         * Utility-method to find out what's the relative index inside the Text-object of an
         * index in the complete string.
         *
         * @param completeStringIndex Index inside complete string where this Text-object is a part of
         * @return relative index inside the Text-object of an index in the complete string.
         */
        public int getPositionInsideTextObject(int completeStringIndex) {
            return completeStringIndex - this.start;
        }
    }

    /**
     * Holds all information necessary to do a single replacement.
     */
    public static class ReplaceCommand implements Comparable<ReplaceCommand> {

        /**
         * The new value for the placeholder.
         */
        private String newValue;

        /**
         * Information where the replacement takes place.
         */
        private StringFindUtil.FoundResult foundMetaItem;

        /**
         * Constructor.
         *
         * @param newValue The new value for the placeholder
         * @param foundMetaItem Information where the replacement takes place.
         */
        public ReplaceCommand(String newValue, StringFindUtil.FoundResult foundMetaItem) {
            this.newValue = newValue;
            this.foundMetaItem = foundMetaItem;
        }

        public String getNewValue() {
            return newValue;
        }

        public StringFindUtil.FoundResult getFoundResult() {
            return foundMetaItem;
        }

        /**
         * Compares two replacement commands in a way that they are ordered from the end
         * of the complete string to the beginning. This way replacements can happen without
         * invalidating indices of other commands.
         */
        @Override
        public int compareTo(ReplaceCommand other) {
            // Sortieren von hinten nach vorne
            return other.getFoundResult().getStart() - this.getFoundResult().getStart();
        }
    }

}
