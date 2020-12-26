package de.nimax.nimax_cocktails.drinkers.edit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nimax.nimax_cocktails.R;

import de.nimax.nimax_cocktails.drinkers.data.Administration;
import de.nimax.nimax_cocktails.drinkers.data.Drinker;
import de.nimax.nimax_cocktails.menu.Showcase;

public class DrinkerEditActivity extends AppCompatActivity {

    /**
     * Drinker to be edited
     */
    public static Drinker drinker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinker_edit);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        setupViews();
        // Setup the showcases for first use
        setupShowcases();
    }

    /**
     * Method to setup the showcases
     */
    private void setupShowcases() {
        final Activity activity = this;
        // Setup the delete showcase
        Showcase.Next delete = new Showcase.Next() {
            @Override
            public void show() {
                Showcase.setupShowcase(activity, null, null, getString(R.string.showcase_drinkers_edit_delete), null);
            }
        };
        Showcase.setupShowcase(this, Showcase.DRINKERS_EDIT, findViewById(R.id.image_photo), getString(R.string.showcase_drinkers_edit_photo), delete);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * Method to setup the views of the activity
     */
    private void setupViews() {
        // Modify the image
        if (drinker.image != null) {
            ImageView image = findViewById(R.id.list_image);
            image.setImageBitmap(drinker.image);
        }

        // Modify the name
        TextView text = findViewById(R.id.list_name);
        text.setText(drinker.name);

        // Setup the list
        if (drinker.drinks.size() > 0) {
            ListView list = findViewById(R.id.list_drinks);
            list.setAdapter(new DrinkerEditAdapter(this, drinker.drinks));
        }
    }

    /**
     * Method to delete the currently edited drinker
     */
    public void deleteDrinker(View v) {
        Administration.removeDrinker(drinker);
        finish();
    }

    /**
     * Method to take a photo for the mix
     */
    public void takePhoto(View v ) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 101);
        }
    }

    /**
     * Check if the image was taken successfully
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            drinker.image = bitmap;
            ImageView imageView = findViewById(R.id.list_image);
            imageView.setImageBitmap(bitmap);
            Administration.saveDrinkers();
        }
    }
}
