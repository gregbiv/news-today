
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.events;

//~--- non-JDK imports --------------------------------------------------------

import retrofit.RetrofitError;

/**
 * Error that is posted when a non-network error event occurs in the {@link retrofit.RestAdapter}
 */
public class RestAdapterErrorEvent {
    private RetrofitError cause;

    public RestAdapterErrorEvent(RetrofitError cause) {
        this.cause = cause;
    }

    public RetrofitError getCause() {
        return cause;
    }
}
