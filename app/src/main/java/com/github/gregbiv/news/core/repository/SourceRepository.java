
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.repository;

import com.github.gregbiv.news.core.model.Source;

import java.util.Map;

import rx.Observable;

public interface SourceRepository {
    Observable<Map<Integer, Source>> sources();
}
