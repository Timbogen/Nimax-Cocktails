package com.example.nimax_cocktails.recipes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nimax_cocktails.MainActivity;
import com.example.nimax_cocktails.R;

public class RecipesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
    }

    /**
     * Override the back buttons function
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
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
}
