package rana.jatin.core.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rana.jatin.core.R;
import rana.jatin.core.widget.circularReveal.widget.RevealRelativeLayout;

public class RelativeViewLayout extends RevealRelativeLayout {

    public static final  int MATCH_PARENT = 2;
    public static final  int WRAP_CONTENT = 1;
    private static final String TAG_LOADING = "ProgressView.TAG_LOADING";
    private static final String TAG_EMPTY = "ProgressView.TAG_EMPTY";
    private static final String TAG_ERROR = "ProgressView.TAG_ERROR";
    final String CONTENT = "type_content";
    final String LOADING = "type_loading";
    final String EMPTY = "type_empty";
    final String ERROR = "type_error";
    LayoutInflater inflater;
    List<View> contentViews = new ArrayList<>();
    View loadingView;
    View emptyView;
    View errorView;
    private TextView tvProgressTitle;
    private TextView tvProgressDesc;
    private String TAG = RelativeViewLayout.class.getName();
    private String state = CONTENT;
    private RelativeLayout.LayoutParams layoutParams;
    private static int LOADING_DELAY=2000;

    public RelativeViewLayout(Context context) {
        super(context);
    }

    public RelativeViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RelativeViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RelativeViewLayout);
        int emptyViewLayout, progressViewLayout, errorViewLayout;
        try {
            emptyViewLayout = ta.getResourceId(R.styleable.RelativeViewLayout_emptyView, 0);
            progressViewLayout = ta.getResourceId(R.styleable.RelativeViewLayout_progressView, 0);
            errorViewLayout = ta.getResourceId(R.styleable.RelativeViewLayout_errorView, 0);
            int marginTop = ta.getDimensionPixelSize(R.styleable.RelativeViewLayout_marginTop, 0);
            int marginBottom = ta.getDimensionPixelSize(R.styleable.RelativeViewLayout_marginBottom, 0);
            int marginLeft = ta.getDimensionPixelSize(R.styleable.RelativeViewLayout_marginLeft, 0);
            int marginRight = ta.getDimensionPixelSize(R.styleable.RelativeViewLayout_marginRight, 0);
            int[] margin = {marginLeft, marginTop, marginRight, marginBottom};
            setLoadingView(progressViewLayout, margin,ta.getInt(R.styleable.RelativeViewLayout_progressViewTheme,MATCH_PARENT),ta.getInt(R.styleable.RelativeViewLayout_progressViewAlign,Gravity.NO_GRAVITY));
            setEmptyView(emptyViewLayout, margin,ta.getInt(R.styleable.RelativeViewLayout_emptyViewTheme,MATCH_PARENT),ta.getInt(R.styleable.RelativeViewLayout_emptyViewAlign,Gravity.NO_GRAVITY));
            setErrorView(errorViewLayout, margin,ta.getInt(R.styleable.RelativeViewLayout_errorViewTheme,MATCH_PARENT),ta.getInt(R.styleable.RelativeViewLayout_errorViewAlign,Gravity.NO_GRAVITY));
        } catch (Exception e) {

        } finally {
            ta.recycle();
        }

    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (child.getTag() == null || (!child.getTag().equals(TAG_LOADING)
                && !child.getTag().equals(TAG_EMPTY) && !child.getTag().equals(TAG_ERROR))) {
            contentViews.add(child);
        }
    }

    /**
     * Hide all other states and show content Immediately
     */
    public void showContentImmediately() {
        switchState(CONTENT, Collections.<Integer>emptyList());
    }

    /**
     * Hide all other states and show content
     */
    public void showContent() {
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchState(CONTENT, Collections.<Integer>emptyList());
            }
        }, LOADING_DELAY);
    }

    /**
     * Hide all other states and show content
     *
     * @param skipIds Ids of views not to show
     */
    public void showContent(final List<Integer> skipIds) {
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchState(CONTENT, skipIds);
            }
        }, LOADING_DELAY);
    }

    /**
     * Hide content and show the progress bar
     *
     * @param skipIds Ids of views to not hide
     */
    public void showLoading(List<Integer> skipIds) {
        switchState(LOADING, skipIds);
    }

    /**
     * Hide content and show the progress bar
     */
    public void showLoading() {
        switchState(LOADING, Collections.<Integer>emptyList());
    }

    /**
     * Hide content and show the progress bar with progress text
     */
    public void showLoading(String progress, String des) {
        switchState(LOADING, progress, des, Collections.<Integer>emptyList());
    }

    /**
     * Show empty view when there are not data to show
     *
     * @param skipIds Ids of views to not hide
     */
    public void showEmpty(final List<Integer> skipIds) {
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchState(EMPTY, skipIds);
            }
        }, LOADING_DELAY);
    }

    public void showEmpty() {
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchState(EMPTY, Collections.<Integer>emptyList());
            }
        }, LOADING_DELAY);
    }

    /**
     * Show error view with a button when something goes wrong and prompting the user to try again
     *
     * @param skipIds Ids of views to not hide
     */
    public void showError(final List<Integer> skipIds) {
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchState(ERROR, skipIds);
            }
        }, LOADING_DELAY);
    }


    public void showError() {
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switchState(ERROR, Collections.<Integer>emptyList());
            }
        }, LOADING_DELAY);
    }


    public String getState() {
        return state;
    }

    public boolean isContent() {
        return state.equals(CONTENT);
    }

    public boolean isLoading() {
        return state.equals(LOADING);
    }

    public boolean isEmpty() {
        return state.equals(EMPTY);
    }

    public boolean isError() {
        return state.equals(ERROR);
    }

    private void switchState(String state, List<Integer> skipIds) {
        this.state = state;

        switch (state) {
            case CONTENT:
                //Hide all state views to display content
                hideLoadingView();
                hideEmptyView();
                hideErrorView();

                // setContentVisibility(true, skipIds);
                break;
            case LOADING:
                hideEmptyView();
                hideErrorView();

                showLoadingView();
                // setContentVisibility(false, skipIds);
                break;
            case EMPTY:
                hideLoadingView();
                hideErrorView();

                showEmptyView();
                //   setContentVisibility(false, skipIds);
                break;
            case ERROR:
                hideLoadingView();
                hideEmptyView();

                showErrorView();
                // setContentVisibility(false, skipIds);
                break;
        }
    }

    private void switchState(String state, String messageTitle, String messageDesc, List<Integer> skipIds) {
        this.state = state;

        switch (state) {
            case CONTENT:
                //Hide all state views to display content
                hideLoadingView();
                hideEmptyView();
                hideErrorView();

                // setContentVisibility(true, skipIds);
                break;
            case LOADING:
                hideEmptyView();
                hideErrorView();

                showLoadingView(messageTitle, messageDesc);
                // setContentVisibility(false, skipIds);
                break;
            case EMPTY:
                hideLoadingView();
                hideErrorView();

                showEmptyView();
                //   setContentVisibility(false, skipIds);
                break;
            case ERROR:
                hideLoadingView();
                hideEmptyView();

                showErrorView();
                // setContentVisibility(false, skipIds);
                break;
        }
    }

    private void showLoadingView() {
        if (loadingView != null) {
            loadingView.setVisibility(VISIBLE);
            loadingView.bringToFront();
            loadingView.invalidate();
        }
    }

    private void showLoadingView(String progress, String des) {
        if (loadingView != null) {
            if (tvProgressTitle != null)
                tvProgressTitle.setText(progress);

            if (tvProgressDesc != null)
                tvProgressDesc.setText(des);

            loadingView.setVisibility(VISIBLE);
            loadingView.bringToFront();
            loadingView.invalidate();
        }
    }

    private void showEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(VISIBLE);
            emptyView.bringToFront();
            emptyView.invalidate();
        }
    }

    private void showErrorView() {
        if (errorView != null) {
            errorView.setVisibility(VISIBLE);
            errorView.bringToFront();
            errorView.invalidate();
        }
    }

    public void loadingClickListener(View.OnClickListener onClickListener) {
        if (loadingView != null) {
            loadingView.setOnClickListener(onClickListener);
        }
    }

    public void emptyViewClickListener(View.OnClickListener onClickListener) {
        if (emptyView != null) {
            emptyView.setOnClickListener(onClickListener);
        }

    }

    public void errorViewClickListener(View.OnClickListener onClickListener) {
        if (errorView != null) {
            errorView.setOnClickListener(onClickListener);
        }
    }

    private void setContentVisibility(boolean visible, List<Integer> skipIds) {
        for (View v : contentViews) {
            if (!skipIds.contains(v.getId())) {
                v.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void hideLoadingView() {
        if (loadingView != null) {
            loadingView.setVisibility(GONE);
        }
    }

    private void hideEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(GONE);
        }
    }

    private void hideErrorView() {
        if (errorView != null) {
            errorView.setVisibility(GONE);
        }
    }

    public View getLoadingView() {
        return loadingView;
    }

    public void setLoadingView(int id, int[] margin,int theme,int gravity) {
        try {
            this.loadingView = inflater.inflate(id, null);
            this.loadingView.setTag(TAG_LOADING);
            this.loadingView.setClickable(true);

            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            if (margin != null)
                layoutParams.setMargins(margin[0], margin[1], margin[2], margin[3]);

            tvProgressTitle = loadingView.findViewById(R.id.progress_title);
            tvProgressDesc = loadingView.findViewById(R.id.progress_desc);

            addView(this.loadingView, layoutParams);
            setLoadingViewTheme(theme,gravity);
            showContentImmediately();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public void setThemeMinimal() {

    }

    public void setThemeFull() {

    }

    public RelativeViewLayout setLoadingViewTheme(int theme, int gravity) {
        try {
            if (theme==MATCH_PARENT) {
                layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.addRule(gravity);
                this.loadingView.setLayoutParams(layoutParams);
            } else {
                layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(gravity);
                this.loadingView.setLayoutParams(layoutParams);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return this;
    }

    public View getEmptyView() {
        return emptyView;
    }

    public void setEmptyView(int id, int[] margin,int theme,int gravity) {
        try {
            this.emptyView = inflater.inflate(id, null);
            this.emptyView.setTag(TAG_EMPTY);
            this.emptyView.setClickable(true);

            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            if (margin != null)
                layoutParams.setMargins(margin[0], margin[1], margin[2], margin[3]);

            addView(this.emptyView, layoutParams);
            setLoadingViewTheme(theme,gravity);
            showContentImmediately();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

    }

    public View getErrorView() {
        return errorView;
    }

    public void setErrorView(int id, int[] margin,int theme,int gravity) {
        try {
            this.errorView = inflater.inflate(id, null);
            this.errorView.setTag(TAG_ERROR);
            this.errorView.setClickable(true);

            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            if (margin != null)
                layoutParams.setMargins(margin[0], margin[1], margin[2], margin[3]);
            addView(this.errorView, layoutParams);
            setLoadingViewTheme(theme,gravity);
            showContentImmediately();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }
}