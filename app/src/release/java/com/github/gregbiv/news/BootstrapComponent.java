
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news;

import javax.inject.Singleton;

import com.github.gregbiv.news.ui.activity.BootstrapActivity;
import com.github.gregbiv.news.ui.activity.MainActivity;
import com.github.gregbiv.news.ui.fragment.NewsFragment;

import dagger.Component;

@Singleton
@Component(modules = { AndroidModule.class, BootstrapModule.class })
public interface BootstrapComponent {
    void inject(BaseActivity target);

    void inject(BootstrapApplication target);

    void inject(BrowseNewsActivity target);

    void inject(NewsFragment target);

    void inject(BrowseNewsFragment target);

    void inject(NewsDetailsFragment target);

    void inject(BaseActivity target);

    void inject(NewsSyncAdapter target);
}
