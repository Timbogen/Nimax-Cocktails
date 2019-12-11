package de.nimax.nimax_cocktails.mixing.models;

public class MixingDrink {

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
     * Water level
     */
    public int level;

    public MixingDrink(String name, int image, double alcohol, int level) {
        this.image = image;
        this.alcohol = alcohol;
        this.name = name;
        this.level = level;
    }

}
