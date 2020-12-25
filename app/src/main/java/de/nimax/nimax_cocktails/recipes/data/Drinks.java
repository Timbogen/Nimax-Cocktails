package de.nimax.nimax_cocktails.recipes.data;

import com.nimax.nimax_cocktails.R;

import java.util.ArrayList;

public enum Drinks {

    ASBACH(R.drawable.drink_asbach, 38),
    BACARDI(R.drawable.drink_bacardi, 37.5),
    BITTER_LEMON(R.drawable.drink_bitter_lemon, 0),
    BLUE_CURACAO(R.drawable.drink_blue_curacao, 21),
    CAMPARI(R.drawable.drink_campari, 25),
    CAPTAIN_MORGAN(R.drawable.drink_captain_morgan, 35),
    CHERRY_JUICE(R.drawable.drink_cherry_juice, 0),
    COCA_COLA(R.drawable.drink_coca_cola, 0),
    ENERGY_DRINK(R.drawable.drink_energy_drink, 0),
    GIN(R.drawable.drink_gin, 40),
    GRAPEFRUIT_JUICE(R.drawable.drink_grapefruit_juice, 0),
    GRENADINE_SYRUP(R.drawable.drink_grenadine_syrup, 0),
    HAVANA(R.drawable.drink_havana, 40),
    JOSTER(R.drawable.drink_joster, 15),
    LILLET(R.drawable.drink_lillet, 17),
    LIME_JUICE(R.drawable.drink_lime_juice, 0),
    ORANGE_JUICE(R.drawable.drink_orange_juice, 0),
    PASSION_FRUIT_JUICE(R.drawable.drink_passion_fruit_juice, 0),
    PINEAPPLE_JUICE(R.drawable.drink_pineapple_juice, 0),
    PITU(R.drawable.drink_pitu, 40),
    STRAWBERRY_SYRUP(R.drawable.drink_strawberry_syrup, 0),
    TEQUILA(R.drawable.drink_tequila, 38),
    TONIC_WATER(R.drawable.drink_tonic_water, 0),
    VODKA(R.drawable.drink_vodka, 37.5),
    WILD_BERRY(R.drawable.drink_wild_berry, 0);

    /**
     * List containing all the drinks
     */
    public static final ArrayList<Drinks> ALL = new ArrayList<>();
    /**
     * List containing all the alcoholic drinks
     */
    public static final ArrayList<Drinks> ALC = new ArrayList<>();
    /**
     * List containing all the alcoholic drinks
     */
    public static final ArrayList<Drinks> NON_ALC = new ArrayList<>();

    static {
        for (Drinks drink : Drinks.values()) {
            ALL.add(drink);
            if (drink.alcohol > 0.5) ALC.add(drink);
            else NON_ALC.add(drink);
        }
    }
    
    /**
     * Image resource of the drink
     */
    public int image;
    /**
     * Alcohol percentage of the drink
     */
    public double alcohol;

    /**
     * Constructor
     *
     * @param image   Image resource of the drink
     * @param alcohol Alcohol percentage of the drink
     */
    Drinks(int image, double alcohol) {
        this.image = image;
        this.alcohol = alcohol;
    }

    /**
     * Get a drink by the name
     *
     * @param name The name of the drink
     * @return The matching drink
     */
    public static Drinks getDrink(String name) {
        for (Drinks drink : ALL) {
            if (drink.name().equals(name)) return drink;
        }
        return ASBACH;
    }
}
