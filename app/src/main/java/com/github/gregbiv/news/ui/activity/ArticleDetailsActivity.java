
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.activity;

import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.Constants;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.ui.fragment.ArticleDetailsFragment;

import android.os.Bundle;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;

public final class ArticleDetailsActivity extends BaseActivity {
    private static final String NEWS_FRAGMENT_TAG = "fragment_article_details";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);

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

        Article article = getIntent().getParcelableExtra(Constants.Extra.ARTICLE_ITEM);

        if (savedInstanceState == null) {
            ArticleDetailsFragment fragment = ArticleDetailsFragment.newInstance(article);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.article_details_container, fragment, NEWS_FRAGMENT_TAG)
                    .commit();
        }
    }
}
