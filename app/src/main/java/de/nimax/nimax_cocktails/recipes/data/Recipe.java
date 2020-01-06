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
import java.util.Arrays;

public class Recipe {

    /**
     * Max volume of a glass used for the machine
     */
    public static final int MAX_AMOUNT = 360;
    /**
     * Attribute names for the json format
     */
    private static final String NAME = "name";
    private static final String AMOUNT = "amount";
    private static final String IMAGE = "image";
    private static final String INGREDIENTS = "ingredients";
    /**
     * Name of the recipe
     */
    public String name;
    /**
     * Image of the recipe
     */
    public Bitmap image;
    /**
     * The drinks that the recipe is containing
     */
    public ArrayList<Drink> drinks = new ArrayList<>();

    /**
     * Normal constructor
     */
    public Recipe(String name, Drink... drinks) {
        this.name = name;
        this.drinks = new ArrayList<>(Arrays.asList(drinks));
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
        for (Drink d : recipe.drinks) {
            drinks.add(new Drink(d));
        }
    }

    /**
     * Constructor for evaluating a json object
     *
     * @param recipe json object of a recipe
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
                Drink drink = new Drink(Bar.Drinks.getDrink(name));
                drink.amount = ingredients.getJSONObject(i).getInt(AMOUNT);
                drinks.add(drink);
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
     * Method to transform a recipe to a json
     *
     * @return recipe in json format
     */
    JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray recipe = new JSONArray();
        try {
            for (Drink drink : drinks) {
                JSONObject ingredients = new JSONObject();

                // Define the content
                ingredients.put(NAME, drink.name);
                ingredients.put(AMOUNT, drink.amount);

                // Add to array
                recipe.put(ingredients);
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
            json.put(INGREDIENTS, recipe);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Method to check whether a recipe is recipeable
     *
     * @return true if the recipe is recipeable
     */
    public boolean isMixable() {
        for (Drink drink : drinks) {
            if (drink.amount > drink.level) return false;
        }
        return true;
    }

    /**
     * Method to check whether the recipe is empty
     *
     * @return true if recipe is empty
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
     */
    public void addDrink(Drink drink, int amount, Activity activity) {
        // Check if the drink expands the max volume
        int vol = amount;
        for (Drink d : drinks) {
            if (!d.name.equals(drink.name)) vol += d.amount;
        }
        if (vol > MAX_AMOUNT) {
            Toast toast = Toast.makeText(activity, activity.getString(R.string.mixing_max_amount) + " " + MAX_AMOUNT + " ml!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        // Add or update the drink
        for (Drink d : drinks) {
            // If the recipe already contains the drink update the amount
            if (drink.name.equals(d.name)) {
                d.amount = amount;
                return;
            }
        }
        // Otherwise add the drink
        drink.amount = amount;
        drinks.add(drink);
    }

    /**
     * Method to remove a drink
     *
     * @param i to be removed
     */
    public void removeDrink(int i) {
        drinks.remove(i);
    }

    /**
     * For debugging purposes
     */
    @NonNull
    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("Recipe: ").append(name).append("\n");
        for (Drink d : drinks) {
            string.append(d).append("\n");
        }
        return string.toString();
    }
}
