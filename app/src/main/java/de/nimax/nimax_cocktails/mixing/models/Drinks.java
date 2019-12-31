package de.nimax.nimax_cocktails.mixing.models;


import com.nimax.nimax_cocktails.R;

/**
 * Type of drinks you want to display
 */
public enum Drinks {
    NON_ALC(new Drink[]{
            new Drink("Bitter Lemon", R.drawable.anti_alc_bitter_lemon, 0, 1000),
            new Drink("Coca Cola", R.drawable.anti_alc_coca_cola, 0, 1500),
            new Drink("Energy Drink", R.drawable.anti_alc_energy_drink, 0, 1500),
            new Drink("Orange Juice", R.drawable.anti_alc_orange_juice, 0, 1000),
            new Drink("Tonic Water", R.drawable.anti_alc_tonic_water, 0, 1000),
            new Drink("Wild Berry", R.drawable.anti_alc_wild_berry, 0, 1000)
    }),
    ALC(new Drink[]{
            new Drink("Asbach", R.drawable.alc_asbach, 38, 750),
            new Drink("Bacardi", R.drawable.alc_bacardi, 37.5, 750),
            new Drink("Captain Morgan", R.drawable.alc_captain_morgan, 35, 750),
            new Drink("Gin", R.drawable.alc_gin, 40, 750),
            new Drink("Havana", R.drawable.alc_havana, 40, 750),
            new Drink("Lillet", R.drawable.alc_lillet, 17, 750),
            new Drink("Vodka", R.drawable.alc_vodka, 37.5, 750)
    });

    /**
     * Image ids stored in the type
     */
    public Drink[] drinks;

    /**
     * Constructor
     * @param drinks defined for the enum
     */
    Drinks(Drink[] drinks) {
        this.drinks = drinks;
    }

    /**
     * Method to search for a drink by its name
     * @param name of the drink
     * @return the matching drink
     */
    public static Drink getDrink(String name) {
        // Iterate through the non alcoholic drinks
        for (Drink d : NON_ALC.drinks) {
            if (d.name.equals(name)) {
                return d;
            }
        }
        // Iterate through the alcoholic drinks
        for (Drink d : ALC.drinks) {
            if (d.name.equals(name)) {
                return d;
            }
        }
        // If the name wasn't found return the first drink
        return NON_ALC.drinks[0];
    }
}