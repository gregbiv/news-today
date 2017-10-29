
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.api;

import com.github.gregbiv.news.core.Constants;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.core.model.Category;
import com.github.gregbiv.news.core.model.Source;

import retrofit.http.GET;

import retrofit.http.Query;
import rx.Observable;

public interface NewsApi {
    @GET(Constants.Http.SOURCES)
    Observable<Source.Response> sources();

    @GET(Constants.Http.CATEGORIES)
    Observable<Category.Response> categories();

    @GET(Constants.Http.ARTICLES_GET_ONE)
    Observable<Article> getOne(@Query("id") int id);

    @GET(Constants.Http.ARTICLES_SEARCH)
    Observable<Article.Response> search(@Query("query[cat_id]") String category, @Query("text") String text,
                                     @Query("limit") int limit, @Query("offset") int offset);

    @GET(Constants.Http.ARTICLES_SEARCH_AND_GROUP_BY)
    Observable<Article.Response> searchAndGroupBy(@Query("query[cat_id]") String category, @Query("text") String text, @Query("group_by") String groupBy);
}
