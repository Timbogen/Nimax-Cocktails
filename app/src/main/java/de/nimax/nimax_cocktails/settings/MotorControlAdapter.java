package de.nimax.nimax_cocktails.settings;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nimax.nimax_cocktails.R;

import de.nimax.nimax_cocktails.shared.BluetoothService;

public class MotorControlAdapter {

    /**
     * The activity of the activity
     */
    private final Activity activity;

    /**
     * Constructor
     *
     * @param activity The current activity
     * @param list     The list view
     * @param actions  The actions of the list
     */
    public MotorControlAdapter(@NonNull Activity activity, LinearLayout list, String... actions) {
        this.activity = activity;
        for (String action : actions) {
            addView(action, list);
        }
    }

    /**
     * Prepare the view for a given position
     *
     * @param action The action of the item
     * @param list   The target list view
     */
    public void addView(final String action, LinearLayout list) {
        // Check if the item is null
        View item = LayoutInflater.from(activity).inflate(
                R.layout.settings_button,
                list,
                false
        );

        // Set the logo
        ImageView logo = item.findViewById(R.id.setting_logo);
        logo.setImageResource(R.drawable.icon_play);

        // Set the title
        TextView title = item.findViewById(R.id.setting_title);
        String text = getString("settings_mc_" + action);
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
                        changeMotorAction(action, view);
                    }
                }).start();

            }
        });

        // Add the item
        list.addView(item);
    }

    /**
     * Activate or deactivate the motor
     *
     * @param action of the item
     * @param item   corresponding to the item
     */
    private void changeMotorAction(final String action, final View item) {
        // Start the loading spinner
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                item.findViewById(R.id.setting_loading).setVisibility(View.VISIBLE);
            }
        });
        // Signalize the arduino and wait for response
        BluetoothService.sendData("MANUAL_CONTROL");
        BluetoothService.sendData(action);
        final String response = BluetoothService.readData();

        // Change the layout and check if the motor is
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView logo = item.findViewById(R.id.setting_logo);
                TextView description = item.findViewById(R.id.setting_description);
                if (!response.equals("PAUSE_AVAILABLE")) {
                    // Set the logo
                    logo.setImageResource(R.drawable.icon_play);

                    // Set the description
                    String text = activity.getString(R.string.settings_mc_play);
                    description.setText(text);
                } else {
                    // Set the logo
                    logo.setImageResource(R.drawable.icon_pause);

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
