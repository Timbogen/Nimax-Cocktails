package de.nimax.nimax_cocktails.settings.setup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.CarouselView;

import java.util.ArrayList;

import de.nimax.nimax_cocktails.BluetoothService;
import de.nimax.nimax_cocktails.mixing.AmountViewListener;
import de.nimax.nimax_cocktails.mixing.DrinkViewListener;
import de.nimax.nimax_cocktails.recipes.data.Bar;
import de.nimax.nimax_cocktails.recipes.data.Drink;
import de.nimax.nimax_cocktails.recipes.data.Recipe;
import de.nimax.nimax_cocktails.recipes.edit.RecipeEditAdapter;

public class SetupActivity extends AppCompatActivity {

    /**
     * The drinks of the setup
     */
    public static ArrayList<Drink> drinks;
    /**
     * The drinks for modification
     */
    public static Bar.Drinks modificationDrinks;
    /**
     * The spinner adapter
     */
    private RecipeEditAdapter spinnerAdapter;
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
        setupCarousel();
        setupSelection();
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
        spinnerAdapter = new RecipeEditAdapter(this, drinks);
        list.setAdapter(spinnerAdapter);
        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update position
                selected = position;
                // Select the right items
                CarouselView selectionCarousel = findViewById(R.id.setup_modify_carousel);
                selectionCarousel.setCurrentItem(Bar.Drinks.getPosition(drinks.get(position)));
                CarouselView amountCarousel = findViewById(R.id.setup_modify_spinner);
                amountCarousel.setCurrentItem(drinks.get(selected).amount / 10);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Method to setup the carousel
     */
    private void setupCarousel() {
        CarouselView selectionCarousel = findViewById(R.id.setup_modify_carousel);
        selectionCarousel.setPageCount(modificationDrinks.drinks.length);
        selectionCarousel.setViewListener(new DrinkViewListener(modificationDrinks, this));
    }

    /**
     * Method to setup the selection for the spinners
     */
    private void setupSelection() {
        // Generate the possible values for the spinners
        int[] values = new int[Recipe.MAX_BOTTLE / 10];
        for (int i = 0; i < values.length; i++) {
            values[i] = i * 10;
        }
        // Get and modify the non alc spinner
        CarouselView amountCarousel = findViewById(R.id.setup_modify_spinner);
        amountCarousel.setPageCount(values.length);
        amountCarousel.setCurrentItem(drinks.get(selected).amount / 10);
        amountCarousel.setViewListener(new AmountViewListener(values, this));
    }


    /**
     * Method to start the selected pump
     * @param view that was clicked
     */
    public void startPump(View view) {
        // Hide the play setting
        view.setVisibility(View.INVISIBLE);
        // Show the pause settings
        findViewById(R.id.setup_pause).setVisibility(View.VISIBLE);
    }

    /**
     * Method to stop the selected pump
     * @param view that was clicked
     */
    public void stopPump(View view) {
        // Hide the play setting
        view.setVisibility(View.INVISIBLE);
        // Show the pause settings
        findViewById(R.id.setup_play).setVisibility(View.VISIBLE);
    }

    /**
     * Method to lower the selected item of the carousel
     * @param v view
     */
    public void lowerAmount(View v) {
        CarouselView carousel = findViewById(R.id.setup_modify_spinner);
        if (carousel.getCurrentItem() - 10 < 0) carousel.setCurrentItem(0);
        else carousel.setCurrentItem(carousel.getCurrentItem() - 10);
    }

    /**
     * Method to higher the selected item of the carousel
     * @param v view
     */
    public void higherAmount(View v) {
        CarouselView carousel = findViewById(R.id.setup_modify_spinner);
        if (carousel.getCurrentItem() + 10 >= carousel.getPageCount()) carousel.setCurrentItem(carousel.getPageCount() - 1);
        else carousel.setCurrentItem(carousel.getCurrentItem() + 10);
    }

    /**
     * Method to confirm the modification
     * @param v view
     */
    public void confirmModification(View v) {
        // Disable the view
        v.setEnabled(false);

        // Signalize modification action
        BluetoothService.sendData("MODIFY");

        // Send the position
        if (drinks.get(selected).alcohol <= 0) {
            BluetoothService.sendData(Integer.toString(selected));
        } else {
            BluetoothService.sendData(Integer.toString(selected + 12));
        }

        // Send the id
        CarouselView selectionCarousel = findViewById(R.id.setup_modify_carousel);
        int position = selectionCarousel.getCurrentItem();
        BluetoothService.sendData(Integer.toString(position));

        // Send the amount
        CarouselView amountCarousel = findViewById(R.id.setup_modify_spinner);
        int amount = amountCarousel.getCurrentItem() * 10;
        BluetoothService.sendData(Integer.toString(amount));

        // Update the value
        drinks.set(selected, new Drink(modificationDrinks.drinks[position]));
        drinks.get(selected).amount = amount;
        // Update the spinner
        spinnerAdapter.notifyDataSetChanged();

        // Toast to notify the user
        BluetoothService.makeToast(this, getString(R.string.settings_setup_value_changed));

        // Enable the view
        v.setEnabled(true);
    }
}
