package utils.bobo.com.boboutils.App.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import utils.bobo.com.boboutils.R;

public class GridLayoutManagerActivity extends Activity {
    private RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected GridLayoutManager mLayoutManager;
    protected String[] mDataset;
    private static final int DATASET_COUNT = 60;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout_manager);
        initDataset();
        mRecyclerView = (RecyclerView) this.findViewById(R.id.recycler_view);
        mLayoutManager = new GridLayoutManager(this, 4);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position%5 == 0){
                    return 4;
                }
                return 1;
            }
        });
        mAdapter = new CustomAdapter(mDataset);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
    }
    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset[i] = "Element #" + i;
        }
    }
    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        private static final String TAG = "CustomAdapter";

        private String[] mDataSet;

        // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textView;

            public ViewHolder(View v) {
                super(v);
                v.setClickable(true);
                // Define click listener for the ViewHolder's View.
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    }
                });
                textView = (TextView) v.findViewById(R.id.textView);
            }

            public TextView getTextView() {
                return textView;
            }
        }
        // END_INCLUDE(recyclerViewSampleViewHolder)

        /**
         * Initialize the dataset of the Adapter.
         *
         * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
         */
        public CustomAdapter(String[] dataSet) {
            mDataSet = dataSet;
        }
        // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view.
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.text_row_item, viewGroup, false);

            return new ViewHolder(v);
        }
        // END_INCLUDE(recyclerViewOnCreateViewHolder)

        // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            Log.d(TAG, "Element " + position + " set.");

            // Get element from your dataset at this position and replace the contents of the view
            // with that element
            viewHolder.getTextView().setText(mDataSet[position]);
        }
        // END_INCLUDE(recyclerViewOnBindViewHolder)

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataSet.length;
        }
    }
}
