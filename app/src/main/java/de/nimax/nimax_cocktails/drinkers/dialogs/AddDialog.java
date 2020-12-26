package de.nimax.nimax_cocktails.drinkers.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.nimax.nimax_cocktails.R;

import java.util.Objects;

import de.nimax.nimax_cocktails.drinkers.data.Administration;
import de.nimax.nimax_cocktails.drinkers.data.Drinker;

public class AddDialog extends Dialog {

    /**
     * Current context
     */
    private final Context context;

    /**
     * Dialog for saving a drinker
     * @param context in which the dialog should be opened
     */
    public AddDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add);
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
                addDrinker();
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
    }

    /**
     * Close the dialog
     */
    private void closeDialog() {
        this.dismiss();
    }

    /**
     * Method to add a drinker on button click
     */
    private void addDrinker() {
        // If field is empty do nothing
        TextView nameEdit = findViewById(R.id.input_drinker_name);
        if(nameEdit.getText().toString().equals("")) {
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.drinkers_no_name),
                    Toast.LENGTH_SHORT
            ).show();
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
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.drinkers_known_name),
                    Toast.LENGTH_SHORT
            ).show();
        }

    }
}
