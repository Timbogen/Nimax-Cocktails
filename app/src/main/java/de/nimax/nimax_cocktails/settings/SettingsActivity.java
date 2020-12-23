package de.nimax.nimax_cocktails.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.nimax.nimax_cocktails.R;

import de.nimax.nimax_cocktails.BluetoothService;
import de.nimax.nimax_cocktails.menu.MenuActivity;
import de.nimax.nimax_cocktails.menu.Showcase;
import de.nimax.nimax_cocktails.settings.setup.SetupActivity;

public class SettingsActivity extends AppCompatActivity {

    /**
     * Method to setup settings
     *
     * @param activity that is currently active
     */
    public static void setupSettings(Activity activity) {
        SettingsButton bluetooth = activity.findViewById(R.id.settings_bluetooth);
        // Update the text of the bluetooth settings state
        if (BluetoothService.isConnected()) {
            bluetooth.setDescription(activity.getString(R.string.bluetooth_status_connected) + "  " + activity.getString(R.string.point) + "  " + activity.getString(R.string.bluetooth_connect));
            // Make the other settings visible
            activity.findViewById(R.id.settings_non).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.settings_alc).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.control_motors).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.control_motors_underline).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.list_motors).setVisibility(View.VISIBLE);
        } else {
            bluetooth.setDescription(activity.getString(R.string.bluetooth_status_disconnected) + "  " + activity.getString(R.string.point) + "  " + activity.getString(R.string.bluetooth_connect));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        // Deactivate the old transition
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);
        setupSettings(this);
        // Showcase
        Showcase.setupShowcase(this, Showcase.SETTINGS, null, getString(R.string.showcase_settings_start), null);

        // Setup the list
        ListView list = findViewById(R.id.list_motors);
        list.setAdapter(new MotorControlAdapter(this, "shift_up", "shift_down",
                "roundel_left", "roundel_initial", "roundel_right", "cup_left", "cup_middle",
                "cup_right"));
    }

    /**
     * Override the back buttons function
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        // The layout
        Pair<View, String> layout = new Pair<>(
                findViewById(R.id.settings_layout), getString(R.string.transition_settings_layout));
        // The image
        Pair<View, String> logo = new Pair<>(
                findViewById(R.id.settings_logo), getString(R.string.transition_settings_logo));
        // The title
        Pair<View, String> title = new Pair<>(
                findViewById(R.id.settings_title), getString(R.string.transition_settings_title));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, layout, logo, title);
        startActivity(intent, options.toBundle());
        finishAffinity();
    }

    /**
     * Method to open the bluetooth connect activity
     */
    public void connectToBluetooth(View v) {
        BluetoothService.connectDevice(this, v);
    }

    /**
     * Method to show the non alcoholic setup
     *
     * @param v view that was clicked
     */
    public void showNonAlcoholicSetup(View v) {
        // Set the right data set
        SetupActivity.drinks = BluetoothService.pumpDrinks;
        SetupActivity.pumps = true;
        // Start the intent
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Method to show the alcoholic setup
     *
     * @param v view that was clicked
     */
    public void showAlcoholicSetup(View v) {
        // Set the right data set
        SetupActivity.drinks = BluetoothService.roundelDrinks;
        SetupActivity.pumps = false;
        // Start the intent
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}
