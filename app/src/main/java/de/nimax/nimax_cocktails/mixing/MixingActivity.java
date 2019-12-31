package de.nimax.nimax_cocktails.mixing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import de.nimax.nimax_cocktails.MenuActivity;
import de.nimax.nimax_cocktails.mixing.models.Drinks;
import de.nimax.nimax_cocktails.mixing.models.Mix;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;

public class MixingActivity extends AppCompatActivity {
    /**
     * Amount that is added to the mix on click on a non alc drink
     */
    private int addAmountNonAlc = 10;
    /**
     * Amount that is added to the mix on click on an alc drink
     */
    private int addAmountAlc = 10;
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
        CarouselView antiAlcCarousel = findViewById(R.id.carousel_anti_alc);
        antiAlcCarousel.setPageCount(Drinks.NON_ALC.drinks.length);
        antiAlcCarousel.setViewListener(new MixingViewListener(Drinks.NON_ALC, this));
        antiAlcCarousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                mix.addDrink(Drinks.NON_ALC.drinks[position], addAmountNonAlc, activity);
                refreshTable();
            }
        });

        // The alc carousel
        CarouselView alcCarousel = findViewById(R.id.carousel_alc);
        alcCarousel.setPageCount(Drinks.ALC.drinks.length);
        alcCarousel.setViewListener(new MixingViewListener(Drinks.ALC, this));
        alcCarousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                mix.addDrink(Drinks.ALC.drinks[position], addAmountAlc, activity);
                refreshTable();
            }
        });
    }

    /**
     * Method to setup the selection for the spinners
     */
    private void setupSelection() {
        // Generate the possible values for the spinners
        Integer[] values = new Integer[Mix.MAX_AMOUNT / 10];
        for (int i = 0; i < values.length; i++) {
            values[i] = (i + 1) * 10;
        }
        // Get and modify the non alc spinner
        Spinner antiAlc = findViewById(R.id.spinner_amount_non_alc);
        antiAlc.setAdapter(new ArrayAdapter<>(this, R.layout.adapter_amount, values));
        antiAlc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addAmountNonAlc = (position + 1) * 10;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Get and modify the non alc spinner
        Spinner alc = findViewById(R.id.spinner_amount_alc);
        alc.setAdapter(new ArrayAdapter<>(this, R.layout.adapter_amount, values));
        alc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addAmountAlc = (position + 1) * 10;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
