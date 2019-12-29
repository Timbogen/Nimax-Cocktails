package de.nimax.nimax_cocktails.mixing;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.ViewListener;

import de.nimax.nimax_cocktails.mixing.models.Drinks;


public class MixingViewListener implements ViewListener {

    /**
     * Type of drinks
     */
    private Drinks type;
    /**
     * Active activity
     */
    private Activity activity;

    public MixingViewListener(Drinks type, Activity activity) {
        this.type = type;
        this.activity = activity;
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
        String alcoholContent =type.drinks[position].alcohol + "%";
        alcohol.setText(alcoholContent);

        // Set the level
        TextView level = drink_item.findViewById(R.id.text_drink_level);
        String drinkLevel = type.drinks[position].level + " ml";
        level.setText(drinkLevel);

        return drink_item;
    }
}
