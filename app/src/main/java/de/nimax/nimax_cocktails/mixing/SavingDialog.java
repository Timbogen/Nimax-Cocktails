package de.nimax.nimax_cocktails.mixing;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nimax.nimax_cocktails.R;

import de.nimax.nimax_cocktails.recipes.data.Bar;

public class SavingDialog extends Dialog {

    /**
     * Current context
     */
    private Context context;

    /**
     * Dialog for saving a recipe
     * @param context in which the dialog should be opened
     */
    SavingDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_saving);
        setupDialog();
    }

    /**
     * Method to setup the dialog
     */
    private void setupDialog() {
        // Save the recipe on click
        Button save = findViewById(R.id.button_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipe();
            }
        });
    }

    /**
     * Method to add a recipe on button click
     */
    private void addRecipe() {
        // If field is empty do nothing
        TextView nameEdit = findViewById(R.id.input_recipe_name);
        TextView error = findViewById(R.id.text_error);
        error.setVisibility(TextView.VISIBLE);
        if(nameEdit.getText().toString().equals("")) {
            // Set the hint of the input field
            error.setText(context.getResources().getString(R.string.mixing_no_name));
            return;
        }

        // If order / recipe is empty
        if (MixingActivity.mix.drinks.size() == 0) {
            // Set the hint for the input field
            error.setText(context.getResources().getString(R.string.mixing_no_drinks));
            return;
        }

        // If it was successful
        MixingActivity.mix.name = nameEdit.getText().toString();
        if (Bar.addNewMix(MixingActivity.mix)) {
            // Save the recipes
            Bar.saveMixes();
            // Close the dialog
            this.dismiss();

        // If it wasn't successful
        } else {
            // Tell that the names already blocked
            error.setText(context.getResources().getString(R.string.mixing_known_name));
        }

    }
}
