package de.nimax.nimax_cocktails.mixing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;

import de.nimax.nimax_cocktails.BluetoothService;
import de.nimax.nimax_cocktails.menu.MenuActivity;
import de.nimax.nimax_cocktails.menu.Showcase;
import de.nimax.nimax_cocktails.recipes.data.Bar;
import de.nimax.nimax_cocktails.recipes.data.Drink;
import de.nimax.nimax_cocktails.recipes.data.Recipe;
import de.nimax.nimax_cocktails.settings.SettingsActivity;

public class MixingActivity extends AppCompatActivity {

    /**
     * The current drinker
     */
    public static Recipe recipe = new Recipe("Mix");
    /**
     * The drink format for mixing a cocktail on the machine
     */
    private static int[] antiAlc, alc;
    /**
     * True if the machine is currently mixing
     */
    private static boolean mixing = false;
    /**
     * The current activity
     */
    private Activity activity = this;
    /**
     * The drink carousels
     */
    private DrinkViewListener alcCarousel, nonAlcCarousel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixing);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        // Deactivate the old transition
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);
        setupCarousel();
        setupSelection();
        refreshTable();
        // Setup the showcases for first use
        setupShowcases();
    }

    /**
     * Mix the recipe
     */
    public void mixRecipe(View v) {
        // Check if the machine is currently mixing
        if (mixing) {
            Toast.makeText(
                    this,
                    getString(R.string.bluetooth_currently_mixing),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Check if the recipe is empty
        if (recipe.isEmpty()) {
            Toast.makeText(
                    this,
                    getString(R.string.mixing_no_drinks),
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
                        BluetoothService.sendData("MIX_DRINK");
                        // Send the cocktail data
                        for (int i = 0; i < alc.length; i++) {
                            // Send the data
                            BluetoothService.sendData(Integer.toString(alc[i]));
                            // Update the local amounts
                            SettingsActivity.alcDrinks.get(i).amount -= alc[i];
                        }
                        for (int i = 0; i < antiAlc.length; i++) {
                            BluetoothService.sendData(Integer.toString(antiAlc[i]));
                            // Update the local amounts
                            SettingsActivity.nonAlcDrinks.get(i).amount -= antiAlc[i];
                        }

                        // Update the views
                        alcCarousel.update();
                        nonAlcCarousel.update();

                        // Wait for the arduino to respond
                        if (BluetoothService.readData().equals("FINISHED")) {
                            mixing = false;
                        }
                    }
                }).start();
            }
        } else {
            Toast.makeText(
                    this,
                    getString(R.string.bluetooth_no_active_connection),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    /**
     * @return true if a cocktail is mixable
     */
    private boolean isMixable() {
        // Empty the previous cocktail
        antiAlc = new int[]{0, 0, 0, 0, 0, 0};
        alc = new int[]{0, 0, 0, 0, 0, 0};

        // Check if machine has enough alc
        for (Drink recipeDrink : recipe.drinks) {
            // Check if machine contains the drink
            int position = -1;
            Drink matchedDrink = null;
            for (int i = 0; i < SettingsActivity.alcDrinks.size(); i++) {
                if (SettingsActivity.alcDrinks.get(i).name.equals(recipeDrink.name)) {
                    position = i;
                    matchedDrink = SettingsActivity.alcDrinks.get(i);
                    break;
                }
            }

            // Check if the amount can be set
            if (position > -1 && matchedDrink.amount >= recipeDrink.amount) {
                alc[position] = recipeDrink.amount;
                continue;
            }

            for (int i = 0; i < SettingsActivity.nonAlcDrinks.size(); i++) {
                if (SettingsActivity.nonAlcDrinks.get(i).name.equals(recipeDrink.name)) {
                    position = i;
                    matchedDrink = SettingsActivity.nonAlcDrinks.get(i);
                    break;
                }
            }

            // Check if the amount can be set
            if (position > -1 && matchedDrink.amount >= recipeDrink.amount) {
                antiAlc[position] = recipeDrink.amount;
            }

            // Check if there's enough
            if (matchedDrink == null) {
                Toast.makeText(
                        this,
                        getString(R.string.bluetooth_no_drink) + " " + recipeDrink.name + ".",
                        Toast.LENGTH_SHORT
                ).show();
                return false;
            } else if (matchedDrink.amount < recipeDrink.amount) {
                Toast.makeText(
                        this,
                        getString(R.string.bluetooth_too_less_1) + " " + matchedDrink.amount
                                + " ml " + matchedDrink.name + " "
                                + getString(R.string.bluetooth_too_less_2),
                        Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        }
        return true;
    }

    /**
     * Method to setup the showcases
     */
    private void setupShowcases() {
        // Setup the delete showcase
        final Showcase.Next mix = new Showcase.Next() {
            @Override
            public void show() {
                Showcase.setupShowcase(activity, null, findViewById(R.id.mixing_play), getString(R.string.showcase_mixing_mix), null);
            }
        };
        // Setup the photo showcase
        Showcase.Next save = new Showcase.Next() {
            @Override
            public void show() {
                Showcase.setupShowcase(activity, null, findViewById(R.id.mixing_save), getString(R.string.showcase_mixing_save), mix);
            }
        };
        Showcase.setupShowcase(this, Showcase.MIXING, findViewById(R.id.setup_modify_carousel), getString(R.string.showcase_mixing_add), save);
    }

    /**
     * Override the back buttons function
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        // The layout
        Pair<View, String> layout = new Pair<>(
                findViewById(R.id.mixing_layout), getString(R.string.transition_mixing_layout));
        // The image
        Pair<View, String> logo = new Pair<>(
                findViewById(R.id.mixing_logo), getString(R.string.transition_mixing_logo));
        // The title
        Pair<View, String> title = new Pair<>(
                findViewById(R.id.mixing_title), getString(R.string.transition_mixing_title));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, layout, logo, title);
        startActivity(intent, options.toBundle());
        finishAffinity();
    }

    /**
     * Method for opening a dialog to save recipes
     */
    public void saveRecipe(View v) {
        SavingDialog dialog = new SavingDialog(this);
        dialog.show();
    }

    /**
     * Method to higher the selected item of the carousel
     *
     * @param v view
     */
    public void higherAmountNonAlc(View v) {
        CarouselView carousel = findViewById(R.id.carousel_amount_non_alc);
        if (carousel.getCurrentItem() + 10 >= carousel.getPageCount())
            carousel.setCurrentItem(carousel.getPageCount() - 1);
        else carousel.setCurrentItem(carousel.getCurrentItem() + 10);
    }

    /**
     * Method to lower the selected item of the carousel
     *
     * @param v view
     */
    public void lowerAmountNonAlc(View v) {
        CarouselView carousel = findViewById(R.id.carousel_amount_non_alc);
        if (carousel.getCurrentItem() - 10 < 0) carousel.setCurrentItem(0);
        else carousel.setCurrentItem(carousel.getCurrentItem() - 10);
    }

    /**
     * Method to higher the selected item of the carousel
     *
     * @param v view
     */
    public void higherAmountAlc(View v) {
        CarouselView carousel = findViewById(R.id.carousel_amount_alc);
        if (carousel.getCurrentItem() + 10 >= carousel.getPageCount())
            carousel.setCurrentItem(carousel.getPageCount() - 1);
        else carousel.setCurrentItem(carousel.getCurrentItem() + 10);
    }

    /**
     * Method to lower the selected item of the carousel
     *
     * @param v view
     */
    public void lowerAmountAlc(View v) {
        CarouselView carousel = findViewById(R.id.carousel_amount_alc);
        if (carousel.getCurrentItem() - 10 < 0) carousel.setCurrentItem(0);
        else carousel.setCurrentItem(carousel.getCurrentItem() - 10);
    }

    /**
     * Method to setup the carousel
     */
    private void setupCarousel() {
        // The anti alc carousel
        CarouselView nonAlcCarousel = findViewById(R.id.carousel_non_alc);
        nonAlcCarousel.setPageCount(Bar.Drinks.NON_ALC.drinks.length);
        this.nonAlcCarousel = new DrinkViewListener(Bar.Drinks.NON_ALC, this);
        nonAlcCarousel.setViewListener(this.nonAlcCarousel);
        nonAlcCarousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                CarouselView carousel = findViewById(R.id.carousel_amount_non_alc);
                recipe.addDrink(Bar.Drinks.NON_ALC.drinks[position], (carousel.getCurrentItem() + 1) * 10, activity);
                refreshTable();
            }
        });

        // The alc carousel
        CarouselView alcCarousel = findViewById(R.id.carousel_alc);
        alcCarousel.setPageCount(Bar.Drinks.ALC.drinks.length);
        this.alcCarousel = new DrinkViewListener(Bar.Drinks.ALC, this);
        alcCarousel.setViewListener(this.alcCarousel);
        alcCarousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                CarouselView carousel = findViewById(R.id.carousel_amount_alc);
                recipe.addDrink(Bar.Drinks.ALC.drinks[position], (carousel.getCurrentItem() + 1) * 10, activity);
                refreshTable();
            }
        });
    }

    /**
     * Method to setup the selection for the spinners
     */
    private void setupSelection() {
        // Generate the possible values for the spinners
        int[] values = new int[Recipe.MAX_AMOUNT / 10];
        for (int i = 0; i < values.length; i++) {
            values[i] = (i + 1) * 10;
        }
        // Get and modify the non alc spinner
        CarouselView nonAlc = findViewById(R.id.carousel_amount_non_alc);
        nonAlc.setPageCount(values.length);
        nonAlc.setCurrentItem(values.length / 2);
        nonAlc.setViewListener(new AmountViewListener(values, this));

        // Get and modify the non alc spinner
        CarouselView alc = findViewById(R.id.carousel_amount_alc);
        alc.setPageCount(values.length);
        alc.setViewListener(new AmountViewListener(values, this));
    }

    /**
     * Draws the table which shows the current orders
     */
    private void refreshTable() {
        // Get the table
        TableLayout table = findViewById(R.id.mixing_table);
        table.removeAllViews();
        // Check if the drinker is empty
        View underline = findViewById(R.id.mixing_table_underline);
        if (recipe.isEmpty()) {
            underline.setVisibility(View.INVISIBLE);
            return;
        }
        underline.setVisibility(View.VISIBLE);
        // Fill the table
        for (int i = 0; i < recipe.drinks.size(); i++) {
            // Declare the variables
            final int which = i;

            TableRow tr = new TableRow(this);

            TextView drink = new TextView(this);
            drink.setTextColor(getResources().getColor(R.color.black));
            TextView amount = new TextView(this);
            amount.setTextColor(getResources().getColor(R.color.black));
            ImageView delete = new ImageView(this);

            // Modify the drink text view
            drink.setTextSize(20);
            drink.setText(recipe.drinks.get(i).name);

            // Modify the amount text view
            amount.setTextSize(20);
            String text = recipe.drinks.get(i).amount + " " + getString(R.string.mixing_ml);
            amount.setText(text);

            // Modify the delete button
            delete.setImageResource(R.drawable.mixing_button_delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recipe.removeDrink(which);
                    refreshTable();
                }
            });
            delete.setLayoutParams(new TableRow.LayoutParams(150, 75));

            // Modify the table row
            tr.setPadding(0, getDP(10), 0, getDP(10));
            tr.addView(drink);
            tr.addView(amount);
            tr.addView(delete);

            // Add table row to table
            table.addView(tr);
        }
        // Scroll down the scrollview
        final ScrollView scroll = findViewById(R.id.mixing_table_scroll);
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    /**
     * Method to convert a specification in pixels to dp
     *
     * @param pixels expects a specification in pixels
     * @return returns the dp
     */
    private int getDP(int pixels) {
        // Get the scale
        final float scale = this.getResources().getDisplayMetrics().density;
        // Return the value
        return (int) (pixels * scale + 0.5f);
    }
}
