package de.nimax.nimax_cocktails.mixing;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.ViewListener;

import java.util.ArrayList;

import de.nimax.nimax_cocktails.recipes.data.Drinks;

public class DrinkViewListener implements ViewListener {

    /**
     * The drinks to be shown
     */
    private final ArrayList<Drinks> drinks;
    /**
     * The currently active activity
     */
    private final Activity activity;

    /**
     * Constructor
     *
     * @param drinks   The drinks to be shown
     * @param activity The currently active activity
     */
    public DrinkViewListener(ArrayList<Drinks> drinks, Activity activity) {
        this.drinks = drinks;
        this.activity = activity;
    }

    /**
     * Update the view for a given position
     *
     * @param position of the item
     * @return the updated view
     */
    @Override
    public View setViewForPosition(int position) {
        View drink_item = View.inflate(activity, R.layout.adapter_drinks_carousel, null);

        // Modify the picture
        ImageView image = drink_item.findViewById(R.id.image_drink);
        image.setImageResource(drinks.get(position).image);

        // Set the name
        TextView name = drink_item.findViewById(R.id.text_drink_name);
        name.setText(getString("drink_" + drinks.get(position).name().toLowerCase()));

        // Set the alcohol content
        TextView alcohol = drink_item.findViewById(R.id.text_drink_alcohol);
        String alcoholContent = drinks.get(position).alcohol + "%";
        alcohol.setText(alcoholContent);

        // Set the origin text
        TextView origin = drink_item.findViewById(R.id.text_drink_info);
        origin.setText(getString("drink_" + drinks.get(position).name().toLowerCase() + "_info"));

        return drink_item;
    }

    /**
     * Get a string by name
     *
     * @param name of the string
     * @return the corresponding translation
     */
    private String getString(String name) {
        int resourceId = activity.getResources().getIdentifier(
                name,
                "string",
                activity.getPackageName()
        );
        return activity.getString(resourceId);
    }
}
