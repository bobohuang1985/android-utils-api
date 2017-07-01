package org.join.ws.ui.widget;

import org.join.web.serv.R;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

public class ProgressBarPreference extends Preference {

    private ProgressBar mProgressBar;

    public ProgressBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ProgressBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBarPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

}
