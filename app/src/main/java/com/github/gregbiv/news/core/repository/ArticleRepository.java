
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.repository;

import java.util.List;

import com.github.gregbiv.news.core.model.Article;

import rx.Observable;

public interface ArticleRepository {
    Observable<List<Article>> search(String source, String text, int limit, int offset);
    Observable<List<Article>> searchAndGroupBy(String source, String text, String groupBy);
    Observable<Article> getOne(int id);
}
