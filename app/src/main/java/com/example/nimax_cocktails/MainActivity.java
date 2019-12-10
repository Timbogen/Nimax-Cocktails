package com.example.nimax_cocktails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nimax_cocktails.drinkers.DrinkersActivity;
import com.example.nimax_cocktails.mixing.MixingActivity;
import com.example.nimax_cocktails.recipes.RecipesActivity;
import com.example.nimax_cocktails.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Method to open the recipes activity
     * @param view that was clicked
     */
    public void openRecipesActivity(View view) {
        Intent intent = new Intent(this, RecipesActivity.class);
        // The layout
        Pair<View, String> layout = new Pair<>(
                findViewById(R.id.recipes_layout), getString(R.string.transition_recipes_layout));
        // The image
        Pair<View, String> logo = new Pair<>(
                findViewById(R.id.recipes_logo), getString(R.string.transition_recipes_logo));
        // The title
        Pair<View, String> title = new Pair<>(
                findViewById(R.id.recipes_title), getString(R.string.transition_recipes_title));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, layout, logo, title);
        startActivity(intent, options.toBundle());
    }

    /**
     * Method to open the mixing activity
     * @param view that was clicked
     */
    public void openMixingActivity(View view) {
        Intent intent = new Intent(this, MixingActivity.class);
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
     * Method to open the recipes activity
     * @param view that was clicked
     */
    public void openDrinkersActivity(View view) {
        Intent intent = new Intent(this, DrinkersActivity.class);
        // The layout
        Pair<View, String> layout = new Pair<>(
                findViewById(R.id.drinkers_layout), getString(R.string.transition_drinkers_layout));
        // The image
        Pair<View, String> logo = new Pair<>(
                findViewById(R.id.drinkers_logo), getString(R.string.transition_drinkers_logo));
        // The title
        Pair<View, String> title = new Pair<>(
                findViewById(R.id.drinkers_title), getString(R.string.transition_drinkers_title));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, layout, logo, title);
        startActivity(intent, options.toBundle());
    }

    /**
     * Method to open the recipes activity
     * @param view that was clicked
     */
    public void openSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        // The layout
        Pair<View, String> layout = new Pair<>(
                findViewById(R.id.settings_layout), getString(R.string.transition_settings_layout));
        // The image
        Pair<View, String> logo = new Pair<>(
                findViewById(R.id.settings_logo), getString(R.string.transition_settings_logo));
        // The title
        Pair<View, String> title = new Pair<>(
                findViewById(R.id.settings_title), getString(R.string.transition_settings_title));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, layout, logo, title);
        startActivity(intent, options.toBundle());
    }
}
