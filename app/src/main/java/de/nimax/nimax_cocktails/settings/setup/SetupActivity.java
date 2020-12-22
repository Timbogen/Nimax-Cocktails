package de.nimax.nimax_cocktails.settings.setup;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.CarouselView;

import java.util.ArrayList;

import de.nimax.nimax_cocktails.BluetoothService;
import de.nimax.nimax_cocktails.mixing.DrinkViewListener;
import de.nimax.nimax_cocktails.recipes.data.Drinks;

public class SetupActivity extends AppCompatActivity {

    /**
     * The drinks of the setup
     */
    public static ArrayList<Drinks> drinks;
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
        // Setup the list
        Spinner list = findViewById(R.id.setup_drinks);
        spinnerAdapter = new SetupEditAdapter(this, drinks);
        list.setAdapter(spinnerAdapter);
        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update position
                selected = position;
                // Select the right items
                CarouselView selectionCarousel = findViewById(R.id.setup_modify_carousel);
                selectionCarousel.setCurrentItem(drinks.get(position).ordinal());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Method to setup the actions tab
     */
    private void setupActions() {
        if (!pumps) {
            ConstraintLayout actions = findViewById(R.id.actions_layout);
            actions.setVisibility(ConstraintLayout.GONE);
        }
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
        BluetoothService.sendData(Integer.toString(pumps ? selected : selected + 6));

        // Send the id
        CarouselView selectionCarousel = findViewById(R.id.setup_modify_carousel);
        int position = selectionCarousel.getCurrentItem();
        BluetoothService.sendData(Integer.toString(position));

        // Update the data
        drinks.set(selected, Drinks.ALL.get(position));

        // Update the spinner
        spinnerAdapter.notifyDataSetChanged();

        // Toast to notify the user
        BluetoothService.makeToast(this, getString(R.string.settings_setup_value_changed));

        // Enable the view
        v.setEnabled(true);
    }
}
