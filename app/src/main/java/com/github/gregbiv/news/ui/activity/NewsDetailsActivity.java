
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.activity;

//~--- non-JDK imports --------------------------------------------------------

import android.os.Bundle;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;

import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.Constants;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.ui.fragment.NewsDetailsFragment;

public final class NewsDetailsActivity extends BaseActivity {
    private static final String NEWS_FRAGMENT_TAG = "fragment_news_details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        if (mToolbar != null) {
            ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));

            mToolbar.setNavigationOnClickListener(view -> finish());

            ActionBar ab = getSupportActionBar();

            if (ab != null) {
                ab.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
                ab.setDisplayHomeAsUpEnabled(true);
                ab.setDisplayShowHomeEnabled(true);
            }
        }

        Article news = getIntent().getParcelableExtra(Constants.Extra.NEWS_ITEM);

        if (savedInstanceState == null) {
            NewsDetailsFragment fragment = NewsDetailsFragment.newInstance(news);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.news_details_container, fragment, NEWS_FRAGMENT_TAG)
                    .commit();
        }
    }
}
