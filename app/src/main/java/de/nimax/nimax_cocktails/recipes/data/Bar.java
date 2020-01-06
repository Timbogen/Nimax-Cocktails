package de.nimax.nimax_cocktails.recipes.data;


import androidx.annotation.NonNull;

import com.nimax.nimax_cocktails.R;

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
     * @param Path expects the recipe file path
     */
    public static void setupPath(String Path) {
        path = Path;
    }

    /**
     * Adds a new recipe
     *
     * @param recipe expects the new recipe
     * @return returns false if recipe is known and true if its a knew recipe
     */
    public static boolean addRecipe(Recipe recipe) {

        // Check if recipe is new
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).name.equals(recipe.name)) {
                return false;
            }
        }
        // Only add if new recipe
        recipes.add(recipe);
        // Sort the recipes
        sortRecipes();
        return true;
    }

    /**
     * Removes a recipe
     *
     * @param recipe expects the id of the recipe to be removed
     */
    public static void removeRecipe(Recipe recipe) {
        // Remove recipe
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
        // Sort the recipe list
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

    /**
     * Type of drinks you want to display
     */
    public enum Drinks {
        NON_ALC(new Drink[]{
                new Drink("Bitter Lemon", R.drawable.anti_alc_bitter_lemon, 0, 1000),
                new Drink("Coca Cola", R.drawable.anti_alc_coca_cola, 0, 1500),
                new Drink("Energy Drink", R.drawable.anti_alc_energy_drink, 0, 1500),
                new Drink("Orange Juice", R.drawable.anti_alc_orange_juice, 0, 1000),
                new Drink("Tonic Water", R.drawable.anti_alc_tonic_water, 0, 1000),
                new Drink("Wild Berry", R.drawable.anti_alc_wild_berry, 0, 1000)
        }),
        ALC(new Drink[]{
                new Drink("Asbach", R.drawable.alc_asbach, 38, 750),
                new Drink("Bacardi", R.drawable.alc_bacardi, 37.5, 750),
                new Drink("Captain Morgan", R.drawable.alc_captain_morgan, 35, 750),
                new Drink("Gin", R.drawable.alc_gin, 40, 750),
                new Drink("Havana", R.drawable.alc_havana, 40, 750),
                new Drink("Lillet", R.drawable.alc_lillet, 17, 750),
                new Drink("Vodka", R.drawable.alc_vodka, 37.5, 750)
        });

        /**
         * Image ids stored in the type
         */
        public Drink[] drinks;

        /**
         * Constructor
         *
         * @param drinks defined for the enum
         */
        Drinks(Drink[] drinks) {
            this.drinks = drinks;
        }

        /**
         * Method to search for a drink by its name
         *
         * @param name of the drink
         * @return the matching drink
         */
        public static Drink getDrink(String name) {
            // Iterate through the non alcoholic drinks
            for (Drink d : NON_ALC.drinks) {
                if (d.name.equals(name)) {
                    return d;
                }
            }
            // Iterate through the alcoholic drinks
            for (Drink d : ALC.drinks) {
                if (d.name.equals(name)) {
                    return d;
                }
            }
            // If the name wasn't found return the first drink
            return NON_ALC.drinks[0];
        }
    }
}
