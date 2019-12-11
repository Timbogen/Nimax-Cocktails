package de.nimax.nimax_cocktails.mixing.models;


import com.nimax.nimax_cocktails.R;

/**
 * Type of drinks you want to display
 */
public enum MixingType {
    ANTI_ALC(new MixingDrink[]{
            new MixingDrink("Bitter Lemon", R.drawable.anti_alc_bitter_lemon, 0, 1000),
            new MixingDrink("Coca Cola", R.drawable.anti_alc_coca_cola, 0, 1500),
            new MixingDrink("Energy Drink", R.drawable.anti_alc_energy_drink, 0, 1500),
            new MixingDrink("Orange Juice", R.drawable.anti_alc_orange_juice, 0, 1000),
            new MixingDrink("Tonic Water", R.drawable.anti_alc_tonic_water, 0, 1000),
            new MixingDrink("Wild Berry", R.drawable.anti_alc_wild_berry, 0, 1000)
    }),
    ALC(new MixingDrink[]{
            new MixingDrink("Asbach", R.drawable.alc_asbach, 38, 750),
            new MixingDrink("Bacardi", R.drawable.alc_bacardi, 37.5, 750),
            new MixingDrink("Captain Morgan", R.drawable.alc_captain_morgan, 35, 750),
            new MixingDrink("Gin", R.drawable.alc_gin, 40, 750),
            new MixingDrink("Havana", R.drawable.alc_havana, 40, 750),
            new MixingDrink("Lillet", R.drawable.alc_lillet, 17, 750),
            new MixingDrink("Vodka", R.drawable.alc_vodka, 37.5, 750)
    });

    /**
     * Image ids stored in the type
     */
    public MixingDrink[] drinks;

    /**
     * Constructor
     * @param drinks defined for the enum
     */
    MixingType(MixingDrink[] drinks) {
        this.drinks = drinks;
    }
}
