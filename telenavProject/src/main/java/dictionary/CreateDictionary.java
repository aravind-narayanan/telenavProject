package dictionary;

//import com.byteowls.jopencage.JOpenCageGeocoder;
//import com.byteowls.jopencage.model.JOpenCageForwardRequest;
//import com.byteowls.jopencage.model.JOpenCageLatLng;
//import com.byteowls.jopencage.model.JOpenCageResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class CreateDictionary {
    //    private static final String dataFile = "local.txt"; // get data file
//    private static final String testFile = "test.txt"; // get test file
//    private static final String fileName = "words.txt"; // file for dictionary
//    private static final String outputFile = "output.txt"; // output result
    private final String key = "b2c332fa93ec4271864316f41f65737e";
    private final String link = "https://maps.googleapis.com/maps/api/geocode/json?";
    private final double latitude = 42.331429; // default detroit location
    private final double longitude = -83.045753; // default detroit location
    private final List<String> prep = new ArrayList<>(
            Arrays.asList("of", "in", "at", "on", "for"));
    private final List<String> addWord = new ArrayList<>(
            Arrays.asList("st", "rd", "dr", "pl", "blvd", "street", "road", "boulevard"));
    private File inputFile;
    private File outputFile;

    public CreateDictionary(File inputFile, File outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public CreateDictionary(String inputFilePath, String outputFilePath) {
        this.inputFile = createFile(inputFilePath);
        this.outputFile = createFile(outputFilePath);
    }

    // create a new file
    private File createFile(String name) {
        try {
            File myObj = new File(name);
            myObj.createNewFile();
            return myObj;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    // input is local.txt, output is word.txt
    public void originateDictionary() throws IOException {
        updateFrequency(inputFile, outputFile, false);
    }

    // input is local.txt, output is word.txt
    public void incrementalUpdateDictionary() throws IOException {
        updateFrequency(inputFile, outputFile, true);
    }

    // this method has 3 steps:
    // 1. get all word from frequency file, split by line, and only keep space and all alphabetic
    // 2. add all word into map with word and their counts
    // 3. update the frequency in our own dictionary
    private void updateFrequency(File dataFile, File outputFile, boolean update) throws IOException {
        Map<String, Integer> wordCount = new HashMap<String, Integer>();
        FileReader reader = new FileReader(dataFile);
        BufferedReader br = new BufferedReader(reader);
        String data;
        // add word count into map
        while ((data = br.readLine()) != null) {
            data = data.trim();
            if (data.length() > 0 && !addressChecker(data, true)) {
                String[] words = data.split("\t"); // index 0 is data, 1 is lat and 2 is log
                String word = words[0].toLowerCase().trim();
                //*** we can add an address classifier here
                word = word.replaceAll("[:!.,]", ""); // remove punctuation
                String[] wordList = word.split(" ");
                if (wordList.length == 1) { //if the query is only one word long, add as unigram
                    String lastWord = wordList[0];
                    lastWord = lastWord.replaceAll(" ", "");
                    if (lastWord.length() > 0) {
                        if (wordCount.containsKey(lastWord)) {
                            wordCount.put(lastWord, (wordCount.get(lastWord)) + 1);
                        } else {
                            wordCount.put(lastWord, 1);
                        }
                    }
                }
                for (int i = 0; i < wordList.length - 1; i++) {
                    String firstWord = wordList[i];
                    String secondWord = wordList[i + 1];
                    firstWord = firstWord.replaceAll(" ", "");
                    secondWord = secondWord.replaceAll(" ", "");
                    if (firstWord.length() > 0 && secondWord.length() > 0) {
                        if (prep.contains(secondWord) && (i != wordList.length - 2)) { //deals with queries that have preposition
                            String thirdWord = wordList[i + 2];
                            thirdWord = thirdWord.replaceAll(" ", "");
                            String combined = firstWord + " " + secondWord + " " + thirdWord;
                            if (wordCount.containsKey(combined)) {
                                wordCount.put(combined, (wordCount.get(combined)) + 1);
                            } else {
                                wordCount.put(combined, 1);
                            }
                            i++;
                        } else { //deals with queries that don't have preposition
                            String combined = firstWord + " " + secondWord;
                            if (wordCount.containsKey(combined)) {
                                wordCount.put(combined, (wordCount.get(combined)) + 1);
                            } else {
                                wordCount.put(combined, 1);
                            }
                        }
                    }
                }
            }
        }
        // here will do the update thing (add the initial file frequency with new one)
        Map<String, Integer> freq = new HashMap<>();
        if (update) {
            freq = getFrequencyFile(outputFile);
            for (String key : freq.keySet()) {
                if (wordCount.containsKey(key)) {
                    wordCount.put(key, wordCount.get(key) + freq.get(key));
                } else {
                    wordCount.put(key, freq.get(key));
                }
            }
        }
        writeIntoTxt(wordCount, freq, outputFile);
    }

    // write the map of word + freq into txt
    private void writeIntoTxt(Map<String, Integer> dictCount, Map<String, Integer> freq, File outputFile) throws IOException {
        // sort the map in alphabetic
        Map<String, Integer> sorted = sortByKey(dictCount);
        //update original dictionary file
        FileWriter myWriter = new FileWriter(outputFile.getPath());
        for (String key : sorted.keySet()) {
            myWriter.write(key);
            if (key.split(" ").length == 1) {
                int count = dictCount.get(key);
                int temp = 0;
                if (freq.containsKey(key)) {
                    // we will not weight 0.5 to freq file word
                    temp = freq.get(key);
                }
                count -= temp;
                count = count / 2 + count % 2;
                count += temp;
                myWriter.write("\n" + count + "\n");
            } else {
                myWriter.write("\n" + dictCount.get(key) + "\n");
            }
        }
        myWriter.close();
    }

    // get word+frequency from dictionary file
    private static Map<String, Integer> getFrequencyFile(File dictFile) throws IOException {
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

    // sort by key
    private Map<String, Integer> sortByKey(Map<String, Integer> map) {
        // TreeMap to store values of HashMap
        Map<String, Integer> sorted = new TreeMap<>();
        // Copy all data from hashMap into TreeMap
        sorted.putAll(map);
        return sorted;
    }

    private void testWholeFile(Map<String, Integer> dictCount, File testFile, File outputFile) throws IOException {
        FileWriter myWriter = new FileWriter(outputFile);
        Scanner myReader = new Scanner(testFile);
        while (myReader.hasNextLine()) {

            String line = myReader.nextLine().toLowerCase();
            if (addressChecker(line, false)) {
//                String[] words2 = line.split("\\|");
//                Map<String, Double> frequencySumMap = new HashMap<>();
//                for (int i = 0; i < words2.length; i++) {
//                    double dist  = testAddress(words2[i].trim(), latitude, longitude);
//                    String output = "distance to " + words2[i].trim() + " is: " + dist + " miles";
//                    frequencySumMap.put(output, dist);
//                }
//                Map<String, Double> frequencySumMapSorted = sortByValue(frequencySumMap);
//
//                for (Map.Entry<String, Double> entry : frequencySumMapSorted.entrySet()) {
//                    myWriter.write(entry.getKey());
//                    //new line
//                    myWriter.write("\n");
//                }
            } else {
                String[] words = line.split("\\|");
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
                for (Map.Entry<String, Integer> entry : frequencySumMapSorted.entrySet()) {
                    //put key and value separated by a colon
                    myWriter.write(entry.getKey() + ": " + entry.getValue());
                    //new line
                    myWriter.write("\n");
                }
            }
            myWriter.write("\n");
        }
        myWriter.close();
    }

    // if local file we use tab, if test we use | split
    // return true if it is address
    private boolean addressChecker(String line, boolean tab) {
        String[] queries;
        if (tab) {
            queries = line.trim().split("\t");
        } else {
            queries = line.trim().split("\\|");
        }
        String firstQuery = queries[0].trim();
        firstQuery = firstQuery.replaceAll("[:!.,]", "");
        String[] firstQueryArray = firstQuery.split(" ");
        if (firstQueryArray.length > 1) {
            int countword = 0;
            while (countword < firstQueryArray.length) {
                String tempWord = firstQueryArray[countword].trim();
                if (countword > 1 && addWord.contains(tempWord)) { // if st shows after second word
                    return true;
                }
                if (tempWord.length() > 2 && tempWord.replaceAll("[0-9]", "").length() != tempWord.length()) {
                    return true;
                }
                countword++;
            }
        }
        return false;
    }
//
//    public static double testAddress(String word2, double latitude, double longitude) {
//        double[] latLong = location(word2);
//        double dist = distance(latitude, longitude, latLong[0], latLong[1]);
//        return dist;
//    }
//
//    public static double[] location(String location) {
//        String changeForm = location.replaceAll(" ", "%20");
//        String str = link + "address=" + changeForm + "&key=" + key;
//        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder(key);
//        JOpenCageForwardRequest request = new JOpenCageForwardRequest(location);
//        request.setRestrictToCountryCode("us"); // restrict results to a specific country
//        JOpenCageResponse response = jOpenCageGeocoder.forward(request);
//        JOpenCageLatLng firstResultLatLng = response.getFirstPosition(); // get the coordinate pair of the first result
//        double[] returnList = new double[]{firstResultLatLng.getLat(), firstResultLatLng.getLng()};
//        return returnList;
//    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            return (dist);
        }
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValueReverse(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}