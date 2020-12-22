package de.nimax.nimax_cocktails.drinkers.edit;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nimax.nimax_cocktails.R;

import java.util.ArrayList;

import de.nimax.nimax_cocktails.drinkers.data.HistoryDrink;

public class DrinkerEditAdapter extends ArrayAdapter<HistoryDrink> {

    /**
     * The context of the activity
     */
    private final Activity activity;
    /**
     * The drinks that should be shown in the list
     */
    private final ArrayList<HistoryDrink> drinks;

    /**
     * Custom Array Adapter for the list and the spinners
     *
     * @param activity of the adapter
     */
    DrinkerEditAdapter(@NonNull Activity activity, ArrayList<HistoryDrink> drinks) {
        super(activity, 0, drinks);
        this.activity = activity;
        this.drinks = drinks;
    }

    /**
     * Prepare a view for a given position
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View item, @NonNull ViewGroup parent) {
        // Check if the item is null
        if (item == null)
            item = LayoutInflater.from(activity).inflate(R.layout.adapter_drinker_edit, parent, false);

        // Specify the time
        TextView time = item.findViewById(R.id.list_time);
        time.setText(drinks.get(position).time.toString());

        // Specify the name
        TextView name = item.findViewById(R.id.list_name);
        name.setText(drinks.get(position).name);

        // Specify the alcohol
        TextView alcohol = item.findViewById(R.id.list_alcohol);
        String alcoholContent = drinks.get(position).alcohol + "%";
        alcohol.setText(alcoholContent);

        // Specify the amount
        TextView amount = item.findViewById(R.id.list_amount);
        String amountText = drinks.get(position).amount + " ml";
        amount.setText(amountText);

        // Return the item
        return item;
    }

    /**
     * Get the dropdown view
     */
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
