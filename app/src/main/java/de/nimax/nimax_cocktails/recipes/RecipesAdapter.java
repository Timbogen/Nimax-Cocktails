package de.nimax.nimax_cocktails.recipes;

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

import de.nimax.nimax_cocktails.recipes.data.Bar;
import de.nimax.nimax_cocktails.recipes.data.Recipe;
import de.nimax.nimax_cocktails.recipes.edit.RecipeEditActivity;

public class RecipesAdapter extends ArrayAdapter<Recipe> {

    /**
     * The activity of the activity
     */
    private final Activity activity;
    /**
     * The recipes that should be shown in the list
     */
    private final ArrayList<Recipe> recipes;

    /**
     * Custom Array Adapter for the list and the spinners
     *
     * @param activity of the adapter
     */
    RecipesAdapter(@NonNull Activity activity, ArrayList<Recipe> drinks) {
        super(activity, 0, drinks);
        this.activity = activity;
        this.recipes = drinks;
    }

    /**
     * Update the view for a given position
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View item, @NonNull ViewGroup parent) {
        // Check if the item is null
        if (item == null)
            item = LayoutInflater.from(activity).inflate(R.layout.adapter_recipes, parent, false);

        // Specify the icon
        ImageView image = item.findViewById(R.id.list_image);
        if (recipes.get(position).image != null) {
            image.setImageBitmap(recipes.get(position).image);
        } else {
            image.setImageResource(R.drawable.icon_mixing);
        }

        // Specify the name
        TextView name = item.findViewById(R.id.list_name);
        name.setText(recipes.get(position).name);

        // Specify the ingredients
        TextView value = item.findViewById(R.id.list_ingredients);
        StringBuilder ingredients = new StringBuilder();
        for (Recipe.Drink d : recipes.get(position).drinks) {
            ingredients.append(getString("drink_" + d.drink.name().toLowerCase())).append(", ");
        }
        value.setText(ingredients.substring(0, ingredients.length() - 2));

        // Set the on click listener
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecipeEditActivity.recipe = Bar.recipes.get(position);
                Intent intent = new Intent(activity, RecipeEditActivity.class);
                activity.startActivityForResult(intent, 0);
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        // Return the item
        return item;
    }

    /**
     * Get the view
     */
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
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
