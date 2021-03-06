package de.nimax.nimax_cocktails.recipes.edit;

import android.app.Activity;
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

import de.nimax.nimax_cocktails.recipes.data.Recipe;

public class RecipeEditAdapter extends ArrayAdapter<Recipe.Drink> {

    /**
     * The context of the activity
     */
    private final Activity activity;
    /**
     * The drinks that should be shown in the list
     */
    private final ArrayList<Recipe.Drink> drinks;

    /**
     * Custom Array Adapter for the list and the spinners
     *
     * @param activity of the adapter
     */
    public RecipeEditAdapter(@NonNull Activity activity, ArrayList<Recipe.Drink> drinks) {
        super(activity, 0, drinks);
        this.activity = activity;
        this.drinks = drinks;
    }

    /**
     * Prepare the view for a given position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View item, @NonNull ViewGroup parent) {
        // Check if the item is null
        if (item == null)
            item = LayoutInflater.from(activity).inflate(R.layout.adapter_recipe_edit, parent, false);

        // Specify the amount
        TextView amount = item.findViewById(R.id.list_amount);
        String amountText = drinks.get(position).amount + " ml";
        amount.setText(amountText);

        // Specify the image
        ImageView image = item.findViewById(R.id.list_image);
        image.setImageResource(drinks.get(position).drink.image);

        // Specify the name
        TextView name = item.findViewById(R.id.list_name);
        name.setText(getString("drink_" + drinks.get(position).drink.name().toLowerCase()));

        // Specify the alcohol
        TextView alcohol = item.findViewById(R.id.list_alcohol);
        String alcoholContent = drinks.get(position).drink.alcohol + "%";
        alcohol.setText(alcoholContent);

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
