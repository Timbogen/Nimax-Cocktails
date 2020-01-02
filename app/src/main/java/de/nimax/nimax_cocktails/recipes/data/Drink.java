package de.nimax.nimax_cocktails.recipes.data;

import androidx.annotation.NonNull;

public class Drink {

    /**
     * Name of the drink
     */
    public String name;
    /**
     * Image resource of the drink
     */
    public int image;
    /**
     * Alcohol content of the drink
     */
    public double alcohol;
    /**
     * How much of the drink is mixed in
     */
    public int amount;
    /**
     * How much is available
     */
    public int level;

    /**
     * Normal constructor
     */
    public Drink(String name, int image, double alcohol, int level) {
        this.image = image;
        this.alcohol = alcohol;
        this.name = name;
        this.level = level;
    }

    /**
     * Copy-Constructor
     */
    public Drink(Drink drink) {
        this.image = drink.image;
        this.alcohol = drink.alcohol;
        this.name = drink.name;
        this.level = drink.level;
    }

    /**
     * For debugging purposes
     */
    @NonNull
    @Override
    public String toString() {
        return "Name: " + name + ", Amount: " + amount;
    }
}
