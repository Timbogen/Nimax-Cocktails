package de.nimax.nimax_cocktails.drinkers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import de.nimax.nimax_cocktails.MenuActivity;
import de.nimax.nimax_cocktails.drinkers.data.Administration;

import com.nimax.nimax_cocktails.R;

public class DrinkersActivity extends AppCompatActivity {

    /**
     * The custom adapter for the list view
     */
    private DrinkersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinkers);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        // Deactivate the old transition
        getWindow().setEnterTransition(null);
        getWindow().setExitTransition(null);
        setupList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.notifyDataSetChanged();
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
     * Method to setup the list
     */
    private void setupList() {
        if (Administration.drinkers.size() > 0) {
            // Hide the text fields
            TextView noRecipes = findViewById(R.id.drinkers_no_drinkers);
            TextView noRecipesInfo = findViewById(R.id.drinkers_no_drinkers_info);
            noRecipes.setVisibility(TextView.GONE);
            noRecipesInfo.setVisibility(TextView.GONE);
            // Setup the adapter
            adapter = new DrinkersAdapter(this, Administration.drinkers);
            // Get the list view
            final ListView list = findViewById(R.id.drinkers_list);
            list.setAdapter(adapter);
        }
    }

    /**
     * Method for opening a dialog to save recipes
     */
    public void addDrinker(View v) {
        AddDialog dialog = new AddDialog(this);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (Administration.drinkers.size() == 1) {
                    setupList();
                } else if (Administration.drinkers.size() > 1) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
