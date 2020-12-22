package de.nimax.nimax_cocktails.recipes.data;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.nimax.nimax_cocktails.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Recipe {

    /**
     * Max volume of a glass used for the machine
     */
    public static final int MAX_AMOUNT = 230;
    /**
     * Attribute names for the json format
     */
    private static final String NAME = "name", AMOUNT = "amount", IMAGE = "image", INGREDIENTS = "ingredients";
    /**
     * Name of the drinker
     */
    public String name;
    /**
     * Image of the drinker
     */
    public Bitmap image;
    /**
     * The drinks that the drinker is containing
     */
    public ArrayList<Drink> drinks = new ArrayList<>();

    /**
     * Normal constructor
     */
    public Recipe(String name) {
        this.name = name;
    }

    /**
     * Copy constructor
     *
     * @param recipe to be copied
     */
    public Recipe(Recipe recipe) {
        this.name = recipe.name;
        if (image != null) {
            this.image = recipe.image.copy(recipe.image.getConfig(), true);
        }
        drinks.addAll(recipe.drinks);
    }

    /**
     * Constructor for evaluating a json object
     *
     * @param recipe json object of a drinker
     */
    Recipe(JSONObject recipe) {
        try {
            // Get name property
            this.name = recipe.getString(NAME);

            // Get the ingredients
            JSONArray ingredients = recipe.getJSONArray(INGREDIENTS);

            // Extract the single drinks
            for (int i = 0; i < ingredients.length(); i++) {
                String name = ingredients.getJSONObject(i).getString(NAME);
                Drinks drink = Drinks.getDrink(name);
                drinks.add(new Drink(drink, ingredients.getJSONObject(i).getInt(AMOUNT)));
            }

            // Get the image
            String encodedImage = recipe.getString(IMAGE);
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (JSONException e) {
            image = null;
        }
    }

    /**
     * Method to transform a drinker to a json
     *
     * @return drinker in json format
     */
    JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray ingredients = new JSONArray();
        try {
            for (Drink drink : drinks) {
                JSONObject ingredient = new JSONObject();

                // Define the content
                ingredient.put(NAME, drink.drink.name());
                ingredient.put(AMOUNT, drink.amount);

                // Add to array
                ingredients.put(ingredient);
            }
            // Save the name
            json.put(NAME, name);

            // Compress the image
            if (image != null) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, os);
                byte[] bytes = os.toByteArray();
                String encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
                // Save the image
                json.put(IMAGE, encodedImage);
            }

            // Save the ingredients
            json.put(INGREDIENTS, ingredients);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Method to check whether the drinker is empty
     *
     * @return true if drinker is empty
     */
    public boolean isEmpty() {
        return drinks.size() == 0;
    }

    /**
     * Method to add a drink
     *
     * @param drink    to be added
     * @param amount   of the drink
     * @param activity the current activity
     * @return true if the operation was successful
     */
    public boolean addDrink(Drinks drink, int amount, Activity activity) {
        // Check if the drink expands the max volume
        int totalAmount = amount;
        for (Drink ingredient : drinks) {
            if (!ingredient.drink.name().equals(drink.name())) totalAmount += ingredient.amount;
        }
        if (totalAmount > MAX_AMOUNT) {
            Toast toast = Toast.makeText(activity, activity.getString(R.string.mixing_max_amount) + " " + MAX_AMOUNT + " ml!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        // Check if the drink has to be removed first
        int position = -1;
        for (int i = 0; i < drinks.size(); i++)
            if (drink.name().equals(drinks.get(i).drink.name())) position = i;
        if (position > -1) drinks.remove(position);

        // Otherwise add the drink
        drinks.add(new Drink(drink, amount));
        return true;
    }

    /**
     * @return the amount of liquid
     */
    public int getAmount() {
        int totalAmount = 0;
        for (Drink drink : drinks) totalAmount += drink.amount;
        return totalAmount;
    }

    /**
     * @return the alcohol percentage of the drink
     */
    public double getAlcohol() {
        double alcohol = 0;
        for (Drink drink : drinks) {
            alcohol += drink.amount * drink.drink.alcohol / 100;
        }
        return alcohol / getAmount();
    }

    /**
     * For debugging purposes
     */
    @NonNull
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("Recipe: ").append(name).append("\n");
        for (Drink drink : drinks) {
            string.append(drink.drink.name()).append("\n");
        }
        return string.toString();
    }

    /**
     * This class describes an ingredient for a mix
     */
    public static class Drink {
        /**
         * The actual drink
         */
        public Drinks drink;
        /**
         * The amount of the drink
         */
        public int amount;

        /**
         * Constructor
         *
         * @param drink  The actual drink
         * @param amount The amount of the drink
         */
        private Drink(Drinks drink, int amount) {
            this.drink = drink;
            this.amount = amount;
        }
    }
}
