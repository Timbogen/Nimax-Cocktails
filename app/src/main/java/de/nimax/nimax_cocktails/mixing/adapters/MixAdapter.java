package de.nimax.nimax_cocktails.mixing.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.nimax.nimax_cocktails.R;

import java.util.Collections;
import java.util.Objects;

import de.nimax.nimax_cocktails.mixing.MixingActivity;
import de.nimax.nimax_cocktails.recipes.data.Drinks;
import de.nimax.nimax_cocktails.recipes.data.Recipe;

public class MixAdapter extends RecyclerView.Adapter<MixAdapter.ViewHolder> {
    /**
     * The current recipe
     */
    private static Recipe recipe;
    /**
     * The current activity
     */
    private final Activity activity;
    /**
     * The item touch helper for drag and drop
     */
    private final ItemTouchHelper touchHelper;

    /**
     * Constructor
     *
     * @param activity The current activity
     * @param recipe   The active recipe
     */
    public MixAdapter(Activity activity, Recipe recipe, ItemTouchHelper touchHelper) {
        this.activity = activity;
        MixAdapter.recipe = recipe;
        this.touchHelper = touchHelper;
    }

    /**
     * Create a view holder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_mix, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Set the actual data
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // Convert the drinks to a list
        holder.drink = recipe.drinks.get(position).drink;

        // Set the name
        String name = (position + 1) + ". " + getString("drink_" + recipe.drinks.get(position).drink.name().toLowerCase());
        holder.name.setText(name);

        // Set the amount
        String amount = recipe.drinks.get(position).amount + " ml";
        holder.amount.setText(amount);

        // Add action for dragging
        holder.dragHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    touchHelper.startDrag(holder);
                }
                return false;
            }
        });

        // Add action for deleting
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the item
                recipe.drinks.remove(position);
                MixingActivity.setModifiedState();
                notifyDataSetChanged();

                // Show or hide the divider
                View underline = activity.findViewById(R.id.mixing_table_underline);
                underline.setVisibility(recipe.drinks.size() == 0 ? View.GONE : View.VISIBLE);
            }
        });
    }

    /**
     * Get a string by name
     *
     * @param name of the string
     * @return the corresponding translation
     */
    private String getString(String name) {
        int resourceId = activity.getResources().getIdentifier(
                name,
                "string",
                activity.getPackageName()
        );
        return activity.getString(resourceId);
    }

    /**
     * @return The size of the list view
     */
    @Override
    public int getItemCount() {
        return recipe.drinks.size();
    }

    /**
     * This class provides the adapter with a single row
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * The actual drink
         */
        Drinks drink;

        /**
         * The text views
         */
        TextView name, amount;

        /**
         * The buttons
         */
        ImageView dragHandle, delete;

        /**
         * Constructor
         *
         * @param itemView the matching item view
         */
        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_drink_name);
            amount = itemView.findViewById(R.id.text_drink_amount);
            dragHandle = itemView.findViewById(R.id.image_drag_handle);
            delete = itemView.findViewById(R.id.image_delete);
        }
    }

    /**
     * This class takes care of drag and drop
     */
    public static class TouchHelper extends ItemTouchHelper.Callback {

        /**
         * Disable long press
         */
        @Override
        public boolean isLongPressDragEnabled() {
            return false;
        }

        /**
         * Adjust the scroll speed when dragging out of bounds
         */
        @Override
        public int interpolateOutOfBoundsScroll(@NonNull RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
            return viewSizeOutOfBounds;
        }

        /**
         * Set the movement flags for dragging
         */
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        }

        /**
         * Handle a drag event
         */
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int from = viewHolder.getAdapterPosition();
            int to = target.getAdapterPosition();
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemMoved(from, to);
            Collections.swap(recipe.drinks, from, to);
            MixingActivity.setModifiedState();
            return true;
        }

        /**
         * Do nothing on swap
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }

        /**
         * Update the numeration
         */
        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        }
    }
}