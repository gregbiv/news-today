
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.repository;

import java.util.HashMap;
import java.util.Map;

import com.github.gregbiv.news.core.api.NewsApi;
import com.github.gregbiv.news.core.model.Source;
import com.github.gregbiv.news.core.provider.NewsContract;
import com.github.gregbiv.news.core.provider.NewsDatabase;

import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.BriteDatabase;

import rx.Observable;

import rx.schedulers.Schedulers;

public final class SourceRepositoryImpl implements SourceRepository {
    private final BriteContentResolver mBriteContentResolver;
    private final BriteDatabase mDatabase;
    private final NewsApi mNewsApi;

    public SourceRepositoryImpl(BriteContentResolver briteContentResolver, BriteDatabase briteDatabase,
                                NewsApi newsApi) {
        mBriteContentResolver = briteContentResolver;
        mDatabase = briteDatabase;
        mNewsApi = newsApi;
    }

    @Override
    public Observable<Map<Integer, Source>> sources() {
        return mBriteContentResolver.createQuery(NewsContract.Sources.CONTENT_URI,
                Source.PROJECTION,
                null,
                null,
                null,
                true).map(Source.PROJECTION_MAP).flatMap(sourceMapFromDatabase -> {
            if (!sourceMapFromDatabase.isEmpty()) {
                return Observable.just(sourceMapFromDatabase);
            } else {
                return mNewsApi.sources().map(sources -> {
                    Map<Integer, Source> sourceMapFromApi = new HashMap<>();
                    BriteDatabase.Transaction transaction = mDatabase.newTransaction();

                    try {
                        for (Source source : sources.result) {
                            mDatabase.insert(NewsDatabase.Tables.SOURCES, new Source.Builder()
                                    .id(source.getId())
                                    .name(source.getName())
                                    .title(source.getTitle())
                                    .description(source.getDescription())
                                    .url(source.getUrl())
                                    .language(source.getLanguage())
                                    .country(source.getCountry())
                                    .category(source.getCategory())
                                    .build());

                            sourceMapFromApi.put(source.getId(), source);
                        }

                        transaction.markSuccessful();
                    } finally {
                        transaction.end();
                    }

                    return sourceMapFromApi;
                });
            }
        }).subscribeOn(Schedulers.io());
    }
}
