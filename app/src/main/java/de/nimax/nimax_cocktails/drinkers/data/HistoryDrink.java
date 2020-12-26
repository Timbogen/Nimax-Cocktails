package de.nimax.nimax_cocktails.drinkers.data;

import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import de.nimax.nimax_cocktails.recipes.data.Recipe;

public class HistoryDrink {

    /**
     * The date format
     */
    public static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    /**
     * The alcohol format
     */
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");
    /**
     * Name of the drink
     */
    public String name;
    /**
     * Amount of the drink
     */
    public int amount;
    /**
     * Alcohol percentage
     */
    public String alcohol;
    /**
     * Image of the recipe
     */
    public Bitmap image;
    /**
     * Timestamp when it was ordered
     */
    public String date;

    /**
     * Constructor
     */
    HistoryDrink(String name) {
        this.name = name;
    }

    /**
     * Constructor
     * @param recipe that was ordered
     */
    public HistoryDrink(Recipe recipe) {
        name = recipe.name;
        amount = recipe.getAmount();
        alcohol = decimalFormat.format(recipe.getAlcohol());
        date = dateFormat.format(new Date());
        if (recipe.image != null) {
            this.image = recipe.image.copy(recipe.image.getConfig(), true);
        }
    }
}
