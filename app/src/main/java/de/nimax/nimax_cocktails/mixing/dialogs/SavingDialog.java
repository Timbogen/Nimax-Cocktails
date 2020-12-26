package de.nimax.nimax_cocktails.mixing.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.nimax.nimax_cocktails.R;

import java.util.Objects;

import de.nimax.nimax_cocktails.mixing.MixingActivity;
import de.nimax.nimax_cocktails.recipes.data.Bar;
import de.nimax.nimax_cocktails.recipes.data.Recipe;

public class SavingDialog extends Dialog {

    /**
     * Current context
     */
    private final Context context;

    /**
     * Dialog for saving a drinker
     *
     * @param context in which the dialog should be opened
     */
    public SavingDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_saving);
        Objects.requireNonNull(getWindow()).setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
        setupDialog();
    }

    /**
     * Method to setup the dialog
     */
    private void setupDialog() {
        // Save the drinker on confirm
        Button save = findViewById(R.id.dialog_confirm);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipe();
            }
        });

        // Close the dialog on cancel
        Button cancel = findViewById(R.id.dialog_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        // Set the name
        EditText name = findViewById(R.id.input_recipe_name);
        name.setText(MixingActivity.recipe.name);
    }

    /**
     * Close the dialog
     */
    private void closeDialog() {
        this.dismiss();
    }

    /**
     * Add a recipe
     */
    private void addRecipe() {
        // If field is empty do nothing
        TextView nameEdit = findViewById(R.id.input_recipe_name);
        if (nameEdit.getText().toString().equals("")) {
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.mixing_no_name),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // If order / drinker is empty
        if (MixingActivity.recipe.drinks.isEmpty()) {
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.mixing_no_drinks),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // If it was successful
        MixingActivity.recipe.name = nameEdit.getText().toString();
        if (Bar.addRecipe(new Recipe(MixingActivity.recipe))) {
            // Save the recipes
            Bar.saveRecipes();
            // Close the dialog
            this.dismiss();
            // Notify the user
            Toast.makeText(
                    context,
                    context.getString(R.string.mixing_recipe_saved) + " " + MixingActivity.recipe.name,
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.mixing_known_name),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
