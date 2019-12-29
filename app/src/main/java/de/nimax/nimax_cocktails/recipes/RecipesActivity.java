package de.nimax.nimax_cocktails.recipes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import de.nimax.nimax_cocktails.MainActivity;
import de.nimax.nimax_cocktails.mixing.models.Bar;

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
        setupList();
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

    /**
     * Method to handle a click on the download button
     */
    public void loadRecipes(View v) {
        // Let the bar load the pre made recipes
        Bar.loadMixes(getResources().openRawResource(R.raw.recipes));
        // Save the recipes
        Bar.saveMixes();
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
        // Setup the adapter
        adapter = new RecipesAdapter(this, Bar.mixes);
        // Get the list view
        ListView list = findViewById(R.id.recipes_list);
        list.setAdapter(adapter);
    }
}
