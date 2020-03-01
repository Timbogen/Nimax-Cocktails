package de.nimax.nimax_cocktails.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import de.nimax.nimax_cocktails.BluetoothService;
import de.nimax.nimax_cocktails.menu.MenuActivity;
import de.nimax.nimax_cocktails.menu.Showcase;

import com.nimax.nimax_cocktails.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        // Deactivate the old transition
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);
        // Update the text of the bluetooth settings state
        TextView status = findViewById(R.id.settings_bluetooth_status);
        String text = BluetoothService.isConnected() ? getString(R.string.bluetooth_status_connected) : getString(R.string.bluetooth_status_disconnected);
        status.setText(text);
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
}
