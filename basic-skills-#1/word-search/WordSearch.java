import java.util.*;
import java.io.*;

public class WordSearch {
    private static final String CONSTITUTION_PATH = "./resources/constitution.txt";
    private static final String DECLARATION_PATH = "./resources/declaration_of_independence.txt";
    private static final String MAGNA_CARTA_PATH = "./resources/magna_carta.txt";


    public static void main(String[] args) throws Exception {


        String wordToSearch = args[0];
        checkForExcludedWords(wordToSearch.toLowerCase());
        int word_count_constitution = getWordCount(wordToSearch, CONSTITUTION_PATH);
        int word_count_declaration = getWordCount(wordToSearch, DECLARATION_PATH);
        int word_count_magna_carta = getWordCount(wordToSearch, MAGNA_CARTA_PATH);

        // Print word count for document in which word occurs the most
        // if word occurs the most in two or more documents I will also print that

        int max = Math.max(word_count_constitution, Math.max(word_count_declaration, word_count_magna_carta));

        if(word_count_constitution == max){
            System.out.println("The word " + wordToSearch + " occurs " + word_count_constitution + " times in the Constitution");
        }
        if(word_count_declaration == max){
            System.out.println("The word " + wordToSearch + " occurs " + word_count_constitution + " times in the Declaration of Independence");
        }
        if(word_count_magna_carta == max){
            System.out.println("The word " + wordToSearch + " occurs " + word_count_constitution + " times in the Magna Carta");
        }
    }

    private static void checkForExcludedWords(String wordToSearch) throws Exception {
        final List<String> excludedWords = new ArrayList<>(Arrays.asList("of","the","to","and","for"));

        if(excludedWords.contains(wordToSearch)){
            throw new Exception("Word to search must not be the following: " + excludedWords);
        }
    }


    private static int getWordCount(String wordToSearch, String documentToSearch) {
        StringBuilder content = new StringBuilder();
        int numWord = 0;
        try{
            // Read Constitution Text
            BufferedReader reader = new BufferedReader(new FileReader(documentToSearch));
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.toLowerCase().contains(wordToSearch.toLowerCase())) numWord++;
            }

            reader.close();
            return numWord;

        }catch (IOException e){
            e.printStackTrace();
        }
        return numWord;
    }

}
