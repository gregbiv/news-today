
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.api;

//~--- non-JDK imports --------------------------------------------------------

import retrofit.RequestInterceptor;

import static com.github.gregbiv.news.core.Constants.Http.*;

public class RestAdapterRequestInterceptor implements RequestInterceptor {
    private UserAgentProvider userAgentProvider;

    public RestAdapterRequestInterceptor(UserAgentProvider userAgentProvider) {
        this.userAgentProvider = userAgentProvider;
    }

    @Override
    public void intercept(RequestFacade request) {

        // Add header to set content type of JSON
        request.addHeader("Content-Type", "application/json");
        request.addEncodedQueryParam(QUERY_API_KEY, API_KEY);
        request.addEncodedQueryParam(QUERY_VERSION, VERSION);

        // Add the user agent to the request.
        request.addHeader("User-Agent", userAgentProvider.get());
    }
}
