package test;

//import Telenav.CreateDictionary;
//import com.byteowls.jopencage.JOpenCageGeocoder;
//import com.byteowls.jopencage.model.JOpenCageForwardRequest;
//import com.byteowls.jopencage.model.JOpenCageLatLng;
//import com.byteowls.jopencage.model.JOpenCageResponse;

import dictionary.OrderQueries;

import java.io.IOException;
import java.util.*;

public class TestDictionary {
    public static void main(String[] args) throws IOException {
        OrderQueries wordOrder;
        if (args.length != 0) {
            wordOrder = new OrderQueries(args[0]);
        } else {
            wordOrder = new OrderQueries("word.txt");
        }
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println("Please enter your input Strings split by |: (press enter to quit)");
        String input = sc.nextLine();
        while (input != null && input.length() != 0) {
            String[] test = input.split("\\|");
            System.out.println("the output is: " + wordOrder.sortQueries(test));
            System.out.println("Please enter your input Strings split by |: (press enter to quit)");
            input = sc.nextLine();
        }
    }
}