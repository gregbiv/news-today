
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.gregbiv.news.core.api.NewsApi;
import com.github.gregbiv.news.core.model.Article;
import com.github.gregbiv.news.core.model.Category;
import com.github.gregbiv.news.core.model.Source;

import rx.Observable;

import rx.functions.Func2;

import rx.schedulers.Schedulers;

import rx.subjects.BehaviorSubject;

public final class ArticleRepositoryImpl implements ArticleRepository {
    private static Func2<List<Article>, Map<Integer, Source>, List<Article>> SOURCES_MAPPER =
            (news, sourceMap) -> {
                for (Article item : news) {
                    Integer sourceId = item.getSourceId();

                    if (sourceId == 0) {
                        continue;
                    }

                    item.setSource(sourceMap.get(sourceId));
                }

                return news;
            };
    private static Func2<List<Article>, Map<Integer, Category>, List<Article>> CATEGORIES_MAPPER =
            (articles, categoryMap) -> {
                for (Article item : articles) {
                    Integer categoryId = item.getCategoryId();

                    if (categoryId == 0) {
                        continue;
                    }

                    item.setCategory(categoryMap.get(categoryId));
                }

                return articles;
            };
    private final NewsApi mNewsApi;
    private final SourceRepository mSourceRepository;
    private final CategoryRepository mCategoryRepository;
    private BehaviorSubject<Map<Integer, Source>> mSourcesSubject;
    private BehaviorSubject<Map<Integer, Category>> mCategorySubject;

    public ArticleRepositoryImpl(NewsApi newsApi, SourceRepository sourceRepository, CategoryRepository categoryRepository) {
        this.mNewsApi = newsApi;
        this.mSourceRepository = sourceRepository;
        this.mCategoryRepository = categoryRepository;
    }

    @Override
    public Observable<List<Article>> search(String category, String text, int limit, int offset) {
        return mNewsApi.search(category, text, limit, offset)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .map(response -> response.articles)
                .withLatestFrom(getCategoriesMap(), CATEGORIES_MAPPER)
                .withLatestFrom(getSourcesMap(), SOURCES_MAPPER)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Article>> searchAndGroupBy(String category, String text, String groupBy) {
        return mNewsApi.searchAndGroupBy(category, text, groupBy)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .map(response -> response.articles)
                .withLatestFrom(getCategoriesMap(), CATEGORIES_MAPPER)
                .withLatestFrom(getSourcesMap(), SOURCES_MAPPER)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Article> getOne(int id) {
        return mNewsApi.getOne(id)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .subscribeOn(Schedulers.io());
    }

    private Observable<Map<Integer, Source>> getSourcesMap() {
        if (mSourcesSubject == null) {
            mSourcesSubject = BehaviorSubject.create();
            mSourceRepository.sources().subscribe(mSourcesSubject);
        }

        return mSourcesSubject.asObservable();
    }

    private Observable<Map<Integer, Category>> getCategoriesMap() {
        if (mCategorySubject == null) {
            mCategorySubject = BehaviorSubject.create();
            mCategoryRepository.categories().subscribe(mCategorySubject);
        }

        return mCategorySubject.asObservable();
    }
}
