package de.nimax.nimax_cocktails.drinkers.data;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Administration {

    /**
     * The path to the data
     */
    public static String path = "";
    /**
     * All the registered drinkers
     */
    public static ArrayList<Drinker> drinkers = new ArrayList<>();

    /**
     * Set the path, where the drinkers are stored at
     *
     * @param Path expects the drinkers file path
     */
    public static void setupPath(String Path) {
        path = Path;
    }

    /**
     * Adds a new drinker
     *
     * @param drinker expects the new drinker
     * @return returns false if drinker is known
     */
    public static boolean addDrinker(Drinker drinker) {
        // Check if drinker is new
        for (int i = 0; i < drinkers.size(); i++) {
            if (drinkers.get(i).name.equals(drinker.name)) {
                return false;
            }
        }
        // Only add if new drinker
        drinkers.add(drinker);
        // Sort the recipes
        sortDrinkers();
        return true;
    }

    /**
     * Removes a drinker
     *
     * @param drinker to be removed
     */
    public static void removeDrinker(Drinker drinker) {
        // Remove drinker
        drinkers.remove(drinker);
        // Save the recipes
        saveDrinkers();
    }

    /**
     * Remove all the drinkers
     */
    public static void removeAllDrinkers() {
        drinkers = new ArrayList<>();
        saveDrinkers();
    }

    /**
     * Saves the drinkers
     */
    public static void saveDrinkers() {
        // Convert to json object
        JSONArray json = new JSONArray();
        for (int i = 0; i < drinkers.size(); i++) {
            json.put(drinkers.get(i).toJson());
        }

        // Write json object into target file
        try {
            // Create output stream
            File file = new File(path);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            // Write json object into the file
            bw.write(json.toString());
            // Close the stream
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load all the drinkers that are stored in a json
     *
     * @param is expects an input stream
     */
    public static void loadDrinkers(InputStream is) {
        try {
            JSONArray json;
            int size = is.available();
            byte[] buffer = new byte[size];
            // Read in the file
            is.read(buffer);
            is.close();
            // Convert the read in bytecode to json string
            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            // Convert the string to an object
            json = new JSONArray(jsonString);
            // Get the json objects
            for (int i = 0; i < json.length(); i++) {
                addDrinker(new Drinker(json.getJSONObject(i)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to sort the drinkers
     */
    private static void sortDrinkers() {
        // Sort the drinker list
        Collections.sort(drinkers, new Comparator<Drinker>() {
            @Override
            public int compare(Drinker r1, Drinker r2) {
                return r1.name.toUpperCase().compareTo(r2.name.toUpperCase());
            }
        });
    }
}
