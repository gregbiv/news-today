
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news;

//~--- non-JDK imports --------------------------------------------------------

import dagger.Component;

import com.github.gregbiv.news.core.sync.NewsSyncAdapter;
import com.github.gregbiv.news.ui.activity.BaseActivity;
import com.github.gregbiv.news.ui.activity.BrowseNewsActivity;
import com.github.gregbiv.news.ui.fragment.BrowseNewsFragment;
import com.github.gregbiv.news.ui.fragment.NewsDetailsFragment;
import com.github.gregbiv.news.ui.fragment.NewsFragment;

//~--- JDK imports ------------------------------------------------------------

import javax.inject.Singleton;

@Singleton
@Component(modules = { AndroidModule.class, BootstrapModule.class })
public interface BootstrapComponent {
    void inject(BootstrapApplication target);

    void inject(BrowseNewsActivity target);

    void inject(NewsFragment target);

    void inject(BrowseNewsFragment target);

    void inject(NewsDetailsFragment target);

    void inject(BaseActivity target);

    void inject(NewsSyncAdapter target);
}
