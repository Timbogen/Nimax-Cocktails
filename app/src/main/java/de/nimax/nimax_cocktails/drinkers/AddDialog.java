package de.nimax.nimax_cocktails.drinkers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.nimax.nimax_cocktails.R;

import de.nimax.nimax_cocktails.drinkers.data.Administration;
import de.nimax.nimax_cocktails.drinkers.data.Drinker;

public class AddDialog extends Dialog {

    /**
     * Current context
     */
    private Context context;

    /**
     * Dialog for saving a drinker
     * @param context in which the dialog should be opened
     */
    AddDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add);
        setupDialog();
    }

    /**
     * Method to setup the dialog
     */
    private void setupDialog() {
        // Save the drinker on click
        Button save = findViewById(R.id.button_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipe();
            }
        });
    }

    /**
     * Method to add a drinker on button click
     */
    private void addRecipe() {
        // If field is empty do nothing
        TextView nameEdit = findViewById(R.id.input_recipe_name);
        TextView error = findViewById(R.id.text_error);
        error.setVisibility(TextView.VISIBLE);
        if(nameEdit.getText().toString().equals("")) {
            // Set the hint of the input field
            error.setText(context.getResources().getString(R.string.drinkers_no_drinkers));
            return;
        }

        // If it was successful
        Drinker drinker = new Drinker(nameEdit.getText().toString());
        if (Administration.addDrinker(drinker)) {
            // Save the recipes
            Administration.saveDrinkers();
            // Close the dialog
            this.dismiss();
            // Notify the user
            Toast toast = Toast.makeText(
                    context,
                    context.getString(R.string.drinkers_drinker_saved) + " " + drinker.name,
                    Toast.LENGTH_SHORT
            );
            toast.show();

        // If it wasn't successful
        } else {
            // Tell that the names already blocked
            error.setText(context.getResources().getString(R.string.drinkers_known_name));
        }

    }
}
