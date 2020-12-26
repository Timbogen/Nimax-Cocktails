package de.nimax.nimax_cocktails.mixing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;

import java.util.Objects;

import de.nimax.nimax_cocktails.shared.BluetoothService;
import de.nimax.nimax_cocktails.menu.MenuActivity;
import de.nimax.nimax_cocktails.menu.Showcase;
import de.nimax.nimax_cocktails.recipes.data.Drinks;
import de.nimax.nimax_cocktails.recipes.data.Recipe;

public class MixingActivity extends AppCompatActivity {

    /**
     * The current drink
     */
    public static Recipe recipe = new Recipe("Mix");
    /**
     * True if the machine is currently mixing
     */
    private static boolean mixing = false;
    /**
     * The current activity
     */
    private final Activity activity = this;
    /**
     * The list view for the current mix
     */
    private RecyclerView mixView;

    /**
     * Create the view
     */
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
        setupMixView();

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
                    this,
                    getString(R.string.bluetooth_no_active_connection),
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
                        this,
                        getString(R.string.bluetooth_no_drink) + " " + getString(resourceId),
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
        if (carousel.getCurrentItem() + 5 >= carousel.getPageCount())
            carousel.setCurrentItem(carousel.getPageCount() - 1);
        else carousel.setCurrentItem(carousel.getCurrentItem() + 5);
    }

    /**
     * Method to lower the selected item of the carousel
     *
     * @param v view
     */
    public void lowerAmountNonAlc(View v) {
        CarouselView carousel = findViewById(R.id.carousel_amount_non_alc);
        carousel.setCurrentItem(Math.max(carousel.getCurrentItem() - 5, 0));
    }

    /**
     * Method to higher the selected item of the carousel
     *
     * @param v view
     */
    public void higherAmountAlc(View v) {
        CarouselView carousel = findViewById(R.id.carousel_amount_alc);
        if (carousel.getCurrentItem() + 5 >= carousel.getPageCount())
            carousel.setCurrentItem(carousel.getPageCount() - 1);
        else carousel.setCurrentItem(carousel.getCurrentItem() + 5);
    }

    /**
     * Method to lower the selected item of the carousel
     *
     * @param v view
     */
    public void lowerAmountAlc(View v) {
        CarouselView carousel = findViewById(R.id.carousel_amount_alc);
        carousel.setCurrentItem(Math.max(carousel.getCurrentItem() - 5, 0));
    }

    /**
     * Method to setup the carousel
     */
    private void setupCarousel() {
        // The anti alc carousel
        CarouselView nonAlcCarousel = findViewById(R.id.carousel_non_alc);
        nonAlcCarousel.setPageCount(Drinks.NON_ALC.size());
        DrinkViewListener nonAlcListener = new DrinkViewListener(Drinks.NON_ALC, this);
        nonAlcCarousel.setViewListener(nonAlcListener);
        nonAlcCarousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                CarouselView carousel = findViewById(R.id.carousel_amount_non_alc);
                if (recipe.addDrink(Drinks.NON_ALC.get(position), (carousel.getCurrentItem() + 1) * 10, activity)) {
                    updateMixView();
                }
            }
        });

        // The alc carousel
        CarouselView alcCarousel = findViewById(R.id.carousel_alc);
        alcCarousel.setPageCount(Drinks.ALC.size());
        DrinkViewListener alcListener = new DrinkViewListener(Drinks.ALC, this);
        alcCarousel.setViewListener(alcListener);
        alcCarousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                CarouselView carousel = findViewById(R.id.carousel_amount_alc);
                if (recipe.addDrink(Drinks.ALC.get(position), (carousel.getCurrentItem() + 1) * 10, activity)) {
                    updateMixView();
                }
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
        nonAlc.setCurrentItem(6);
        nonAlc.setViewListener(new AmountViewListener(values, this));

        // Get and modify the non alc spinner
        CarouselView alc = findViewById(R.id.carousel_amount_alc);
        alc.setPageCount(values.length);
        alc.setViewListener(new AmountViewListener(values, this));
    }

    /**
     * Draws the table which shows the current orders
     */
    private void setupMixView() {
        // Set up the recycler view
        mixView = findViewById(R.id.mixing_table);
        ItemTouchHelper touchHelper = new ItemTouchHelper(new MixAdapter.TouchHelper());
        touchHelper.attachToRecyclerView(mixView);
        MixAdapter mixAdapter = new MixAdapter(this, recipe, touchHelper);
        mixView.setAdapter(mixAdapter);

        // Show or hide the divider
        View underline = findViewById(R.id.mixing_table_underline);
        underline.setVisibility(recipe.drinks.size() == 0 ? View.GONE : View.VISIBLE);
    }

    /**
     * Update the mix view
     */
    private void updateMixView() {
        // Scroll to the bottom
        Objects.requireNonNull(mixView.getAdapter()).notifyDataSetChanged();
        mixView.smoothScrollToPosition(recipe.drinks.size() - 1);

        // Show or hide the divider
        View underline = findViewById(R.id.mixing_table_underline);
        underline.setVisibility(recipe.drinks.size() == 0 ? View.GONE : View.VISIBLE);
    }
}
