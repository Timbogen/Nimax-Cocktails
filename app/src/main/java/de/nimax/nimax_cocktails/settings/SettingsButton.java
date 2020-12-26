package de.nimax.nimax_cocktails.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.nimax.nimax_cocktails.R;

public class SettingsButton extends ConstraintLayout {

    /**
     * Button for normal settings
     * @param context it is used in
     * @param attributeSet that modfiy the button
     */
    public SettingsButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        inflate(context, R.layout.settings_button, this);

        TypedArray values = context.obtainStyledAttributes(attributeSet, R.styleable.SettingsButton);
        // Set the logo
        ImageView logo = findViewById(R.id.setting_logo);
        int resourceId = values.getResourceId(R.styleable.SettingsButton_settingLogo, R.drawable.icon_settings);
        logo.setImageResource(resourceId);

        // Set the title
        TextView title = findViewById(R.id.setting_title);
        String text = values.getString(R.styleable.SettingsButton_settingTitle);
        title.setText(text);

        // Set the description
        TextView description = findViewById(R.id.setting_description);
        text = values.getString(R.styleable.SettingsButton_settingDescription);
        description.setText(text);

        values.recycle();
    }

    /**
     * Method to change the description of a settings button
     * @param text to be set
     */
    public void setDescription(String text) {
        TextView description = findViewById(R.id.setting_description);
        description.setText(text);
    }
}
