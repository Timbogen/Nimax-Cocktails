package de.nimax.nimax_cocktails.mixing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import de.nimax.nimax_cocktails.MainActivity;
import de.nimax.nimax_cocktails.models.Bar;
import de.nimax.nimax_cocktails.models.Mix;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;

public class MixingActivity extends AppCompatActivity {

    /**
     * Max volume of a glass used for the machine
     */
    public static int MAX_AMOUNT = 350;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixing);
        setupCarousel();
        setupSelection();
    }

    /**
     * Override the back buttons function
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
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
        antiAlcCarousel.setPageCount(Bar.ANTI_ALC.drinks.length);
        antiAlcCarousel.setViewListener(new MixingViewListener(Bar.ANTI_ALC, this));
        antiAlcCarousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                mix.addDrink(Bar.ANTI_ALC.drinks[position], addAmountNonAlc);
                refreshTable();
            }
        });
        antiAlcCarousel.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        // The alc carousel
        CarouselView alcCarousel = findViewById(R.id.carousel_alc);
        alcCarousel.setPageCount(Bar.ALC.drinks.length);
        alcCarousel.setViewListener(new MixingViewListener(Bar.ALC, this));
        alcCarousel.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                mix.addDrink(Bar.ALC.drinks[position], addAmountAlc);
                refreshTable();
            }
        });
        alcCarousel.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setupSelection() {
        // Generate the possible values for the spinners
        Integer[] values = new Integer[MAX_AMOUNT / 10];
        for (int i = 0; i < values.length; i++) {
            values[i] = (i + 1) * 10;
        }
        // Get and modify the non alc spinner
        Spinner antiAlc = findViewById(R.id.spinner_amount_non_alc);
        antiAlc.setAdapter(new ArrayAdapter<>(this, R.layout.amount_spinner_item, values));
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
        alc.setAdapter(new ArrayAdapter<>(this, R.layout.amount_spinner_item, values));
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
        TableLayout table = findViewById(R.id.mixing_table_filled);
        table.removeAllViews();
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
