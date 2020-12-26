package de.nimax.nimax_cocktails.drinkers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import de.nimax.nimax_cocktails.drinkers.adapters.DrinkersAdapter;
import de.nimax.nimax_cocktails.drinkers.dialogs.AddDialog;
import de.nimax.nimax_cocktails.menu.MenuActivity;
import de.nimax.nimax_cocktails.drinkers.data.Administration;
import de.nimax.nimax_cocktails.menu.Showcase;
import de.nimax.nimax_cocktails.shared.ConfirmDialog;

import com.nimax.nimax_cocktails.R;

public class DrinkersActivity extends AppCompatActivity {

    /**
     * The custom adapter for the list view
     */
    private DrinkersAdapter adapter;

    /**
     * Create the view
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinkers);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        // Deactivate the old transition
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);
        setupView();
        // Setup the showcases for first use
        setupShowcases();
    }

    /**
     * Method to setup the showcases
     */
    private void setupShowcases() {
        final Activity activity = this;
        // Setup the list showcase
        Showcase.Next list = new Showcase.Next() {
            @Override
            public void show() {
                Showcase.setupShowcase(activity, null, null, getString(R.string.showcase_drinkers_list), null);
            }
        };
        Showcase.setupShowcase(this, Showcase.DRINKERS, findViewById(R.id.drinker_add), getString(R.string.showcase_drinkers_add), list);
    }

    /**
     * Check if an item was deleted
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateView();
    }

    /**
     * Override the back buttons function
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        // The layout
        Pair<View, String> layout = new Pair<>(
                findViewById(R.id.drinkers_layout), getString(R.string.transition_drinkers_layout));
        // The image
        Pair<View, String> logo = new Pair<>(
                findViewById(R.id.drinkers_logo), getString(R.string.transition_drinkers_logo));
        // The title
        Pair<View, String> title = new Pair<>(
                findViewById(R.id.drinkers_title), getString(R.string.transition_drinkers_title));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, layout, logo, title);
        startActivity(intent, options.toBundle());
        finishAffinity();
    }

    /**
     * Set up the view
     */
    private void setupView() {
        ListView list = findViewById(R.id.drinkers_list);
        adapter = new DrinkersAdapter(this, Administration.drinkers, true);
        list.setAdapter(adapter);
        updateView();
    }

    /**
     * Update the view of this activity
     */
    private void updateView() {
        boolean noDrinkers = Administration.drinkers.isEmpty();

        // Update the list
        if (noDrinkers) {
            ListView list = findViewById(R.id.drinkers_list);
            adapter = new DrinkersAdapter(this, Administration.drinkers, true);
            list.setAdapter(adapter);
        }
        else {
            adapter.notifyDataSetChanged();
        }

        // Hide or show the text fields
        TextView noRecipes = findViewById(R.id.drinkers_no_drinkers);
        TextView noRecipesInfo = findViewById(R.id.drinkers_no_drinkers_info);
        noRecipes.setVisibility(noDrinkers ? TextView.VISIBLE : TextView.GONE);
        noRecipesInfo.setVisibility(noDrinkers ? TextView.VISIBLE : TextView.GONE);
    }

    /**
     * Method for opening a dialog to save recipes
     *
     * @param v The view that was clicked
     */
    public void addDrinker(View v) {
        AddDialog dialog = new AddDialog(this);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                updateView();
            }
        });
    }

    /**
     * Remove the drinkers
     *
     * @param v The view that was clicked
     */
    public void clearDrinkers(View v) {
        new ConfirmDialog(
                this,
                getString(R.string.drinkers_clear_title),
                getString(R.string.drinkers_clear_description),
                new ConfirmDialog.DialogAction() {
                    @Override
                    public void confirm() {
                        Administration.removeAllDrinkers();
                        updateView();
                    }
                }
        ).show();
    }
}
