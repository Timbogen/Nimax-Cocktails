package de.nimax.nimax_cocktails.recipes.edit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nimax.nimax_cocktails.R;

import de.nimax.nimax_cocktails.mixing.MixingActivity;
import de.nimax.nimax_cocktails.recipes.data.Bar;
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
        // Modify the image
        if (recipe.image != null) {
            ImageView image = findViewById(R.id.list_image);
            image.setImageBitmap(recipe.image);
        }

        // Modify the name
        TextView text = findViewById(R.id.list_name);
        text.setText(recipe.name);

        // Setup the list
        ListView list = findViewById(R.id.list_ingredients);
        list.setAdapter(new RecipeEditAdapter(this, recipe.drinks));
    }

    /**
     * Method to delete the currently edited recipe
     */
    public void deleteRecipe(View v) {
        Bar.removeRecipe(recipe);
        finish();
    }

    /**
     * Method to load the currently edited recipe
     */
    public void loadRecipe(View v) {
        MixingActivity.recipe = new Recipe(recipe);
        Intent intent = new Intent(this, MixingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        finishAffinity();
    }

    /**
     * Method to take a photo for the mix
     */
    public void takePhoto(View v ) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            recipe.image = bitmap;
            ImageView imageView = findViewById(R.id.list_image);
            imageView.setImageBitmap(bitmap);
            Bar.saveRecipes();
        }
    }
}