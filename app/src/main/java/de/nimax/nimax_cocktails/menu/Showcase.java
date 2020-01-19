package de.nimax.nimax_cocktails.menu;

import android.app.Activity;
import android.view.View;

import org.jetbrains.annotations.Nullable;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.listener.DismissListener;

/**
 * Ids for the showcases
 */
public class Showcase {
    public static String MENU = "Menu";
    public static String RECIPES = "Recipes";
    public static String RECIPES_EDIT = "RecipesEdit";
    public static String MIXING = "Mixing";
    public static String DRINKERS = "Drinkers";
    public static String DRINKERS_EDIT = "DrinkersEdit";
    public static String SETTINGS = "Settings";

    /**
     * Method to setup the showcase view
     * @param activity current
     * @param id of the showcase
     * @param view to the shown
     * @param next showcase
     * @param title of the showcase
     */
    public static void setupShowcase(Activity activity, String id, View view, String title, final Showcase.Next next) {
        FancyShowCaseView.Builder showcase = new FancyShowCaseView.Builder(activity)
                .title(title)
                .enableAutoTextPosition()
                .disableFocusAnimation()
                .dismissListener(new DismissListener() {
                    @Override
                    public void onDismiss(@Nullable String s) {
                        // Show the next showcase
                        if (next != null) {
                            next.show();
                        }
                    }

                    @Override
                    public void onSkipped(@Nullable String s) {
                    }
                });

        // Set the id so that it only shows once
        if (id != null) {
            showcase.showOnce(id);
        }
        // Set the view to be focused
        if (view != null) {
            showcase.focusOn(view);
        }
        showcase.build().show();
    }

    /**
     * Interface for setting up the showcases recursively
     */
    public interface Next {
        void show();
    }
}
