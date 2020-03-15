package de.nimax.nimax_cocktails.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import de.nimax.nimax_cocktails.BluetoothService;
import de.nimax.nimax_cocktails.drinkers.data.Administration;
import de.nimax.nimax_cocktails.drinkers.edit.DrinkerEditActivity;
import de.nimax.nimax_cocktails.menu.MenuActivity;
import de.nimax.nimax_cocktails.menu.Showcase;
import de.nimax.nimax_cocktails.recipes.data.Drink;
import de.nimax.nimax_cocktails.settings.setup.SetupActivity;

import com.nimax.nimax_cocktails.R;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    /**
     * All the non alcoholic drinks that are set up in the machine
     */
    public static ArrayList<Drink> nonAlcDrinks = new ArrayList<>();
    /**
     * All the alcoholic drinks that are set up in the machine
     */
    public static ArrayList<Drink> alcDrinks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        // Deactivate the old transition
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);

        // Update the text of the bluetooth settings state
        if (BluetoothService.isConnected()) {
            TextView status = findViewById(R.id.settings_bluetooth_status);
            status.setText(getString(R.string.bluetooth_status_connected));
            // Make the other settings visible
            findViewById(R.id.settings_non).setVisibility(View.VISIBLE);
            findViewById(R.id.settings_alc).setVisibility(View.VISIBLE);
        }

        // Showcase
        Showcase.setupShowcase(this, Showcase.SETTINGS, null, getString(R.string.showcase_settings_start), null);
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
     * @param v view that was clicked
     */
    public void showNonAlcoholicSetup(View v) {
        // Set the right data set
        SetupActivity.drinks = nonAlcDrinks;
        // Start the intent
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Method to show the alcoholic setup
     * @param v view that was clicked
     */
    public void showAlcoholicSetup(View v) {
        // Set the right data set
        SetupActivity.drinks = alcDrinks;
        // Start the intent
        Intent intent = new Intent(this, SetupActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}
