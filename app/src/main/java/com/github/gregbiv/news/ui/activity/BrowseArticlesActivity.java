
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.activity;

import javax.inject.Inject;

import com.github.gregbiv.news.BootstrapApplication;
import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.Constants;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.core.model.Source;
import com.github.gregbiv.news.core.repository.SourceRepository;
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

import rx.Subscription;

import rx.android.schedulers.AndroidSchedulers;

import rx.subscriptions.Subscriptions;

import timber.log.Timber;

public final class BrowseArticlesActivity extends BaseActivity implements ArticleFragment.Listener {
    private static final String STATE_SOURCE              = "state_source";
    private static final String STATE_SELECTED            = "state_selected";
    private static final String MODE_FEED                 = "mode_feed";
    private static final String NEWS_FRAGMENT_TAG         = "fragment_articles";
    private static final String NEWS_DETAILS_FRAGMENT_TAG = "fragment_article_details";
    private Subscription        mSourcesSubscription      = Subscriptions.empty();
    private ModeSpinnerAdapter  mSpinnerAdapter           = new ModeSpinnerAdapter(this);
    private ArticleFragment     mArticleFragment;
    private boolean             mTwoPane;
    private String              mSource;
    private int                 mSelected;
    @Inject
    SourceRepository            mSourceRepository;

    private void initModeSpinner() {
        Toolbar toolbar = getToolbar();

        if (toolbar == null) {
            return;
        }

        int itemToSelect = -1;

        if (mSource.equals(MODE_FEED)) {
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

    private void loadSources() {

        // Adding sources
        mSourcesSubscription.unsubscribe();
        mSourcesSubscription = mSourceRepository.sources()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sources -> {
                            mSpinnerAdapter.clear();
                            mSpinnerAdapter.addItem(MODE_FEED,
                                    getString(R.string.mode_feeds),
                                    false);
                            mSpinnerAdapter.addHeader(
                                    getString(R.string.menu_sources));

                            // Adding sources
                            for (Source source : sources.values()) {
                                mSpinnerAdapter.addItem(
                                        String.valueOf(source.getId()),
                                        source.getTitle(),
                                        false);
                            }

                            mSpinnerAdapter.notifyDataSetChanged();
                            initModeSpinner();
                        }, throwable -> {
                            Timber.e(throwable, "Sources loading failed");
                        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewsSyncAdapter.initializeSyncAdapter(this);
        BootstrapApplication.component().inject(this);
        setContentView(R.layout.activity_browse_articles);
        mTwoPane = findViewById(R.id.article_details_container) != null;

        if (savedInstanceState != null) {
            mSource = savedInstanceState.getString(STATE_SOURCE, MODE_FEED);
            mSelected = savedInstanceState.getInt(STATE_SELECTED, -1);
        } else {
            mSource = PrefUtils.getBrowseNewsMode(this);
            mSelected = PrefUtils.getSelectedPosition(this);
        }

        loadSources();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_browse_articles, menu);

        return true;
    }

    private void onModeSelected(String mode, int position) {
        if (mode.equals(mSource)) {
            return;
        }

        mSource = mode;
        mSelected = position;

        if (mSource.equals(MODE_FEED)) {
            replaceArticleFragment(new ArticleSourceFragment());
        } else {
            replaceArticleFragment(BrowseArticlesFragment.newInstance(mSource));
        }
    }

    @Override
    public void onNewsSelected(Article article, View view) {
        Timber.d(String.format("Article '%s' selected", article.getTitle()));

        if (mTwoPane) {
            ArticleDetailsFragment fragment = ArticleDetailsFragment.newInstance(article);

            replaceNewsDetailsFragment(fragment);
        } else {
            Intent intent = new Intent(this, ArticleDetailsActivity.class);

            intent.putExtra(Constants.Extra.ARTICLE_ITEM, article);
            startActivity(intent);
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
        PrefUtils.setBrowseNewsMode(this, mSource);
        PrefUtils.setSelectedPosition(this, mSelected);
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mArticleFragment = (ArticleFragment) getSupportFragmentManager().findFragmentByTag(NEWS_FRAGMENT_TAG);

        if (mArticleFragment == null) {
            replaceArticleFragment(mSource.equals(MODE_FEED)
                    ? new ArticleSourceFragment()
                    : BrowseArticlesFragment.newInstance(mSource));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED, mSelected);
        outState.putString(STATE_SOURCE, mSource);
    }

    private void replaceNewsDetailsFragment(ArticleDetailsFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.article_details_container, fragment, NEWS_DETAILS_FRAGMENT_TAG)
                .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right)
                .commit();
    }

    private void replaceArticleFragment(ArticleFragment fragment) {
        mArticleFragment = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.article_container, fragment, NEWS_FRAGMENT_TAG)
                .setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right)
                .commit();
    }
}
