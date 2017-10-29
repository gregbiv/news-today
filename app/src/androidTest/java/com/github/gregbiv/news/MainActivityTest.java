
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news;

//~--- non-JDK imports --------------------------------------------------------

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.github.gregbiv.news.ui.activity.BrowseNewsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<BrowseNewsActivity> main = new ActivityTestRule<>(BrowseNewsActivity.class);

    @Test
    public void shouldBeAbleToLaunchTheHomeScreen() {

        // onView(withText())
    }
}
