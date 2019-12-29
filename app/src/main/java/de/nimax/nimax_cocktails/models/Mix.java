package de.nimax.nimax_cocktails.models;

import android.app.Activity;
import android.widget.Toast;

import com.nimax.nimax_cocktails.R;

import java.util.ArrayList;
import java.util.Arrays;

public class Mix {

    /**
     * Max volume of a glass used for the machine
     */
    public static final int MAX_AMOUNT = 350;
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
     * @param amount of the drink
     * @param activity the current activity
     */
    public void addDrink(Drink drink, int amount, Activity activity) {
        // Check if the drink expands the max volume
        int vol = amount;
        for (Drink d : drinks) {
            if (!d.name.equals(drink.name)) vol += d.amount;
        }
        if (vol > MAX_AMOUNT) {
            Toast toast = Toast.makeText(activity, activity.getString(R.string.mixing_max_amount) + " " + MAX_AMOUNT + " ml!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        // Add or update the drink
        for (Drink d : drinks) {
            // If the mix already contains the drink update the amount
            if (drink.name.equals(d.name)) {
                d.amount = amount;
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
