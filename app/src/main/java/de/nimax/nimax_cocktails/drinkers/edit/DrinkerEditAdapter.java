package de.nimax.nimax_cocktails.drinkers.edit;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.nimax.nimax_cocktails.drinkers.data.HistoryDrink;
import de.nimax.nimax_cocktails.mixing.MixingActivity;

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
     * The date formatter for simple date
     */
    DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    /**
     * The date formatter for simple time
     */
    DateFormat timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
    /**
     * The date value for today
     */
    String today;

    /**
     * Custom Array Adapter for the list and the spinners
     *
     * @param activity of the adapter
     */
    DrinkerEditAdapter(@NonNull Activity activity, ArrayList<HistoryDrink> drinks) {
        super(activity, 0, drinks);
        this.activity = activity;
        this.drinks = drinks;
        today = dateFormat.format(new Date());
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
        try {
            Date date = HistoryDrink.dateFormat.parse(drinks.get(position).date);
            if (date == null) {
                time.setText("-");
            } else {
                if (dateFormat.format(date).equals(today)) {
                    time.setText(timeFormat.format(date));
                } else {
                    time.setText(dateFormat.format(date));
                }
            }
        } catch (ParseException e) {
            time.setText("-");
        }

        // Specify the icon
        ImageView image = item.findViewById(R.id.list_image);
        if (drinks.get(position).image != null) {
            image.setImageBitmap(drinks.get(position).image);
        } else {
            image.setImageResource(R.drawable.icon_mixing);
        }

        // Specify the name
        TextView name = item.findViewById(R.id.list_name);
        name.setText(drinks.get(position).name);

        // Specify the alcohol
        TextView alcohol = item.findViewById(R.id.list_description);
        String amount = activity.getString(R.string.drinkers_amount) + " " + drinks.get(position).amount + " ml";
        String alcoholContent = activity.getString(R.string.drinkers_alcohol) + " " + drinks.get(position).alcohol + " %";
        String text = amount + " " + activity.getString(R.string.point) + " " + alcoholContent;
        alcohol.setText(text);

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
