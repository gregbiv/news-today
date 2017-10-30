
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
import com.github.gregbiv.news.core.model.Category;
import com.github.gregbiv.news.core.provider.NewsContract.*;
import com.github.gregbiv.news.core.provider.NewsDatabase;

import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.BriteDatabase;

import rx.Observable;

import rx.schedulers.Schedulers;

public final class CategoryRepositoryImpl implements CategoryRepository {
    private final BriteContentResolver mBriteContentResolver;
    private final BriteDatabase mDatabase;
    private final NewsApi mNewsApi;

    public CategoryRepositoryImpl(BriteContentResolver briteContentResolver, BriteDatabase briteDatabase,
                                  NewsApi newsApi) {
        mBriteContentResolver = briteContentResolver;
        mDatabase = briteDatabase;
        mNewsApi = newsApi;
    }

    @Override
    public Observable<Map<Integer, Category>> categories() {
        return mBriteContentResolver.createQuery(Categories.CONTENT_URI, Category.PROJECTION, null, null, null, true)
                .map(Category.PROJECTION_MAP)
                .flatMap(
                        categoryMapFromDatabase -> {
                            if (!categoryMapFromDatabase.isEmpty()) {
                                return Observable.just(categoryMapFromDatabase);
                            } else {
                                return mNewsApi.categories().map(categories -> {
                                    Map<Integer, Category> categoryMapFromApi = new HashMap<>();
                                    BriteDatabase.Transaction transaction =
                                            mDatabase.newTransaction();

                                    try {
                                        for (Category category : categories.result) {
                                            mDatabase.insert(NewsDatabase.Tables.CATEGORIES,
                                                    new Category.Builder().id(
                                                            category.getId())
                                                            .title(category.getTitle())
                                                            .name(category.getName())
                                                            .build());
                                            categoryMapFromApi.put(category.getId(), category);
                                        }

                                        transaction.markSuccessful();
                                    } finally {
                                        transaction.end();
                                    }

                                    return categoryMapFromApi;
                                });
                            }
                        })
                .subscribeOn(Schedulers.io());
    }
}
