package com.example.nimax_cocktails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.nimax_cocktails.recipes.RecipesActivity;

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
        startActivity(new Intent(this, RecipesActivity.class));
    }
}
