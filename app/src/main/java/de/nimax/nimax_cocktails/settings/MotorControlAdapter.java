package de.nimax.nimax_cocktails.settings;

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

import de.nimax.nimax_cocktails.BluetoothService;

public class MotorControlAdapter extends ArrayAdapter<String> {

    /**
     * The activity of the activity
     */
    private final Activity activity;
    /**
     * The drinks that should be shown in the list
     */
    private final String[] actions;

    /**
     * Custom Array Adapter for the list and the spinners
     *
     * @param activity of the adapter
     * @param actions  of the adapter
     */
    public MotorControlAdapter(@NonNull Activity activity, String... actions) {
        super(activity, 0, actions);
        this.activity = activity;
        this.actions = actions;
    }

    /**
     * Prepare the view for a given position
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View item, @NonNull ViewGroup parent) {
        // Check if the item is null
        if (item == null) {
            item = LayoutInflater.from(activity).inflate(
                    R.layout.settings_button,
                    parent,
                    false
            );
        }

        // Set the logo
        ImageView logo = item.findViewById(R.id.setting_logo);
        logo.setImageResource(R.drawable.mixing_button_play);

        // Set the title
        TextView title = item.findViewById(R.id.setting_title);
        String text = getString("settings_mc_" + actions[position]);
        title.setText(text);

        // Set the description
        TextView description = item.findViewById(R.id.setting_description);
        text = activity.getString(R.string.settings_mc_play);
        description.setText(text);

        // Add click method
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        changeMotorAction(position, view);
                    }
                }).start();

            }
        });
        // Return the item
        return item;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    /**
     * Activate or deactivate the motor
     *
     * @param position of the item
     * @param item     corresponding to the item
     */
    private void changeMotorAction(int position, final View item) {
        // Start the loading spinner
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                item.findViewById(R.id.setting_loading).setVisibility(View.VISIBLE);
            }
        });
        // Signalize the arduino and wait for response
        BluetoothService.sendData("MANUAL_CONTROL");
        BluetoothService.sendData(actions[position]);
        final String response = BluetoothService.readData();

        // Change the layout and check if the motor is
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView logo = item.findViewById(R.id.setting_logo);
                TextView description = item.findViewById(R.id.setting_description);
                if (!response.equals("PAUSE_AVAILABLE")) {
                    // Set the logo
                    logo.setImageResource(R.drawable.mixing_button_play);

                    // Set the description
                    String text = activity.getString(R.string.settings_mc_play);
                    description.setText(text);
                } else {
                    // Set the logo
                    logo.setImageResource(R.drawable.mixing_button_pause);

                    // Set the description
                    String text = activity.getString(R.string.settings_mc_pause);
                    description.setText(text);
                }
                // Stop the loading spinner
                item.findViewById(R.id.setting_loading).setVisibility(View.GONE);
            }
        });
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
