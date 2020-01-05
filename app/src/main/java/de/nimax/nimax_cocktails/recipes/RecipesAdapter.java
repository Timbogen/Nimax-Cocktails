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

import de.nimax.nimax_cocktails.recipes.data.Drink;
import de.nimax.nimax_cocktails.recipes.data.Recipe;

public class RecipesAdapter extends ArrayAdapter<Recipe> {

    /**
     * The context of the activity
     */
    private Context context;
    /**
     * The recipes that should be shown in the list
     */
    private ArrayList<Recipe> recipes;

    /**
     * Custom Array Adapter for the list and the spinners
     * @param context of the adapter
     */
    RecipesAdapter(@NonNull Context context, ArrayList<Recipe> drinks) {
        super(context, 0, drinks);
        this.context = context;
        this.recipes = drinks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View item, @NonNull ViewGroup parent) {
        // Check if the item is null
        if (item == null) item = LayoutInflater.from(context).inflate(R.layout.adapter_big_list, parent, false);

        // Specify the icon
        if (recipes.get(position).image != null) {
            ImageView image = item.findViewById(R.id.list_image);
            image.setImageBitmap(recipes.get(position).image);
        }

        // Specify the name
        TextView name = item.findViewById(R.id.list_name);
        name.setText(recipes.get(position).name);

        // Specify the ingredients
        TextView value = item.findViewById(R.id.list_ingredients);
        StringBuilder ingredients = new StringBuilder();
        for (Drink d : recipes.get(position).drinks) {
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
