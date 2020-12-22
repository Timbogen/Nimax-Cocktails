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
     * The path to the data
     */
    public static String path = "";
    /**
     * The recipes that the bar provides
     */
    public static ArrayList<Recipe> recipes = new ArrayList<>();

    /**
     * Set the path, where the recipes are stored at
     *
     * @param Path expects the drinker file path
     */
    public static void setupPath(String Path) {
        path = Path;
    }

    /**
     * Adds a new drinker
     *
     * @param recipe expects the new drinker
     * @return returns false if drinker is known and true if its a knew drinker
     */
    public static boolean addRecipe(Recipe recipe) {
        // Check if drinker is new
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).name.equals(recipe.name)) {
                return false;
            }
        }
        // Only add if new drinker
        recipes.add(recipe);
        // Sort the recipes
        sortRecipes();
        return true;
    }

    /**
     * Removes a drinker
     *
     * @param recipe to be removed
     */
    public static void removeRecipe(Recipe recipe) {
        // Remove drinker
        recipes.remove(recipe);
        // Save the recipes
        saveRecipes();
    }

    /**
     * Saves the recipes
     */
    public static void saveRecipes() {
        // Convert to json object
        JSONArray json = new JSONArray();
        for (int i = 0; i < recipes.size(); i++) {
            json.put(recipes.get(i).toJson());
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
     * Load all the recipes that are stored in a json
     *
     * @param is expects an input stream
     */
    public static void loadRecipes(InputStream is) {
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
                addRecipe(new Recipe(json.getJSONObject(i)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to sort the recipes
     */
    private static void sortRecipes() {
        // Sort the drinker list
        Collections.sort(recipes, new Comparator<Recipe>() {
            @Override
            public int compare(Recipe r1, Recipe r2) {
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
        for (Recipe recipe : recipes) {
            string.append(recipe);
        }
        return string.toString();
    }


}
