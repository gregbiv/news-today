
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.repository;

//~--- non-JDK imports --------------------------------------------------------

import com.squareup.sqlbrite.BriteContentResolver;

import com.github.gregbiv.news.core.provider.NewsContract.*;

import rx.Observable;

import rx.schedulers.Schedulers;

//~--- JDK imports ------------------------------------------------------------

import java.util.Map;

public final class CategoryRepositoryImpl implements CategoryRepository {
    private final BriteContentResolver mBriteContentResolver;

    public CategoryRepositoryImpl(BriteContentResolver briteContentResolver) {
        mBriteContentResolver = briteContentResolver;
    }

    @Override
    public Observable<Map<Integer, Category>> categories() {
        return mBriteContentResolver.createQuery(Categories.CONTENT_URI, Category.PROJECTION, null, null, null, true)
                .map(Category.PROJECTION_MAP)
                .subscribeOn(Schedulers.io());
    }
}
