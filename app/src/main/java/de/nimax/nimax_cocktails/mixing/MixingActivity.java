package de.nimax.nimax_cocktails.mixing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import de.nimax.nimax_cocktails.MenuActivity;
import de.nimax.nimax_cocktails.recipes.data.Drinks;
import de.nimax.nimax_cocktails.recipes.data.Mix;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
public class MixingActivity extends AppCompatActivity {

    /**
     * The current mix
     */
    private Mix mix = new Mix("Unnamed");
    /**
     * The current activity
     */
    private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixing);
        // Deactivate the old transition
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);
        setupCarousel();
        setupSelection();
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
    }

    /**
     * Method to setup the carousel
     */
    private void setupCarousel() {
        // The anti alc carousel
        CarouselView nonAlcCarousel = findViewById(R.id.carousel_anti_alc);
        nonAlcCarousel.setPageCount(Drinks.NON_ALC.drinks.length);
        nonAlcCarousel.setViewListener(new DrinkViewListener(Drinks.NON_ALC, this));
        nonAlcCarousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                CarouselView carousel = findViewById(R.id.carousel_amount_non_alc);
                mix.addDrink(Drinks.NON_ALC.drinks[position], (carousel.getCurrentItem() + 1) * 20, activity);
                refreshTable();
            }
        });

        // The alc carousel
        CarouselView alcCarousel = findViewById(R.id.carousel_alc);
        alcCarousel.setPageCount(Drinks.ALC.drinks.length);
        alcCarousel.setViewListener(new DrinkViewListener(Drinks.ALC, this));
        alcCarousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                CarouselView carousel = findViewById(R.id.carousel_amount_alc);
                mix.addDrink(Drinks.ALC.drinks[position], (carousel.getCurrentItem() + 1) * 20, activity);
                refreshTable();
            }
        });
    }

    /**
     * Method to setup the selection for the spinners
     */
    private void setupSelection() {
        // Generate the possible values for the spinners
        int[] values = new int[Mix.MAX_AMOUNT / 20];
        for (int i = 0; i < values.length; i++) {
            values[i] = (i + 1) * 20;
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
        // Check if the mix is empty
        View underline = findViewById(R.id.mixing_table_underline);
        if (mix.isEmpty()) {
            underline.setVisibility(View.INVISIBLE);
            return;
        }
        underline.setVisibility(View.VISIBLE);
        // Fill the table
        for (int i = 0; i < mix.drinks.size(); i++) {
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
            drink.setText(mix.drinks.get(i).name);

            // Modify the amount text view
            amount.setTextSize(20);
            String text = mix.drinks.get(i).amount + " " + getString(R.string.mixing_ml);
            amount.setText(text);

            // Modify the delete button
            delete.setImageResource(R.drawable.mixing_button_delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mix.removeDrink(which);
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
