package de.nimax.nimax_cocktails.recipes.data;


import androidx.annotation.NonNull;

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

public class Bar {

    /**
     *  The path to the data
     */
    public static String path = "";
    /**
     *  The mixes that the bar provides
     */
    public static ArrayList<Mix> mixes = new ArrayList<>();

    /**
     * Set the path, where the mixes are stored at
     * @param Path expects the mix file path
     */
    public static void setupPath(String Path) {
        path = Path;
    }

    /**
     * Adds a new mix
     * @param mix expects the new mix
     * @return returns false if mix is known and true if its a knew mix
     */
    public static boolean addNewMix(Mix mix) {

        // Check if mix is new
        for (int i=0; i<mixes.size(); i++) {
            if (mixes.get(i).name.equals(mix.name)) {
               return false;
            }
        }
        // Only add if new mix
        mixes.add(new Mix(mix));
        // Sort the mixes
        sortMixes();
        return true;
    }

    /**
     * Removes a mix
     * @param which expects the id of the mix to be removed
     */
    public static void removeMix(Mix which) {
        // Remove mix
        mixes.remove(which);
        // Save the mixes
        saveMixes();
    }

    /**
     * Saves the mixes
     */
    public static void saveMixes() {

        // Convert to json object
        JSONArray json = new JSONArray();
        for(int i=0; i<mixes.size(); i++) {
            json.put(mixes.get(i).toJson());
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
     * Load all the mixes that are stored in a json
     * @param is expects an input stream
     */
    public static void loadMixes(InputStream is) {
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
            for(int i=0; i<json.length(); i++){
                addNewMix(new Mix(json.getJSONObject(i)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to sort the mixes
     */
    private static void sortMixes() {
        // Sort the mix list
        Collections.sort(mixes, new Comparator<Mix>() {
            @Override
            public int compare(Mix r1, Mix r2) {
                return r1.name.toUpperCase().compareTo(r2.name.toUpperCase());
            }
        });
    }

    /**
     * For debugging purposes
     */
    @NonNull
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for (Mix mix : mixes) {
            string.append(mix);
        }
        return string.toString();
    }
}
