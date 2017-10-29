
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.repository;

//~--- non-JDK imports --------------------------------------------------------

import com.github.gregbiv.news.core.api.NewsApi;
import com.github.gregbiv.news.core.model.Article;

import rx.Observable;

import rx.functions.Func2;

import rx.schedulers.Schedulers;

import rx.subjects.BehaviorSubject;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class ArticleRepositoryImpl implements ArticleRepository {
    private final NewsApi                           mNewsApi;
    private final CategoryRepository                mCategoryRepository;
    private final FeedRepository                    mFeedRepository;
    private BehaviorSubject<Map<Integer, Category>> mCategoriesSubject;
    private BehaviorSubject<Map<Integer, Feed>>     mFeedsSubject;

    public ArticleRepositoryImpl(NewsApi newsApi, CategoryRepository categoryRepository, FeedRepository feedRepository) {
        this.mNewsApi            = newsApi;
        this.mCategoryRepository = categoryRepository;
        this.mFeedRepository     = feedRepository;
    }

    @Override
    public Observable<List<Article>> search(String category, String text, int limit, int offset) {
        return mNewsApi.search(category, text, limit, offset)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .map(response -> response.news)
                .withLatestFrom(getCategoriesMap(), CATEGORIES_MAPPER)
                .withLatestFrom(getFeedsMap(), FEEDS_MAPPER)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<List<Article>> searchAndGroupBy(String category, String text, String groupBy, String dateRange) {
        return mNewsApi.searchAndGroupBy(category, text, groupBy, dateRange)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .map(response -> response.news)
                .withLatestFrom(getCategoriesMap(), CATEGORIES_MAPPER)
                .withLatestFrom(getFeedsMap(), FEEDS_MAPPER)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<Article> getOne(int id) {
        return mNewsApi.getOne(id)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .subscribeOn(Schedulers.io());
    }

    private Observable<Map<Integer, Category>> getCategoriesMap() {
        if (mCategoriesSubject == null) {
            mCategoriesSubject = BehaviorSubject.create();
            mCategoryRepository.categories().subscribe(mCategoriesSubject);
        }
        return mCategoriesSubject.asObservable();
    }

    private Observable<Map<Integer, Feed>> getFeedsMap() {
        if (mFeedsSubject == null) {
            mFeedsSubject = BehaviorSubject.create();
            mFeedRepository.feeds().subscribe(mFeedsSubject);
        }
        return mFeedsSubject.asObservable();
    }

    private static Func2<List<Article>, Map<Integer, Category>, List<Article>> CATEGORIES_MAPPER = (news, categoryMap) -> {
        for (Article item : news) {
            Integer categoryId = item.getCategoryId();

            if (categoryId == 0) {
                continue;
            }

            item.setCategory(categoryMap.get(categoryId));
        }

        return news;
    };

    private static Func2<List<Article>, Map<Integer, Feed>, List<Article>> FEEDS_MAPPER = (news, feedMap) -> {
        for (Article item : news) {
            Integer feedId = item.getFeedId();

            if (feedId == 0) {
                continue;
            }

            item.setFeed(feedMap.get(feedId));
        }

        return news;
    };
}
