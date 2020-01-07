package de.nimax.nimax_cocktails.drinkers.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.util.ArrayList;

public class Drinker {

    /**
     * Attribute names for the json format
     */
    private static final String NAME = "name";
    private static final String IMAGE = "image";
    private static final String DRINKS = "drinks";
    private static final String AMOUNT = "amount";
    private static final String ALCOHOL = "alcohol";
    private static final String TIME = "time";
    /**
     * Name of the drinker
     */
    public String name;
    /**
     * Image of the drinker
     */
    public Bitmap image;
    /**
     * The drinks the drinker consumed
     */
    public ArrayList<HistoryDrink> drinks = new ArrayList<>();

    /**
     * Constructor
     */
    public Drinker(String name) {
        this.name = name;
    }

    /**
     * Constructor for evaluating a json object
     * @param drinker json object of the drinker
     */
    Drinker(JSONObject drinker) {
        try {
            // Get name property
            this.name = drinker.getString(NAME);

            // Get the ingredients
            JSONArray drinks = drinker.getJSONArray(DRINKS);

            // Extract the single drinks
            for (int i = 0; i < drinks.length(); i++) {
                String name = drinks.getJSONObject(i).getString(NAME);
                HistoryDrink drink = new HistoryDrink(name);
                drink.amount = drinks.getJSONObject(i).getInt(AMOUNT);
                drink.alcohol = drinks.getJSONObject(i).getDouble(ALCOHOL);
                drink.time = Date.valueOf(drinks.getJSONObject(i).getString(TIME));
                this.drinks.add(drink);
            }

            // Get the image
            String encodedImage = drinker.getString(IMAGE);
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
        JSONArray drinks = new JSONArray();
        try {
            for (HistoryDrink drink : this.drinks) {
                JSONObject ingredients = new JSONObject();

                // Define the content
                ingredients.put(NAME, drink.name);
                ingredients.put(AMOUNT, drink.amount);
                ingredients.put(ALCOHOL, drink.alcohol);
                ingredients.put(TIME, drink.time.toString());

                // Add to array
                drinks.put(ingredients);
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
            json.put(DRINKS, drinks);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
