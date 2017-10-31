
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news;

import javax.inject.Singleton;

import com.github.gregbiv.news.core.sync.NewsSyncAdapter;
import com.github.gregbiv.news.ui.activity.BaseActivity;
import com.github.gregbiv.news.ui.activity.BrowseArticlesActivity;
import com.github.gregbiv.news.ui.fragment.ArticleDetailsFragment;
import com.github.gregbiv.news.ui.fragment.BrowseArticlesFragment;
import com.github.gregbiv.news.ui.fragment.ArticleFragment;

import dagger.Component;

@Singleton
@Component(modules = { AndroidModule.class, BootstrapModule.class })
public interface BootstrapComponent {
    void inject(BaseActivity target);

    void inject(BootstrapApplication target);

    void inject(BrowseArticlesActivity target);

    void inject(BrowseArticlesFragment target);

    void inject(ArticleDetailsFragment target);

    void inject(ArticleFragment target);

    void inject(NewsSyncAdapter target);
}
