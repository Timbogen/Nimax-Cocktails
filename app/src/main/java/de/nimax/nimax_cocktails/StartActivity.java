package de.nimax.nimax_cocktails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.nimax.nimax_cocktails.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.nimax.nimax_cocktails.recipes.data.Bar;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        // Deactivate the old transition
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);
        loadRecipes();
        // Show the menu
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showMenu();
            }
        }, 300);
    }

    /**
     * Method to load the recipes
     */
    private void loadRecipes() {
        // Load the recipes
        try {
            // Set the standard recipe file path
            Bar.setupPath(getApplicationContext().getFilesDir().getPath() + "/recipes.json");
            // Create input stream
            InputStream is = new FileInputStream(Bar.path);
            // Load Recipes
            Bar.loadRecipes(is);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to show the menu
     */
    private void showMenu() {
        // Create the intent
        Intent intent = new Intent(this, MenuActivity.class);
        // The logo
        Pair<View, String> layout = new Pair<>(
                findViewById(R.id.start_logo), getString(R.string.transition_start_logo));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, layout);
        startActivity(intent, options.toBundle());
        finish();
    }
}
