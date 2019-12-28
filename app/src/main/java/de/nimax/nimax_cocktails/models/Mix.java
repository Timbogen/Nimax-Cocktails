package de.nimax.nimax_cocktails.models;

import java.util.ArrayList;
import java.util.Arrays;

public class Mix {

    /**
     * Name of the mix
     */
    public String name;
    /**
     * The drinks that the mix is containing
     */
    public ArrayList<Drink> drinks;

    public Mix(String name, Drink... drinks) {
        this.name = name;
        this.drinks = new ArrayList<>(Arrays.asList(drinks));
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
     */
    public void addDrink(Drink drink, int amount) {
        for (Drink d : drinks) {
            // If the mix already contains the drink update the amount
            if (drink.name.equals(d.name)) {
                d.amount += amount;
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
}
