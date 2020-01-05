package de.nimax.nimax_cocktails.recipes.edit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.nimax.nimax_cocktails.R;

import de.nimax.nimax_cocktails.recipes.data.Recipe;

public class RecipeEditActivity extends AppCompatActivity {

    /**
     * Recipe to be edited
     */
    public static Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_edit);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        setupViews();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * Method to setup the views of the activity
     */
    private void setupViews() {
        // Modify the name
        TextView text = findViewById(R.id.list_name);
        text.setText(recipe.name);
        // Setup the list
        ListView list = findViewById(R.id.list_ingredients);
        list.setAdapter(new RecipeEditAdapter(this, recipe.drinks));
    }
}
