package de.nimax.nimax_cocktails.recipes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.nimax.nimax_cocktails.menu.MenuActivity;
import de.nimax.nimax_cocktails.menu.Showcase;
import de.nimax.nimax_cocktails.recipes.data.Bar;

import com.nimax.nimax_cocktails.R;

public class RecipesActivity extends AppCompatActivity {

    /**
     * The custom adapter for the list view
     */
    private RecipesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        // Deactivate the old transition
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);
        setupList();
        // Setup the showcases for first use
        setupShowcases();
    }

    /**
     * Method to setup the showcases
     */
    private void setupShowcases() {
        final Activity activity = this;
        // Setup the list showcase
        Showcase.Next list = new Showcase.Next() {
            @Override
            public void show() {
                Showcase.setupShowcase(activity, null, null, getString(R.string.showcase_recipes_list), null);
            }
        };
        Showcase.setupShowcase(this, Showcase.RECIPES, findViewById(R.id.recipes_load), getString(R.string.showcase_recipes_load), list);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.notifyDataSetChanged();
    }

    /**
     * Override the back buttons function
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
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
        finishAffinity();
    }

    /**
     * Method to handle a click on the download button
     */
    public void loadRecipes(View v) {
        // Let the bar load the pre made recipes
        Bar.loadRecipes(getResources().openRawResource(R.raw.recipes));
        // Save the recipes
        Bar.saveRecipes();
        // If list wasn't setup yet do it now
        if (adapter == null) setupList();
        // Let the adapter reload
        adapter.notifyDataSetChanged();
        // Show toast
        Toast toast = Toast.makeText(this, getString(R.string.recipes_recipes_load), Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Method to setup the list
     */
    private void setupList() {
        if (Bar.recipes.size() > 0) {
            // Hide the text fields
            TextView noRecipes = findViewById(R.id.recipes_no_recipes);
            TextView noRecipesInfo = findViewById(R.id.recipes_no_recipes_info);
            noRecipes.setVisibility(TextView.GONE);
            noRecipesInfo.setVisibility(TextView.GONE);
            // Setup the adapter
            adapter = new RecipesAdapter(this, Bar.recipes);
            // Get the list view
            final ListView list = findViewById(R.id.recipes_list);
            list.setAdapter(adapter);
        }
    }
}
