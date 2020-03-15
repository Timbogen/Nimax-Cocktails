package de.nimax.nimax_cocktails.settings.setup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.nimax.nimax_cocktails.R;

import java.util.ArrayList;

import de.nimax.nimax_cocktails.recipes.data.Drink;
import de.nimax.nimax_cocktails.recipes.edit.RecipeEditAdapter;

public class SetupActivity extends AppCompatActivity {

    /**
     * The drinks of the setup
     */
    public static ArrayList<Drink> drinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        // Setup the list
        ListView list = findViewById(R.id.setup_drinks);
        list.setAdapter(new RecipeEditAdapter(this, drinks));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

}
