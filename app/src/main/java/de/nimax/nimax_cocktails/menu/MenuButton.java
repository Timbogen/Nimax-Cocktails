package de.nimax.nimax_cocktails.menu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.nimax.nimax_cocktails.R;

public class MenuButton extends ConstraintLayout {
    /**
     * Transition name of the layout
     */
    public String transitionLayout;
    /**
     * Transition name of the logo
     */
    public String transitionLogo;
    /**
     * Transition name of the title
     */
    public String transitionTitle;

    /**
     * Button for the menu selection
     * @param context that is given
     * @param attributeSet that modify the button
     */
    public MenuButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        inflate(context, R.layout.menu_button, this);

        TypedArray values = context.obtainStyledAttributes(attributeSet, R.styleable.MenuButton);
        // Set the logo
        ImageView logo = findViewById(R.id.menu_logo);
        int resourceId = values.getResourceId(R.styleable.MenuButton_menuLogo, R.drawable.menu_button_recipes);
        logo.setImageResource(resourceId);

        // Set the name
        TextView title = findViewById(R.id.menu_title);
        String text = values.getString(R.styleable.MenuButton_menuTitle);
        title.setText(text);

        // Set the transition names
        ConstraintLayout layout = findViewById(R.id.menu_layout);
        transitionLayout = values.getString(R.styleable.MenuButton_menuLayoutTransition);
        transitionLogo = values.getString(R.styleable.MenuButton_menuLogoTransition);
        transitionTitle = values.getString(R.styleable.MenuButton_menuTitleTransition);
        layout.setTransitionName(transitionLayout);
        logo.setTransitionName(transitionLogo);
        title.setTransitionName(transitionTitle);
        values.recycle();
    }
}
