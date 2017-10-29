
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.repository;

import java.util.Map;

import com.github.gregbiv.news.core.model.Source;
import com.github.gregbiv.news.core.provider.NewsContract;
import com.squareup.sqlbrite.BriteContentResolver;

import rx.Observable;

import rx.schedulers.Schedulers;

public final class SourceRepositoryImpl implements SourceRepository {
    private final BriteContentResolver mBriteContentResolver;

    public SourceRepositoryImpl(BriteContentResolver briteContentResolver) {
        mBriteContentResolver = briteContentResolver;
    }

    @Override
    public Observable<Map<Integer, Source>> sources() {
        return mBriteContentResolver.createQuery(NewsContract.Sources.CONTENT_URI, Source.PROJECTION, null, null, null, true)
                .map(Source.PROJECTION_MAP)
                .subscribeOn(Schedulers.io());
    }
}
