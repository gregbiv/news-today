
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.fragment;

//~--- non-JDK imports --------------------------------------------------------

import android.app.Activity;
import android.os.Bundle;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;

import com.github.gregbiv.news.BootstrapApplication;
import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.Constants;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.core.repository.ArticleRepository;
import com.github.gregbiv.news.ui.adapter.NewsAdapter;
import com.github.gregbiv.news.ui.widget.BetterViewAnimator;
import com.github.gregbiv.news.ui.widget.MultiSwipeRefreshLayout;

import rx.subscriptions.CompositeSubscription;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public abstract class NewsFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, MultiSwipeRefreshLayout.CanChildScrollUpCallback, NewsAdapter.OnNewsClickListener{
    protected static final int      ANIMATOR_VIEW_LOADING = R.id.view_loading;
    protected static final int      ANIMATOR_VIEW_CONTENT = R.id.news_recycler_view;
    protected static final int      ANIMATOR_VIEW_ERROR   = R.id.view_error;
    protected static final int      ANIMATOR_VIEW_EMPTY   = R.id.view_empty;
    protected int                   mSelectedPosition     = -1;
    @Inject
    protected ArticleRepository mNewsRepository;
    @BindView(R.id.multi_swipe_refresh_layout)
    MultiSwipeRefreshLayout         mSwipeRefreshLayout;
    @BindView(R.id.news_animator)
    BetterViewAnimator              mViewAnimator;
    @BindView(R.id.news_recycler_view)
    RecyclerView                    mRecyclerView;
    protected NewsAdapter           mNewsAdapter;
    protected GridLayoutManager     mGridLayoutManager;
    protected CompositeSubscription mSubscriptions;
    protected Listener              listener;

    @Override
    public void onAttach(Activity activity) {
        if (!(activity instanceof Listener)) {
            throw new IllegalStateException("Activity must implement NewsFragment.Listener.");
        }

        super.onAttach(activity);
        listener = (Listener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSubscriptions    = new CompositeSubscription();
        mSelectedPosition = (savedInstanceState != null)
                            ? savedInstanceState.getInt(Constants.Intent.SELECTED_POSITION, -1)
                            : -1;

        List<Article> restoredNews = (savedInstanceState != null)
                                  ? savedInstanceState.getParcelableArrayList(Constants.Extra.NEWS_ITEM)
                                  : new ArrayList<>();

        mNewsAdapter = new NewsAdapter(this, restoredNews);
        mNewsAdapter.setListener(this);

        initSwipeRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.Extra.NEWS_ITEM, new ArrayList<>(mNewsAdapter.getItems()));
        outState.putInt(Constants.Intent.SELECTED_POSITION, mSelectedPosition);
    }


    @Override
    public void onDestroyView() {
        mSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        listener = (news, view) -> {};
        mNewsAdapter.setListener(NewsAdapter.OnNewsClickListener.DUMMY);
        super.onDetach();
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return ((mRecyclerView != null) && ViewCompat.canScrollVertically(mRecyclerView, -1))
               || ((mViewAnimator != null) && (mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_LOADING));
    }

    @Override
    public abstract void onRefresh();

    public void scrollToTop(boolean smooth) {
        if (smooth) {
            mRecyclerView.smoothScrollToPosition(0);
        } else {
            mRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void onContentClicked(@NonNull Article news, View view, int position) {
        mSelectedPosition = position;
        listener.onNewsSelected(news, view);
    }

    @CallSuper
    protected void initRecyclerView() {
        mGridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.news_columns));
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int spanCount = mGridLayoutManager.getSpanCount();

                return (mNewsAdapter.isLoadMore(position) /* && (position % spanCount == 0) */) ? spanCount : 1;
            }
        });

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mNewsAdapter);

        if (mSelectedPosition != -1) {
            mRecyclerView.scrollToPosition(mSelectedPosition);
        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_progress_colors));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setCanChildScrollUpCallback(this);
    }

    public interface Listener {
        void onNewsSelected(Article news, View view);
    }
}
