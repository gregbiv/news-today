
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.api;

//~--- non-JDK imports --------------------------------------------------------

import com.github.gregbiv.news.core.Constants;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.core.model.Source;

import retrofit.http.GET;

import rx.Observable;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

public interface NewsApi {
    @GET(Constants.Http.ARTICLES)
    Observable<List<Article>> articles();

    @GET(Constants.Http.SOURCES)
    Observable<List<Source>> sources();
}
