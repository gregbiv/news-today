
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.github.gregbiv.news.BootstrapApplication;
import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.Constants;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.core.repository.ArticleRepository;
import com.github.gregbiv.news.ui.adapter.ArticleAdapter;
import com.github.gregbiv.news.ui.widget.BetterViewAnimator;
import com.github.gregbiv.news.ui.widget.MultiSwipeRefreshLayout;

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

import rx.subscriptions.CompositeSubscription;

public abstract class ArticleFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, MultiSwipeRefreshLayout.CanChildScrollUpCallback, ArticleAdapter.OnArticleClickListener {
    protected static final int      ANIMATOR_VIEW_LOADING = R.id.view_loading;
    protected static final int      ANIMATOR_VIEW_CONTENT = R.id.article_recycler_view;
    protected static final int      ANIMATOR_VIEW_ERROR   = R.id.view_error;
    protected static final int      ANIMATOR_VIEW_EMPTY   = R.id.view_empty;
    protected int                   mSelectedPosition     = -1;
    @Inject
    protected ArticleRepository     mArticleRepository;
    @BindView(R.id.multi_swipe_refresh_layout)
    MultiSwipeRefreshLayout         mSwipeRefreshLayout;
    @BindView(R.id.article_animator)
    BetterViewAnimator              mViewAnimator;
    @BindView(R.id.article_recycler_view)
    RecyclerView                    mRecyclerView;
    protected ArticleAdapter        mArticleAdapter;
    protected GridLayoutManager     mGridLayoutManager;
    protected CompositeSubscription mSubscriptions;
    protected Listener              listener;

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return ((mRecyclerView != null) && ViewCompat.canScrollVertically(mRecyclerView, -1))
               || ((mViewAnimator != null) && (mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_LOADING));
    }

    @CallSuper
    protected void initRecyclerView() {
        mGridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.article_columns));

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mArticleAdapter);

        if (mSelectedPosition != -1) {
            mRecyclerView.scrollToPosition(mSelectedPosition);
        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_progress_colors));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setCanChildScrollUpCallback(this);
    }

    @Override
    public void onAttach(Activity activity) {
        if (!(activity instanceof Listener)) {
            throw new IllegalStateException("Activity must implement ArticleFragment.Listener.");
        }

        super.onAttach(activity);
        listener = (Listener) activity;
    }

    @Override
    public void onContentClicked(@NonNull Article article, View view, int position) {
        mSelectedPosition = position;
        listener.onNewsSelected(article, view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_articles, container, false);
    }

    @Override
    public void onDestroyView() {
        mSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        listener = (news, view) -> {}
        ;
        mArticleAdapter.setListener(ArticleAdapter.OnArticleClickListener.DUMMY);
        super.onDetach();
    }

    @Override
    public abstract void onRefresh();

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.Extra.ARTICLE_ITEM, new ArrayList<>(mArticleAdapter.getItems()));
        outState.putInt(Constants.Intent.SELECTED_POSITION, mSelectedPosition);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSubscriptions    = new CompositeSubscription();
        mSelectedPosition = (savedInstanceState != null)
                            ? savedInstanceState.getInt(Constants.Intent.SELECTED_POSITION, -1)
                            : -1;

        List<Article> restoredNews = (savedInstanceState != null)
                                     ? savedInstanceState.getParcelableArrayList(Constants.Extra.ARTICLE_ITEMS)
                                     : new ArrayList<>();

        mArticleAdapter = new ArticleAdapter(this, restoredNews);
        mArticleAdapter.setListener(this);
        initSwipeRefreshLayout();
        initRecyclerView();
    }

    public void scrollToTop(boolean smooth) {
        if (smooth) {
            mRecyclerView.smoothScrollToPosition(0);
        } else {
            mRecyclerView.scrollToPosition(0);
        }
    }

    public interface Listener {
        void onNewsSelected(Article article, View view);
    }
}
