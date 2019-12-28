package de.nimax.nimax_cocktails.models;

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

    public Drink(String name, int image, double alcohol, int level) {
        this.image = image;
        this.alcohol = alcohol;
        this.name = name;
        this.level = level;
    }
}
