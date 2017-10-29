
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.fragment;

//~--- non-JDK imports --------------------------------------------------------

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;

import android.view.View;

import android.widget.Toast;

import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.Constants;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.ui.listener.EndlessScrollListener;

import rx.Observable;

import rx.android.schedulers.AndroidSchedulers;

import rx.subjects.BehaviorSubject;

import timber.log.Timber;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

public final class BrowseNewsFragment extends NewsFragment implements EndlessScrollListener.OnLoadMoreCallback {
    private static final int                        VISIBLE_THRESHOLD       = 10;
    private BehaviorSubject<Observable<List<Article>>> mItemsObservableSubject = BehaviorSubject.create();
    private int                                     mCurrentPage            = 0;
    private boolean                                 mIsLoading              = false;
    private EndlessScrollListener                   mEndlessScrollListener;
    private String                                  mCategory;

    public static BrowseNewsFragment newInstance(@NonNull String category) {
        Bundle args = new Bundle();
        args.putString(Constants.Extra.CATEGORY_ITEM, category);
        BrowseNewsFragment fragment = new BrowseNewsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCategory = getArguments().getString(Constants.Extra.CATEGORY_ITEM);

        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(Constants.Intent.CURRENT_PAGE, 0);
            mIsLoading   = savedInstanceState.getBoolean(Constants.Intent.IS_LOADING, true);
            Timber.d(String.format("Restoring state: pages 1-%d, was loading - %s", mCurrentPage, mIsLoading));
        }

        mNewsAdapter.setLoadMore(true);
        mViewAnimator.setDisplayedChildId((mCurrentPage == 0) ? ANIMATOR_VIEW_LOADING : ANIMATOR_VIEW_CONTENT);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        subscribeToNews();

        if (savedInstanceState == null) {
            reloadContent();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.Intent.CURRENT_PAGE, mCurrentPage);
        outState.putBoolean(Constants.Intent.IS_LOADING, mIsLoading);
        outState.putString(Constants.Extra.CATEGORY_ITEM, mCategory);
    }

    @Override
    public void onDestroyView() {
        mSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        reloadContent();
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (mNewsAdapter.isLoadMore()) {
            pullPage(page);
        }
    }

    protected final void reloadContent() {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_LOADING);
        }

        mSelectedPosition = -1;
        reAddOnScrollListener(mGridLayoutManager, mCurrentPage = 0);
        pullPage(mCurrentPage);
    }

    private void subscribeToNews() {
        Timber.d("Subscribing to items");
        mSubscriptions.add(Observable.concat(mItemsObservableSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(news -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mCurrentPage++;

                    Timber.d(String.format("Page %d is loaded, %d new items", mCurrentPage, news.size()));

                    if (mCurrentPage == 1) {
                        mNewsAdapter.clear();
                    }

                    mNewsAdapter.setLoadMore(!news.isEmpty());
                    mNewsAdapter.add(news);
                    mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_CONTENT);
                }, throwable -> {
                    Timber.e(throwable, "Article loading failed.");
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_CONTENT) {
                        mNewsAdapter.setLoadMore(false);
                        Toast.makeText(getActivity(), R.string.view_error_message, Toast.LENGTH_SHORT).show();
                    } else
                        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_ERROR);
                }));
    }

    private void pullPage(int page) {
        Timber.d(String.format("Page %d is loading.", page));
        mItemsObservableSubject.onNext(
                mNewsRepository.search(mCategory, null, Constants.Http.PER_PAGE, page * Constants.Http.PER_PAGE)
        );
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        reAddOnScrollListener(mGridLayoutManager, mCurrentPage);
    }

    private void reAddOnScrollListener(GridLayoutManager layoutManager, int startPage) {
        if (mEndlessScrollListener != null) {
            mRecyclerView.removeOnScrollListener(mEndlessScrollListener);
        }

        mEndlessScrollListener = EndlessScrollListener.fromGridLayoutManager(layoutManager, VISIBLE_THRESHOLD, startPage).setCallback(this);
        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }
}
