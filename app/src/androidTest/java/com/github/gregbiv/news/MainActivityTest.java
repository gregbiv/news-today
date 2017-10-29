
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.gregbiv.news.ui.activity.BrowseArticlesActivity;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<BrowseArticlesActivity> main = new ActivityTestRule<>(BrowseArticlesActivity.class);

    @Test
    public void shouldBeAbleToLaunchTheHomeScreen() {

        // onView(withText())
    }
}

