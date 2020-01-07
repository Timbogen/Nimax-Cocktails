package de.nimax.nimax_cocktails.drinkers.data;

import java.util.Date;

import de.nimax.nimax_cocktails.recipes.data.Recipe;

public class HistoryDrink {

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
    public double alcohol;
    /**
     * Timestamp when it was ordered
     */
    public Date time;

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
        alcohol = recipe.getAlcohol();
        time = new Date();
    }
}
