package de.nimax.nimax_cocktails.settings.setup;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.CarouselView;

import de.nimax.nimax_cocktails.shared.BluetoothService;
import de.nimax.nimax_cocktails.mixing.DrinkViewListener;
import de.nimax.nimax_cocktails.recipes.data.Drinks;
import de.nimax.nimax_cocktails.settings.SettingsButton;

public class SetupActivity extends AppCompatActivity {

    /**
     * The drinks of the setup
     */
    public static Drinks[] drinks;
    /**
     * True if the view modifies the pumps
     */
    public static boolean pumps = true;
    /**
     * The spinner adapter
     */
    private SetupEditAdapter spinnerAdapter;
    /**
     * The index of the selected drink
     */
    private int selected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        setupSpinner();
        setupActions();
        setupCarousel();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * Method to setup the list
     */
    private void setupSpinner() {
        // Set up the spinner
        Spinner list = findViewById(R.id.setup_drinks);
        spinnerAdapter = new SetupEditAdapter(this, drinks);
        list.setAdapter(spinnerAdapter);

        // Update the view if the user chose another item
        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = position;
                CarouselView selectionCarousel = findViewById(R.id.setup_modify_carousel);
                selectionCarousel.setCurrentItem(drinks[position].ordinal());
                updateActions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Set up the actions
     */
    private void setupActions() {
        SettingsButton button;
        if (pumps) button = findViewById(R.id.setup_change_shake);
        else button = findViewById(R.id.setup_play);
        button.setVisibility(SettingsButton.INVISIBLE);
        updateActions();
    }

    /**
     * Update the shake button
     */
    private void updateActions() {
        SettingsButton shakeMode = findViewById(R.id.setup_change_shake);
        String text = getString(BluetoothService.roundelShakeModes[selected] == 1 ?
                R.string.settings_setup_change_shake_on : R.string.settings_setup_change_shake_off);
        shakeMode.setDescription(text);
    }

    /**
     * Method to setup the carousel
     */
    private void setupCarousel() {
        CarouselView selectionCarousel = findViewById(R.id.setup_modify_carousel);
        selectionCarousel.setPageCount(Drinks.ALL.size());
        selectionCarousel.setViewListener(new DrinkViewListener(Drinks.ALL, this));
    }


    /**
     * Method to start the selected pump
     *
     * @param view that was clicked
     */
    public void startPump(View view) {
        // Hide the play setting
        view.setVisibility(View.INVISIBLE);
        // Show the pause settings
        findViewById(R.id.setup_pause).setVisibility(View.VISIBLE);
        // Start pump manually
        BluetoothService.sendData("START_PUMP");
        BluetoothService.sendData(Integer.toString(selected));
    }

    /**
     * Method to stop the selected pump
     *
     * @param view that was clicked
     */
    public void stopPump(View view) {
        // Hide the play setting
        view.setVisibility(View.INVISIBLE);
        // Show the pause settings
        findViewById(R.id.setup_play).setVisibility(View.VISIBLE);
        // Start pump manually
        BluetoothService.sendData("STOP_PUMP");
        BluetoothService.sendData(Integer.toString(selected));
    }

    /**
     * Change the shake mode for a position on the roundel
     *
     * @param v The view that was clicked
     */
    public void changeShakeMode(View v) {
        v.setEnabled(false);

        // Change the mode
        BluetoothService.sendData("CHANGE_SHAKE_MODE");
        BluetoothService.sendData(Integer.toString(selected));
        BluetoothService.roundelShakeModes[selected] = Integer.parseInt(BluetoothService.readData());
        updateActions();

        v.setEnabled(true);
    }

    /**
     * Method to confirm the modification
     *
     * @param v view
     */
    public void confirmModification(View v) {
        // Disable the view
        v.setEnabled(false);

        // Signalize modification action
        BluetoothService.sendData("MODIFY");

        // Send the position
        BluetoothService.sendData(Integer.toString(pumps ? selected : selected + BluetoothService.DRINK_COUNT));

        // Send the id
        CarouselView selectionCarousel = findViewById(R.id.setup_modify_carousel);
        int position = selectionCarousel.getCurrentItem();
        BluetoothService.sendData(Integer.toString(position));

        // Update the data
        drinks[selected] = Drinks.ALL.get(position);

        // Update the spinner
        spinnerAdapter.notifyDataSetChanged();

        // Toast to notify the user
        Toast.makeText(
                this,
                getString(R.string.settings_setup_value_changed),
                Toast.LENGTH_SHORT
        ).show();

        // Enable the view
        v.setEnabled(true);
    }
}
