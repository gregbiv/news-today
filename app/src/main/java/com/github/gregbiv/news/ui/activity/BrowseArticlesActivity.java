
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.activity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.github.gregbiv.news.BootstrapApplication;
import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.Constants;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.core.model.Category;
import com.github.gregbiv.news.core.model.SpinnerItem;
import com.github.gregbiv.news.core.repository.CategoryRepository;
import com.github.gregbiv.news.core.sync.NewsSyncAdapter;
import com.github.gregbiv.news.ui.adapter.ModeSpinnerAdapter;
import com.github.gregbiv.news.ui.fragment.ArticleDetailsFragment;
import com.github.gregbiv.news.ui.fragment.ArticleFragment;
import com.github.gregbiv.news.ui.fragment.ArticleSourceFragment;
import com.github.gregbiv.news.ui.fragment.BrowseArticlesFragment;
import com.github.gregbiv.news.util.PrefUtils;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Spinner;

import rx.Observable;
import rx.Subscription;

import rx.android.schedulers.AndroidSchedulers;

import rx.subjects.BehaviorSubject;

import rx.subscriptions.Subscriptions;

import timber.log.Timber;

public final class BrowseArticlesActivity extends BaseActivity implements ArticleFragment.Listener {
    private static final String MODE_FEED                    = "mode_feed";
    private static final String ARTICLE_FRAGMENT_TAG         = "fragment_article";
    private static final String ARTICLE_DETAILS_FRAGMENT_TAG = "fragment_article_details";
    private BehaviorSubject<Observable<List<Category>>>
                                mItemsObservableSubject      = BehaviorSubject.create();
    private ModeSpinnerAdapter  mSpinnerAdapter              = new ModeSpinnerAdapter(this, new ArrayList<>());
    private Subscription        mCategoriesSubscription      = Subscriptions.empty();
    private ArticleFragment     mArticleFragment;
    private boolean             mTwoPane;
    private String              mCategory;
    private int                 mSelected;
    @Inject
    CategoryRepository mCategoryRepository;

    private void initModeSpinner() {
        Toolbar toolbar = getToolbar();

        if (toolbar == null) {
            return;
        }

        int itemToSelect = -1;

        if (mCategory.equals(MODE_FEED)) {
            itemToSelect = 0;
        } else if (mSelected >= 0) {

            // for default items
            itemToSelect = mSelected;
        }

        View spinnerContainer = LayoutInflater.from(this)
                .inflate(R.layout.widget_toolbar_spinner,
                        toolbar,
                        false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        toolbar.addView(spinnerContainer, lp);

        Spinner spinner = (Spinner) spinnerContainer.findViewById(R.id.mode_spinner);

        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view,
                                       int position, long itemId) {
                onModeSelected(mSpinnerAdapter.getMode(position), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if (itemToSelect >= 0) {
            Timber.d("Restoring item selection to mode spinner: " + itemToSelect);
            spinner.setSelection(itemToSelect);
        }
    }

    private void loadCategories() {
        Timber.d("Categories is loading.");
        mItemsObservableSubject.onNext(mCategoryRepository.categories().map(categoryMap -> {
            List<Category> categoryList = new ArrayList<Category>();

            for (Category category : categoryMap.values()) {
                categoryList.add(category);
            }

            return categoryList;
        }));
    }

    @Override
    public void onNewsSelected(Article news, View view) {
        Timber.d(String.format("Article '%s' selected", news.getTitle()));

        if (mTwoPane) {
            ArticleDetailsFragment fragment = ArticleDetailsFragment.newInstance(news);

            replaceArticleDetailsFragment(fragment);
        } else {
            Intent intent = new Intent(this, ArticleDetailsActivity.class);

            intent.putExtra(Constants.Extra.ARTICLE_ITEM, news);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewsSyncAdapter.initializeSyncAdapter(this);
        BootstrapApplication.component().inject(this);
        setContentView(R.layout.activity_browse_articles);
        mTwoPane = findViewById(R.id.article_details_container) != null;

        if (savedInstanceState != null) {
            mCategory = savedInstanceState.getString(Constants.Intent.SELECTED_CATEGORY, mCategory);
            mSelected = savedInstanceState.getInt(Constants.Intent.SELECTED_POSITION, -1);

            ArrayList<SpinnerItem> restoredItems =
                    savedInstanceState.getParcelableArrayList(Constants.Extra.SPINNER_ITEMS);

            mSpinnerAdapter = new ModeSpinnerAdapter(this, restoredItems);
            initModeSpinner();
        } else {
            mCategory = PrefUtils.getBrowseNewsMode(this);
            mSelected = PrefUtils.getSelectedPosition(this);
        }

        subscribeToCategories();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_browse_articles, menu);

        return true;
    }

    private void onModeSelected(String mode, int position) {
        if (mode.equals(mCategory)) {
            return;
        }

        mCategory = mode;
        mSelected = position;

        if (mCategory.equals(MODE_FEED)) {
            replaceArticleFragment(new ArticleSourceFragment());
        } else {
            replaceArticleFragment(BrowseArticlesFragment.newInstance(mCategory));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mArticleFragment.onRefresh();

                break;

            case R.id.menu_scroll_to_top:
                mArticleFragment.scrollToTop(true);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        PrefUtils.setBrowseNewsMode(this, mCategory);
        PrefUtils.setSelectedPosition(this, mSelected);
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mArticleFragment = (ArticleFragment) getSupportFragmentManager().findFragmentByTag(ARTICLE_FRAGMENT_TAG);

        if (mArticleFragment == null) {
            replaceArticleFragment(mCategory.equals(MODE_FEED)
                    ? new ArticleSourceFragment()
                    : BrowseArticlesFragment.newInstance(mCategory));
        }

        if (savedInstanceState == null) {
            loadCategories();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.Extra.SPINNER_ITEMS, new ArrayList<>(mSpinnerAdapter.getItems()));
        outState.putInt(Constants.Intent.SELECTED_POSITION, mSelected);
        outState.putString(Constants.Intent.SELECTED_CATEGORY, mCategory);
    }

    private void replaceArticleDetailsFragment(ArticleDetailsFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.article_details_container, fragment, ARTICLE_DETAILS_FRAGMENT_TAG)
                .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right)
                .commit();
    }

    private void replaceArticleFragment(ArticleFragment fragment) {
        mArticleFragment = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.article_container, fragment, ARTICLE_FRAGMENT_TAG)
                .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right)
                .commit();
    }

    private final void subscribeToCategories() {
        Timber.d("Subscribing to categories");

        // Adding categories
        mCategoriesSubscription.unsubscribe();
        mCategoriesSubscription = mCategoryRepository.categories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        categories -> {
                            mSpinnerAdapter.clear();
                            mSpinnerAdapter.addItem(MODE_FEED,
                                    getString(R.string.mode_feeds),
                                    false);
                            mSpinnerAdapter.addHeader(
                                    getString(R.string.menu_categories));

                            for (Category category : categories.values()) {
                                mSpinnerAdapter.addItem(
                                        String.valueOf(category.getId()),
                                        category.getTitle(),
                                        false);
                            }

                            mSpinnerAdapter.notifyDataSetChanged();
                            initModeSpinner();
                        }, throwable -> {
                            Timber.e(throwable, "Categories loading failed");
                        });
    }
}
