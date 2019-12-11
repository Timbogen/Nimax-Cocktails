package de.nimax.nimax_cocktails.mixing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.nimax.nimax_cocktails.MainActivity;
import de.nimax.nimax_cocktails.mixing.models.MixingType;

import com.nimax.nimax_cocktails.R;
import com.synnapps.carouselview.CarouselView;

public class MixingActivity extends AppCompatActivity {

    /**
     * Position of the selected anti alc drink
     */
    private int selectedAntiAlc;
    /**
     * Position of the selected alc drink
     */
    private int selectedAlc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixing);
        setupCarousel();
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
        antiAlcCarousel.setPageCount(MixingType.ANTI_ALC.drinks.length);
        antiAlcCarousel.setViewListener(new MixingViewListener(MixingType.ANTI_ALC, this));
        antiAlcCarousel.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectedAntiAlc = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        // The alc carousel
        CarouselView alcCarousel = findViewById(R.id.carousel_alc);
        alcCarousel.setPageCount(MixingType.ALC.drinks.length);
        alcCarousel.setViewListener(new MixingViewListener(MixingType.ALC, this));
        alcCarousel.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectedAlc = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
