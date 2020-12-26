package de.nimax.nimax_cocktails.recipes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.nimax.nimax_cocktails.R;

import de.nimax.nimax_cocktails.menu.MenuActivity;
import de.nimax.nimax_cocktails.menu.Showcase;
import de.nimax.nimax_cocktails.recipes.data.Bar;
import de.nimax.nimax_cocktails.shared.ConfirmDialog;

public class RecipesActivity extends AppCompatActivity {

    /**
     * The custom adapter for the list view
     */
    private RecipesAdapter adapter;

    /**
     * Create the view
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        // Deactivate the old transition
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);
        setupView();
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

    /**
     * Check if an item was deleted
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateView();
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
        new ConfirmDialog(
                this,
                getString(R.string.recipes_load_title),
                getString(R.string.recipes_load_description),
                new ConfirmDialog.DialogAction() {
                    @Override
                    public void confirm() {
                        Bar.loadRecipes(getResources().openRawResource(R.raw.recipes));
                        Bar.saveRecipes();
                        updateView();
                    }
                }
        ).show();
    }

    /**
     * Delete all the recipes
     *
     * @param v The view that was clicked
     */
    public void clearRecipes(View v) {
        new ConfirmDialog(
                this,
                getString(R.string.recipes_clear_title),
                getString(R.string.recipes_clear_description),
                new ConfirmDialog.DialogAction() {
                    @Override
                    public void confirm() {
                        Bar.removeAllRecipes();
                        updateView();
                    }
                }
        ).show();
    }

    /**
     * Set up the view
     */
    private void setupView() {
        ListView list = findViewById(R.id.recipes_list);
        adapter = new RecipesAdapter(this, Bar.recipes);
        list.setAdapter(adapter);
        updateView();
    }

    /**
     * Update the view of this activity
     */
    private void updateView() {
        boolean noDrinks = Bar.recipes.isEmpty();

        // Update the list
        if (noDrinks) {
            ListView list = findViewById(R.id.recipes_list);
            adapter = new RecipesAdapter(this, Bar.recipes);
            list.setAdapter(adapter);
        }
        else {
            adapter.notifyDataSetChanged();
        }

        // Show/Hide the text fields
        TextView noRecipes = findViewById(R.id.recipes_no_recipes);
        TextView noRecipesInfo = findViewById(R.id.recipes_no_recipes_info);
        noRecipes.setVisibility(noDrinks ? TextView.VISIBLE : TextView.GONE);
        noRecipesInfo.setVisibility(noDrinks ? TextView.VISIBLE : TextView.GONE);
    }
}
