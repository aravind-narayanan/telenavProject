package dictionary;

import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;

public class OrderQueries implements QuerySorter {
    private static final List<String> prep = new ArrayList<>(
            Arrays.asList("of", "in", "at", "on", "for"));
    private static final List<String> addWord = new ArrayList<>(
            Arrays.asList("st", "rd", "dr", "pl", "blvd", "street", "road", "boulevard"));
    private Map<String, Integer> map;

    public OrderQueries() throws FileNotFoundException {
        //this(new File("java/Telenav/words.txt"))
    }

    public OrderQueries(String path) throws FileNotFoundException {
        this(new File(path));
    }

    public OrderQueries(File dictionaryFile) throws FileNotFoundException {
        this.map = getFrequencyFile(dictionaryFile);
    }

    private Map<String, Integer> getFrequencyFile(File dictFile) throws FileNotFoundException {
        Map<String, Integer> wordCount = new HashMap<>();
        Scanner myReader = new Scanner(dictFile);
        while (myReader.hasNextLine()) {
            String word = myReader.nextLine();
            if (word.trim().length() != 0) {
                int count = 0;
                if (myReader.hasNextLine()) {
                    count = Integer.parseInt(myReader.nextLine());
                }
                wordCount.put(word, count);
            }
        }
        return wordCount;
    }

    public String sortQueries(String[] words) {
        Map<String, Integer> dictCount = map;
        Map<String, Integer> frequencySumMap = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            word = word.toLowerCase().trim();
            String[] allWord = word.split(" ");
            int frequencySum = 0;
            if (allWord.length == 1) {
                String lookup = allWord[0];
                if (dictCount.containsKey(lookup)) { // if the query is only one word, look up the unigram
                    frequencySum += dictCount.get(lookup);
                } else {
                    dictCount.put(lookup, 0);
                }
            } else {
                for (int j = 0; j < allWord.length - 1; j++) {
                    String lookup;
                    if (prep.contains(allWord[j + 1]) && j != allWord.length - 2) { // if the query contains preposition, look up the trigram
                        lookup = allWord[j] + " " + allWord[j + 1] + " " + allWord[j + 2];
                        j++;
                    } else { //if the query doesn't contain preposition, look up the bigram
                        lookup = allWord[j] + " " + allWord[j + 1];
                    }
                    if (dictCount.containsKey(lookup)) {
                        frequencySum += dictCount.get(lookup);
                    }
                }
            }
            frequencySumMap.put(word, frequencySum);
        }
        Map<String, Integer> frequencySumMapSorted = sortByValueReverse(frequencySumMap);
        int index = 0;
        String outputWord[] = new String[words.length];
        for (String word: frequencySumMapSorted.keySet()) {
            outputWord[index] = word;
            index++;
        }
        return printOutput(outputWord);
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValueReverse(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private String printOutput(String[] outputWord) {
        String returnString = "";
        for (int i = 0; i < outputWord.length - 1; i++) {
            returnString += (outputWord[i] + " | ");
        }
        returnString += outputWord[outputWord.length - 1];
        return returnString;
    }
}

