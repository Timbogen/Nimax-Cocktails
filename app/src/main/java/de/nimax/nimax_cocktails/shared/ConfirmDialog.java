package de.nimax.nimax_cocktails.shared;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nimax.nimax_cocktails.R;

import java.util.Objects;

public class ConfirmDialog extends Dialog {

    /**
     * Current context
     */
    private final Context context;
    /**
     * The action on confirmation
     */
    private final DialogAction action;
    /**
     * The text of the dialog
     */
    private final String title, description;

    /**
     * Dialog for confirming an action
     *
     * @param context     Context in which the dialog should be opened
     * @param title       The title of the dialog
     * @param description The description of the dialog
     * @param action      The action on confirmation
     */
    public ConfirmDialog(@NonNull Context context, String title, String description, DialogAction action) {
        super(context, R.style.DialogTheme);
        this.context = context;
        this.title = title;
        this.description = description;
        this.action = action;
    }

    /**
     * Create the dialog
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
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
                action.confirm();
                closeDialog();
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

        // Set the text
        TextView title = findViewById(R.id.dialog_title);
        title.setText(this.title);
        TextView description = findViewById(R.id.dialog_description);
        description.setText(this.description);
    }

    /**
     * Close the dialog
     */
    private void closeDialog() {
        this.dismiss();
    }

    /**
     * The action that shall be executed on confirmation
     */
    public interface DialogAction {
        void confirm();
    }
}
