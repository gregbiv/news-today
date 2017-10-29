
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.repository;

//~--- non-JDK imports --------------------------------------------------------

import com.github.gregbiv.news.core.model.Article;

import rx.Observable;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

public interface ArticleRepository {
    Observable<List<Article>> search(String category, String text, int limit, int offset);
    Observable<List<Article>> searchAndGroupBy(String category, String text, String groupBy, String dateRange);
    Observable<Article> getOne(int id);
}
