
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.fragment;

//~--- non-JDK imports --------------------------------------------------------

import android.app.Activity;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;

import android.text.Html;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.BindColor;

import com.bumptech.glide.Glide;

import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.core.repository.ArticleRepository;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import com.github.gregbiv.news.BootstrapApplication;
import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.Constants;
import com.github.gregbiv.news.ui.activity.NewsDetailsActivity;
import com.github.gregbiv.news.ui.widget.BetterViewAnimator;
import com.github.gregbiv.news.ui.widget.MultiSwipeRefreshLayout;
import com.github.gregbiv.news.util.UIUtils;

import rx.Observable;

import rx.android.schedulers.AndroidSchedulers;

import rx.subjects.BehaviorSubject;

import rx.subscriptions.CompositeSubscription;

import timber.log.Timber;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public final class NewsDetailsFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, MultiSwipeRefreshLayout.CanChildScrollUpCallback,
                   ObservableScrollViewCallbacks {
    protected static final int                ANIMATOR_VIEW_LOADING   = R.id.view_loading;
    protected static final int                ANIMATOR_VIEW_CONTENT   = R.id.news_scroll_view;
    protected static final int                ANIMATOR_VIEW_ERROR     = R.id.view_error;
    protected static final int                ANIMATOR_VIEW_EMPTY     = R.id.view_empty;
    private List<Runnable>                    mDeferredUiOperations   = new ArrayList<>();
    private BehaviorSubject<Observable<Article>> mItemsObservableSubject = BehaviorSubject.create();
    private boolean                           mIsLoading              = false;
    @Nullable
    Toolbar                                   mToolbar;
    @BindView(R.id.news_animator)
    BetterViewAnimator                        mViewAnimator;
    @BindView(R.id.news_scroll_view)
    ObservableScrollView                      mScrollView;
    @BindView(R.id.news_illustration)
    ImageView                                 mIllustrationImage;
    @BindView(R.id.news_illustration_container)
    FrameLayout                               mIllustrationContainer;
    @BindView(R.id.multi_swipe_refresh_layout)
    MultiSwipeRefreshLayout                   mSwipeRefreshLayout;
    @BindView(R.id.news_title)
    TextView                                  mTitle;
    @BindView(R.id.news_date)
    TextView                                  mDate;
    @BindView(R.id.news_overview)
    TextView                                  mDescription;
    @BindColor(R.color.theme_primary)
    int                                       mColorThemePrimary;
    @BindColor(R.color.body_text_white)
    int                                       mColorTextWhite;
    @Inject
    protected ArticleRepository mNewsRepository;
    private CompositeSubscription             mSubscriptions;
    private Article mNews;
    private MenuItem                          mMenuItemShare;

    public static NewsDetailsFragment newInstance(Article news) {
        Bundle args = new Bundle();

        args.putParcelable(Constants.Extra.NEWS_ITEM, news);

        NewsDetailsFragment fragment = new NewsDetailsFragment();

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        trySetupToolbar();
        mScrollView.setScrollViewCallbacks(this);

        if (savedInstanceState != null) {
            Timber.d(String.format("Restoring state: pages was loading - %s", mIsLoading));
            mScrollView.onRestoreInstanceState(savedInstanceState.getParcelable(Constants.Intent.SCROLL_VIEW));
            mIsLoading = savedInstanceState.getBoolean(Constants.Intent.IS_LOADING, true);
            mNews      = savedInstanceState.getParcelable(Constants.Extra.NEWS_ITEM);
        }

        mViewAnimator.setDisplayedChildId((mIsLoading) ? ANIMATOR_VIEW_LOADING : ANIMATOR_VIEW_CONTENT);
        initSwipeRefreshLayout();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSubscriptions = new CompositeSubscription();
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);

        Article news = getArguments().getParcelable(Constants.Extra.NEWS_ITEM);

        if (null == news) {
            return;
        }

        subscribeToNews();

        if (savedInstanceState == null) {
            reloadContent(news.getId());
        } else if (mNews != null) {
            onNewsLoaded(mNews);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_news_item, menu);
        mMenuItemShare = menu.findItem(R.id.menu_share);
        tryExecuteDeferredUiOperations();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_share) {}

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.Extra.NEWS_ITEM, mNews);
        outState.putParcelable(Constants.Intent.SCROLL_VIEW, mScrollView.onSaveInstanceState());
        outState.putBoolean(Constants.Intent.IS_LOADING, mIsLoading);
    }

    @Override
    public void onDestroyView() {
        mSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        ViewCompat.setTranslationY(mIllustrationContainer, scrollY / 2);

        if (mToolbar != null) {
            int   parallaxImageHeight = mIllustrationContainer.getMeasuredHeight();
            float alpha               = Math.min(1, (float) scrollY / parallaxImageHeight);

            mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, mColorThemePrimary));
            mToolbar.setTitleTextColor(ScrollUtils.getColorWithAlpha(alpha, mColorTextWhite));
        }
    }

    @Override
    public void onDownMotionEvent() {
        /** ignore */
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        /** ignore */
    }

    private void subscribeToNews() {
        Timber.d("Subscribing to items");

        mSubscriptions.add(Observable.concat(mItemsObservableSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(news -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Timber.d(String.format("Article %d is loaded", news.getId()));
                    mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_CONTENT);
                    onNewsLoaded(news);
                }, throwable -> {
                    Timber.e(throwable, "Article loading failed.");

                    if (mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_CONTENT) {
                        Toast.makeText(getActivity(), R.string.view_error_message, Toast.LENGTH_SHORT).show();
                    } else
                        mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_ERROR);
                }));
    }

    private void trySetupToolbar() {
        if (getActivity() instanceof NewsDetailsActivity) {
            NewsDetailsActivity activity = ((NewsDetailsActivity) getActivity());

            mToolbar = activity.getToolbar();
        }
    }

    private void onNewsLoaded(Article news) {
        mNews = news;

        if (mToolbar != null) {
            mToolbar.setTitle(mNews.getTitle());
        }

        mTitle.setText(news.getTitle());
        mDate.setText(UIUtils.getDisplayDate(news.getDate()));
        mDescription.setText(Html.fromHtml(news.getTextPlain()).toString());

        if (null != news.getIllustration()) {

            // Illustration image
            Glide.with(this)
                    .load(news.getIllustration().getLarge())
                    .placeholder(R.color.news_illustration_placeholder)
                    .centerCrop()
                    .crossFade()
                    .into(mIllustrationImage);
        }
    }

    private void tryExecuteDeferredUiOperations() {
        if (mMenuItemShare != null) {
            for (Runnable r : mDeferredUiOperations) {
                r.run();
            }

            mDeferredUiOperations.clear();
        }
    }

    protected final void reloadContent(int id) {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mViewAnimator.setDisplayedChildId(ANIMATOR_VIEW_LOADING);
        }

        mItemsObservableSubject.onNext(mNewsRepository.getOne(id));
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_progress_colors));
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setCanChildScrollUpCallback(this);
    }

    public void onRefresh() {
        reloadContent(mNews.getId());
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return ((mScrollView != null) && ViewCompat.canScrollVertically(mScrollView, -1))
               || ((mViewAnimator != null) && (mViewAnimator.getDisplayedChildId() == ANIMATOR_VIEW_LOADING));
    }
}
