package de.nimax.nimax_cocktails.mixing;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.ViewListener;

import de.nimax.nimax_cocktails.recipes.data.Bar;
import de.nimax.nimax_cocktails.recipes.data.Drink;
import de.nimax.nimax_cocktails.settings.SettingsActivity;

public class DrinkViewListener implements ViewListener {

    /**
     * Type of drinks
     */
    private Bar.Drinks type;
    /**
     * Active activity
     */
    private Activity activity;
    /**
     * The text views to be updated
     */
    private TextView[] items;

    public DrinkViewListener(Bar.Drinks type, Activity activity) {
        this.type = type;
        this.activity = activity;
        items = new TextView[type.drinks.length];
    }

    @Override
    public View setViewForPosition(int position) {
        View drink_item = activity.getLayoutInflater().inflate(R.layout.adapter_drinks_carousel, null);

        // Modify the picture
        ImageView image = drink_item.findViewById(R.id.image_drink);
        image.setImageResource(type.drinks[position].image);

        // Set the name
        TextView name = drink_item.findViewById(R.id.text_drink_name);
        name.setText(type.drinks[position].name);

        // Set the alcohol content
        TextView alcohol = drink_item.findViewById(R.id.text_drink_alcohol);
        String alcoholContent = type.drinks[position].alcohol + "%";
        alcohol.setText(alcoholContent);

        // Set the level
        TextView level = drink_item.findViewById(R.id.text_drink_level);
        Drink drink = findDrink(position);
        String drinkLevel = (drink == null ? 0 : drink.amount) + " ml";
        items[position] = level;
        level.setText(drinkLevel);

        return drink_item;
    }

    /**
     * Update the level values
     */
    public void update() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < items.length; i++) {
                    Drink drink = findDrink(i);
                    String drinkLevel = (drink == null ? 0 : drink.amount) + " ml";
                    items[i].setText(drinkLevel);
                }
            }
        });
    }

    /**
     * Find the matching drink
     *
     * @param position of the drink
     * @return the drink
     */
    public Drink findDrink(int position) {
        Drink drink = null;
        for (Drink barDrink : SettingsActivity.nonAlcDrinks) {
            if (type.drinks[position].name.equals(barDrink.name)) {
                drink = barDrink;
                break;
            }
        }
        for (Drink barDrink : SettingsActivity.alcDrinks) {
            if (type.drinks[position].name.equals(barDrink.name)) {
                drink = barDrink;
                break;
            }
        }
        return drink;
    }
}
