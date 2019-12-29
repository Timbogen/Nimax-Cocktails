package de.nimax.nimax_cocktails.mixing.models;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.nimax.nimax_cocktails.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Mix {

    /**
     * Attribute names for the json format
     */
    private static final String NAME = "name";
    private static final String AMOUNT = "amount";
    private static final String IMAGE= "image";
    private static final String INGREDIENTS = "ingredients";
    /**
     * Max volume of a glass used for the machine
     */
    public static final int MAX_AMOUNT = 350;
    /**
     * Name of the mix
     */
    public String name;
    /**
     * Image of the mix
     */
    public Bitmap image;
    /**
     * The drinks that the mix is containing
     */
    public ArrayList<Drink> drinks = new ArrayList<>();

    /**
     * Normal constructor
     */
    public Mix(String name, Drink... drinks) {
        this.name = name;
        this.drinks = new ArrayList<>(Arrays.asList(drinks));
    }

    /**
     * Constructor for evaluating a json object
     * @param mix json object of a mix
     */
    public Mix(JSONObject mix) {
        try {
            // Get name property
            this.name = mix.getString(NAME);

            // Get the ingredients
            JSONArray ingredients = mix.getJSONArray(INGREDIENTS);

            // Extract the single drinks
            for (int i = 0; i < ingredients.length(); i++) {
                String name = ingredients.getJSONObject(i).getString(NAME);
                Drink drink = new Drink(Drinks.getDrink(name));
                drink.amount = ingredients.getJSONObject(i).getInt(AMOUNT);
                drinks.add(drink);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to transform a mix to a json
     * @return mix in json format
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray recipe = new JSONArray();
        try {
            for (int i = 0; i < drinks.size(); i++) {
                JSONObject ingredients = new JSONObject();

                // Define the content
                ingredients.put(NAME, drinks.get(i).name);
                ingredients.put(AMOUNT, drinks.get(i).amount);

                // Add to array
                recipe.put(ingredients);
            }

            // Add Array to the main object
            json.put(NAME, name);
            json.put(IMAGE, image);
            json.put(INGREDIENTS, recipe);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Method to check whether a mix is mixable
     * @return true if the mix is mixable
     */
    public boolean isMixable() {
        for (Drink drink : drinks) {
            if (drink.amount > drink.level) return false;
        }
        return true;
    }

    /**
     * Method to check whether the mix is empty
     * @return true if mix is empty
     */
    public boolean isEmpty() {
        return drinks.size() == 0;
    }

    /**
     * Method to add a drink
     * @param drink to be added
     * @param amount of the drink
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
            // If the mix already contains the drink update the amount
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
        string.append("Mix: ").append(name).append("\n");
        for (Drink d : drinks) {
            string.append(d).append("\n");
        }
        return string.toString();
    }
}
