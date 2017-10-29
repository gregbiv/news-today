
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.fragment;

//~--- non-JDK imports --------------------------------------------------------

import android.os.Bundle;

import android.view.View;

import android.widget.Toast;

import ca.barrenechea.widget.recyclerview.decoration.DividerDecoration;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.Constants;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.ui.adapter.NewsFeedAdapter;
import com.github.gregbiv.news.util.DateUtils;

import rx.Observable;

import rx.android.schedulers.AndroidSchedulers;

import rx.subjects.BehaviorSubject;

import timber.log.Timber;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewsFeedFragment extends NewsFragment {
    private boolean                                 mIsLoading              = false;
    private BehaviorSubject<Observable<List<Article>>> mItemsObservableSubject = BehaviorSubject.create();
    private NewsFeedAdapter                         mNewsFeedAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mIsLoading = savedInstanceState.getBoolean(Constants.Intent.IS_LOADING, true);
            Timber.d(String.format("Restoring state: was loading - %s", mIsLoading));
        }

        List<Article> restoredNews = (savedInstanceState != null)
                ? savedInstanceState.getParcelableArrayList(Constants.Extra.NEWS_ITEM)
                : new ArrayList<>();
        mNewsFeedAdapter        = new NewsFeedAdapter(this, restoredNews);
        mNewsFeedAdapter.setListener(this);

        StickyHeaderDecoration decoration = new StickyHeaderDecoration(mNewsFeedAdapter);
        DividerDecoration divider         = new DividerDecoration.Builder(this.getActivity())
                .setHeight(R.dimen.default_divider_height)
                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.default_header_color)
                .build();

        mRecyclerView.setAdapter(mNewsFeedAdapter);
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.addItemDecoration(decoration, 1);

        mViewAnimator.setDisplayedChildId((mIsLoading) ? ANIMATOR_VIEW_LOADING : ANIMATOR_VIEW_CONTENT);
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
        outState.putParcelableArrayList(Constants.Extra.NEWS_ITEM, new ArrayList<>(mNewsFeedAdapter.getItems()));
        outState.putBoolean(Constants.Intent.IS_LOADING, mIsLoading);
        outState.putInt(Constants.Intent.SELECTED_POSITION, mSelectedPosition);
    }

    private void subscribeToNews() {
        Timber.d("Subscribing to items");

        mSubscriptions.add(Observable.concat(mItemsObservableSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(news -> {
                    mSwipeRefreshLayout.setRefreshing(false);

                    Timber.d(String.format("Page is loaded, %d new items", news.size()));

                    mNewsFeedAdapter.clear();
                    mNewsFeedAdapter.add(news);
                    mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_CONTENT);
                }, throwable -> {
                    Timber.e(throwable, "Article loading failed.");

                    if (mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_CONTENT) {
                        mNewsFeedAdapter.setLoadMore(false);
                        Toast.makeText(getActivity(), R.string.view_error_message, Toast.LENGTH_SHORT).show();
                    } else {
                        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_ERROR);
                    }
                }));
    }

    protected final void reloadContent() {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_LOADING);
        }

        // get Calendar instance
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        // substract 3 days
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 3);

        mSelectedPosition = -1;
        mItemsObservableSubject.onNext(
                mNewsRepository.searchAndGroupBy(
                        null,
                        null,
                        "feed",
                        DateUtils.toString(cal.getTime(), "yyyy-MM-dd")
                )
        );
    }

    @Override
    public void onRefresh() {
        reloadContent();
    }
}
