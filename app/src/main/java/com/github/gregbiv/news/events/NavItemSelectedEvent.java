
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.events;

//~--- non-JDK imports --------------------------------------------------------

import com.github.gregbiv.news.ui.activity.BrowseNewsActivity;

/**
 * Pub/Sub event used to communicate between fragment and activity.
 * Subscription occurs in the {@link BrowseNewsActivity}
 */
public class NavItemSelectedEvent {
    private int itemPosition;

    public NavItemSelectedEvent(int itemPosition) {
        this.itemPosition = itemPosition;
    }

    public int getItemPosition() {
        return itemPosition;
    }
}
