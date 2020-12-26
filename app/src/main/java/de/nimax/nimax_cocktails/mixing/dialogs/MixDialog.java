package de.nimax.nimax_cocktails.mixing.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.nimax.nimax_cocktails.R;

import java.util.ArrayList;
import java.util.Objects;

import de.nimax.nimax_cocktails.drinkers.adapters.DrinkersAdapter;
import de.nimax.nimax_cocktails.drinkers.data.Administration;
import de.nimax.nimax_cocktails.drinkers.data.Drinker;
import de.nimax.nimax_cocktails.recipes.data.Recipe;
import de.nimax.nimax_cocktails.shared.BluetoothService;

public class MixDialog extends Dialog {

    /**
     * True if the machine is currently mixing
     */
    private static boolean mixing = false;
    /**
     * The selected drinker
     */
    private static int selectedDrinker = 0;
    /**
     * The text of the dialog
     */
    private final Activity activity;
    /**
     * The current recipe
     */
    private final Recipe recipe;

    /**
     * Dialog for confirming an action
     *
     * @param activity Context in which the dialog should be opened
     */
    public MixDialog(@NonNull Activity activity, Recipe recipe) {
        super(activity, R.style.DialogTheme);
        this.activity = activity;
        this.recipe = recipe;
    }

    /**
     * Create the dialog
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mix);
        Objects.requireNonNull(getWindow()).setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        setupDialog();
    }

    /**
     * Method to setup the dialog
     */
    private void setupDialog() {
        // Extend the user list with the standard
        final ArrayList<Drinker> drinkers = new ArrayList<>();
        drinkers.add(new Drinker(activity.getString(R.string.mixing_default_drinker)));
        drinkers.addAll(Administration.drinkers);

        // Save the drinker on confirm
        Button save = findViewById(R.id.dialog_confirm);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMixing(drinkers.get(selectedDrinker));
            }
        });

        // Close the dialog on cancel
        Button cancel = findViewById(R.id.dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        // Set up the spinner
        Spinner spinner = findViewById(R.id.spinner_drinkers);
        spinner.setAdapter(new DrinkersAdapter(activity, drinkers, false));

        // Update the view if the user chose another item
        spinner.setSelection(selectedDrinker < drinkers.size() ? selectedDrinker : drinkers.size() - 1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDrinker = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Actually try to start mixing the cocktail
     *
     * @param drinker The drinker that ordered the drink
     */
    private void startMixing(final Drinker drinker) {
        // Check if the machine is currently mixing
        if (mixing) {
            Toast.makeText(
                    activity,
                    activity.getString(R.string.bluetooth_currently_mixing),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Check if the recipe is empty
        if (recipe.isEmpty()) {
            Toast.makeText(
                    activity,
                    activity.getString(R.string.mixing_no_drinks),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Check if the connection is active
        if (BluetoothService.isConnected()) {
            if (isMixable()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mixing = true;
                        drinker.addDrink(recipe);
                        BluetoothService.sendData("MIX_DRINK");

                        // Close the dialog and add the recipe to the drinker's record
                        closeDialog();

                        // Send the cocktail data
                        BluetoothService.sendData(Integer.toString(recipe.drinks.size()));
                        for (Recipe.Drink drink : recipe.drinks) {
                            BluetoothService.sendData(
                                    Integer.toString(BluetoothService.getIndex(drink.drink))
                            );
                            BluetoothService.sendData(Integer.toString(drink.amount));
                        }

                        // Wait for the arduino to respond
                        if (BluetoothService.readData().equals("FINISHED")) {
                            mixing = false;
                        }
                    }
                }).start();
            }
        } else {
            Toast.makeText(
                    activity,
                    activity.getString(R.string.bluetooth_no_active_connection),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * Check if a cocktail is mixable
     *
     * @return True if a cocktail is mixable
     */
    private boolean isMixable() {
        for (Recipe.Drink drink : recipe.drinks) {
            int result = BluetoothService.getIndex(drink.drink);
            if (result == BluetoothService.NOT_AVAILABLE) {
                int resourceId = activity.getResources().getIdentifier(
                        "drink_" + drink.drink.name().toLowerCase(),
                        "string",
                        activity.getPackageName()
                );
                Toast.makeText(
                        activity,
                        activity.getString(R.string.bluetooth_no_drink) + " " + activity.getString(resourceId),
                        Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        }
        return true;
    }

    /**
     * Close the dialog
     */
    private void closeDialog() {
        this.dismiss();
    }
}
