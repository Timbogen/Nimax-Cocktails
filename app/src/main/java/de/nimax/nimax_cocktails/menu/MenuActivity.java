package de.nimax.nimax_cocktails.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nimax.nimax_cocktails.R;

import de.nimax.nimax_cocktails.drinkers.DrinkersActivity;
import de.nimax.nimax_cocktails.mixing.MixingActivity;
import de.nimax.nimax_cocktails.recipes.RecipesActivity;
import de.nimax.nimax_cocktails.settings.SettingsActivity;

public class MenuActivity extends AppCompatActivity {


    /**
     * All the external libraries are documented in build.gradle (Module: app)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        // Deactivate the old transition
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);
        // Setup the showcases for first use
        setupShowcases();
    }

    /**
     * Method to setup the showcases
     */
    private void setupShowcases() {
        final Activity activity = this;
        // Setup the settings showcase
        final Showcase.Next settings = new Showcase.Next() {
            @Override
            public void show() {
                Showcase.setupShowcase(activity, null, findViewById(R.id.menu_button_settings), getString(R.string.showcase_menu_settings), null);
            }
        };
        // Setup the drinkers showcase
        final Showcase.Next drinkers = new Showcase.Next() {
            @Override
            public void show() {
                Showcase.setupShowcase(activity, null, findViewById(R.id.menu_button_drinkers), getString(R.string.showcase_menu_drinkers), settings);
            }
        };
        // Setup the mixing showcase
        final Showcase.Next mixing = new Showcase.Next() {
            @Override
            public void show() {
                Showcase.setupShowcase(activity, null, findViewById(R.id.menu_button_mixing), getString(R.string.showcase_menu_mixing), drinkers);
            }
        };
        // Setup the recipes showcase
        Showcase.Next recipes = new Showcase.Next() {
            @Override
            public void show() {
                Showcase.setupShowcase(activity, null, findViewById(R.id.menu_button_recipes), getString(R.string.showcase_menu_recipes), mixing);
            }
        };
        // Show the introduction
        Showcase.setupShowcase(this, Showcase.MENU, null, getString(R.string.showcase_menu_intro), recipes);
    }

    /**
     * Override the back buttons function to prevent the access to the start screen
     */
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    /**
     * Method to open an activity with shared elements
     * @param intent for the activity change
     * @param button that was clicked
     */
    private void openActivity(Intent intent, MenuButton button) {
        // The layout
        Pair<View, String> layout = new Pair<>(
                button.findViewById(R.id.menu_layout), button.transitionLayout);
        // The image
        Pair<View, String> logo = new Pair<>(
                button.findViewById(R.id.menu_logo), button.transitionLogo);
        // The title
        Pair<View, String> title = new Pair<>(
                button.findViewById(R.id.menu_title), button.transitionTitle);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, layout, logo, title);
        startActivity(intent, options.toBundle());
    }

    /**
     * Method to open the recipes activity
     * @param view that was clicked
     */
    public void openRecipesActivity(View view) {
        openActivity(new Intent(this, RecipesActivity.class), (MenuButton) view);
    }

    /**
     * Method to open the mixing activity
     * @param view that was clicked
     */
    public void openMixingActivity(View view) {
        openActivity(new Intent(this, MixingActivity.class), (MenuButton) view);
    }

    /**
     * Method to open the recipes activity
     * @param view that was clicked
     */
    public void openDrinkersActivity(View view) {
        openActivity(new Intent(this, DrinkersActivity.class), (MenuButton) view);
    }

    /**
     * Method to open the recipes activity
     * @param view that was clicked
     */
    public void openSettingsActivity(View view) {
        openActivity(new Intent(this, SettingsActivity.class), (MenuButton) view);
    }
}
