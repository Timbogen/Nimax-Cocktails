package de.nimax.nimax_cocktails.recipes;

import android.content.Context;
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

import de.nimax.nimax_cocktails.mixing.models.Drink;
import de.nimax.nimax_cocktails.mixing.models.Mix;

public class RecipesAdapter extends ArrayAdapter<Mix> {

    /**
     * The context of the activity
     */
    private Context context;
    /**
     * The mixes that should be shown in the list
     */
    private ArrayList<Mix> mixes;

    /**
     * Custom Array Adapter for the list and the spinners
     * @param context of the adapter
     */
    public RecipesAdapter(@NonNull Context context, ArrayList<Mix> drinks) {
        super(context, 0, drinks);
        this.context = context;
        this.mixes = drinks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View item, @NonNull ViewGroup parent) {
        // Check if the item is null
        if (item == null) item = LayoutInflater.from(context).inflate(R.layout.adapter_recipes_list, parent, false);

        // Update the flag icon
        if (mixes.get(position).image != null) {
            ImageView image = item.findViewById(R.id.list_image);
            image.setImageBitmap(mixes.get(position).image);
        }

        // Update the currency name
        TextView name = item.findViewById(R.id.list_name);
        name.setText(mixes.get(position).name);

        // Update the currency value
        TextView value = item.findViewById(R.id.list_ingredients);
        StringBuilder ingredients = new StringBuilder();
        for (Drink d : mixes.get(position).drinks) {
            ingredients.append(d.name).append(", ");
        }
        value.setText(ingredients.substring(0, ingredients.length() - 2));

        // Return the item
        return item;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
