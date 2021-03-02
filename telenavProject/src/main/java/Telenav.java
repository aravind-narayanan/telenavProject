import com.byteowls.jopencage.JOpenCageGeocoder;
import com.byteowls.jopencage.model.JOpenCageForwardRequest;
import com.byteowls.jopencage.model.JOpenCageLatLng;
import com.byteowls.jopencage.model.JOpenCageResponse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Telenav {
    private static final String URL = "file:///usr/share/dict/words";
    private static final String fileName = "words.txt";
    private static final String outputFile = "output.txt";
    private static final String key = "b2c332fa93ec4271864316f41f65737e";
    private static final String link = "https://maps.googleapis.com/maps/api/geocode/json?";
    public static void main(String[] args) throws IOException {
        //createFile();
        //initialFile();
        //updateFrequency();
        location("Seattle");
    }

    public static void createFile() {
        try {
            File myObj = new File(fileName);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch(Exception e) {
            System.out.println(e);
        }

    }

    public static void initialFile() throws IOException{
        try{
            URL url = new URL(URL);
            FileWriter myWriter = new FileWriter(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String value = reader.readLine();
            while (value != null) {
                myWriter.write(value + "\n");
                myWriter.write(0 + "\n"); // initial all frequency to 0
                value = reader.readLine();
            }
            reader.close();
            myWriter.close();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static void updateFrequency() throws IOException {
        HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

        File myObj = new File ("localsearch_query.tsv");
        FileReader reader = new FileReader(myObj);
        BufferedReader br = new BufferedReader(reader);

        String lines;

        int count = 1;

        while ((lines = br.readLine()) != null) {

            String[] words = lines.split("\\s+");

            int freq = 0;

            // for loop goes through every word
            for (int i = 0; i < words.length; i++) {
                // Case if the HashMap already contains the key.
                // If so, just increments the value

                String wordNew = words[i].replaceAll(
                        "[^a-zA-Z]", "");

                if ((!wordNew.equals("")) && wordCount.containsKey(words[i])) {
                    wordCount.put(wordNew, freq++);
                }
                // Otherwise, puts the word into the HashMap
                else if (!wordNew.equals("")){
                    wordCount.put(wordNew, 1);
                }
            }

            count++;
        }
        //System.out.println(wordCount);

        File myObj2 = new File ("input.txt");
        FileReader reader2 = new FileReader(myObj2);
        BufferedReader br2 = new BufferedReader(reader2);
        //String outputFilePath = "C:\\Users\\aravind\\Documents\\UW_Junior_Year\\Winter_Quarter\\EE_497\\ordered_queries.txt";

        //new file object
        File writeFile = new File(outputFile);

        //create new BufferedWriter for the output file
        BufferedWriter bw = new BufferedWriter( new FileWriter(writeFile) );
        String lines2;

        while ((lines2 = br2.readLine()) != null) {

            HashMap<String, Integer> wordOrder = new HashMap<String, Integer>();

            String[] words2 = lines2.split("\\|");

//			System.out.println(Arrays.toString(words2));

            for (int i = 0; i < words2.length; i++) {

                String word2New = words2[i].trim();

                String[] words3 = word2New.split(" ");

                int count2 = 0;

                for (int j = 0; j < words3.length; j++) {

                    if (wordCount.containsKey(words3[j])) {

                        count2 += wordCount.get(words3[j]);
                    }

                }

                wordOrder.put(word2New, count2);


//				System.out.println(word2New);

            }

            Map<String, Integer> wordOrderSorted = sortByValue(wordOrder);

            //iterate map entries
            for(Map.Entry<String, Integer> entry : wordOrderSorted.entrySet()){

                //put key and value separated by a colon
                bw.write( entry.getKey() + ":" + entry.getValue() );

                //new line
                bw.newLine();
            }

            bw.newLine();



        }

        bw.flush();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static void location(String location) {
        String changeForm = location.replaceAll(" ", "%20");
        String str = link + "address=" + changeForm + "&key=" + key;
        System.out.println(str);

        JOpenCageGeocoder jOpenCageGeocoder = new JOpenCageGeocoder(key);
        JOpenCageForwardRequest request = new JOpenCageForwardRequest(location);
        request.setRestrictToCountryCode("us"); // restrict results to a specific country
       // request.setBounds(18.367, -34.109, 18.770, -33.704); // restrict results to a geographic bounding box (southWestLng, southWestLat, northEastLng, northEastLat)

        JOpenCageResponse response = jOpenCageGeocoder.forward(request);
        JOpenCageLatLng firstResultLatLng = response.getFirstPosition(); // get the coordinate pair of the first result
        System.out.println("lat:" + firstResultLatLng.getLat());
        System.out.println("long:" + firstResultLatLng.getLng());
    }
}
