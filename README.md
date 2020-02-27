# Docx4JSRUtil - Search and replace util for Docx4J

Docx4JS*earchAnd*R*replace*Util helps you to find arbitrary text placeholders
inside a .docx-Document parsed with [Docx4J](https://github.com/plutext/docx4j)
and replaces them.

The challenge is that even if `${NAME}` stands inside a docx-Document when it's edited in
Microsoft Word internally there can be style markup in-between. Therefore `${NAME}` is most 
probably `$` + `{` + `NAME` + `}`. This means we can't just to a simple replace on a `Text`-object
(that's a Docx4J-Type).
My util takes care of that.

##### How it works internally
1. It retrieves the list of all `Text`-objects (in correct order)
2. creates a "complete string" (list reduced to a single string via concatenation)
3. build lookup information to get from index in complete string to corresponding text object
4. do search of place holders in "complete string"
5. build a `List<ReplaceCommand>` that is ordered from the last index in the "complete string" 
   to the first (that's important to not invalidate indices of other `ReplaceCommand`s during replacement!)
6. figure out on which Text-objects changes has to be done and do the actual replacement   

Place holders can be any string pattern, it doesn't have to be `${}`.

**PS:** This is not yet in maven central. (TODO)

##### Usage:
           
    WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(new File("document.docx")));;
    
    searchAndReplace(template, Map.of(
            "${NAME}", "Philipp",
            "${SURNAME}", "Schuster",
            "${PLACE_OF_BIRTH}", "GERMANY"
    ));
    // that's it; you can now save `template`, export it as PDF or whatever you want to do
