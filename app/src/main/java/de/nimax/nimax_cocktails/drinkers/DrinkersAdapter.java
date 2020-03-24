package de.nimax.nimax_cocktails.drinkers;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nimax.nimax_cocktails.R;

import java.util.ArrayList;

import de.nimax.nimax_cocktails.drinkers.data.Administration;
import de.nimax.nimax_cocktails.drinkers.data.Drinker;
import de.nimax.nimax_cocktails.drinkers.edit.DrinkerEditActivity;
import de.nimax.nimax_cocktails.recipes.data.Bar;
import de.nimax.nimax_cocktails.recipes.edit.RecipeEditActivity;

public class DrinkersAdapter extends ArrayAdapter<Drinker> {

    /**
     * The activity of the activity
     */
    private Activity activity;
    /**
     * The drinkers that should be shown in the list
     */
    private ArrayList<Drinker> drinkers;

    /**
     * Custom Array Adapter for the list and the spinners
     * @param activity of the adapter
     */
    DrinkersAdapter(@NonNull Activity activity, ArrayList<Drinker> drinkers) {
        super(activity, 0, drinkers);
        this.activity = activity;
        this.drinkers = drinkers;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View item, @NonNull ViewGroup parent) {
        // Check if the item is null
        if (item == null) item = LayoutInflater.from(activity).inflate(R.layout.adapter_drinkers, parent, false);

        // Specify the icon
        ImageView image = item.findViewById(R.id.list_image);
        if (drinkers.get(position).image != null) {
            image.setImageBitmap(drinkers.get(position).image);
        } else {
            image.setImageResource(R.drawable.menu_button_drinkers);
        }

        // Specify the name
        TextView name = item.findViewById(R.id.list_name);
        name.setText(drinkers.get(position).name);

        // Specify the ingredients
        TextView value = item.findViewById(R.id.list_info);
        if (drinkers.get(position).drinks.size() > 0) {
            String text = activity.getString(R.string.drinkers_last_drink)
                    + " " + drinkers.get(position).drinks.get(0).name;
            value.setText(text);
        } else {
            value.setText(activity.getString(R.string.drinkers_no_last_drink));
        }

        // Set the on click listener
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrinkerEditActivity.drinker = Administration.drinkers.get(position);
                Intent intent = new Intent(activity, DrinkerEditActivity.class);
                activity.startActivityForResult(intent, 0);
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        // Return the item
        return item;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
