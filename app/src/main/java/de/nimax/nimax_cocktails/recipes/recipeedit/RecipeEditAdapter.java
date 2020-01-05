package de.nimax.nimax_cocktails.recipes.recipeedit;

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

import de.nimax.nimax_cocktails.recipes.data.Drink;

public class RecipeEditAdapter extends ArrayAdapter<Drink> {

    /**
     * The context of the activity
     */
    private Context context;
    /**
     * The drinks that should be shown in the list
     */
    private ArrayList<Drink> drinks;

    /**
     * Custom Array Adapter for the list and the spinners
     * @param context of the adapter
     */
    RecipeEditAdapter(@NonNull Context context, ArrayList<Drink> drinks) {
        super(context, 0, drinks);
        this.context = context;
        this.drinks = drinks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View item, @NonNull ViewGroup parent) {
        // Check if the item is null
        if (item == null) item = LayoutInflater.from(context).inflate(R.layout.adapter_small_list, parent, false);

        // Specify the image
        ImageView image = item.findViewById(R.id.list_image);
        image.setImageResource(drinks.get(position).image);

        // Specify the name
        TextView name = item.findViewById(R.id.list_name);
        name.setText(drinks.get(position).name);

        // Specify the alcohol
        TextView alcohol = item.findViewById(R.id.list_alcohol);
        String alcoholContent = drinks.get(position).alcohol + "%";
        alcohol.setText(alcoholContent);

        // Return the item
        return item;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
