
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news;

import java.io.File;

import javax.inject.Singleton;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.github.gregbiv.news.core.Constants;
import com.github.gregbiv.news.core.PostFromAnyThreadBus;
import com.github.gregbiv.news.core.api.NewsApi;
import com.github.gregbiv.news.core.api.RestAdapterRequestInterceptor;
import com.github.gregbiv.news.core.api.RestErrorHandler;
import com.github.gregbiv.news.core.api.UserAgentProvider;
import com.github.gregbiv.news.core.provider.NewsDatabase;
import com.github.gregbiv.news.core.repository.ArticleRepository;
import com.github.gregbiv.news.core.repository.ArticleRepositoryImpl;
import com.github.gregbiv.news.core.repository.CategoryRepository;
import com.github.gregbiv.news.core.repository.CategoryRepositoryImpl;
import com.github.gregbiv.news.core.repository.SourceRepository;
import com.github.gregbiv.news.core.repository.SourceRepositoryImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import static com.github.gregbiv.news.core.Constants.Http.DISK_CACHE_SIZE;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import android.telephony.TelephonyManager;

import dagger.Module;
import dagger.Provides;

import retrofit.RestAdapter;

import retrofit.client.OkClient;

import retrofit.converter.GsonConverter;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module
public class BootstrapModule {
    @Provides
    @Singleton
    BriteContentResolver provideBriteContentResolver(SqlBrite sqlBrite, ContentResolver contentResolver, Scheduler scheduler) {
        return sqlBrite.wrapContentProvider(contentResolver, scheduler);
    }

    @Provides
    @Singleton
    ContentResolver provideContentResolver(Context context) {
        return context.getContentResolver();
    }

    @Provides
    Gson provideGson() {

        /**
         * GSON instance to use for all request  with date format set up for proper parsing.
         * <p/>
         * You can also configure GSON with different naming policies for your API.
         * Maybe your API is Rails API and all json values are lower case with an underscore,
         * like this "first_name" instead of "firstName".
         * You can configure GSON as such below.
         * <p/>
         *
         * public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd")
         *         .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
         */
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd").create();
    }

    @Singleton
    @Provides
    NewsApi provideNewsApi(RestAdapter restAdapter) {
        return restAdapter.create(NewsApi.class);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Context context) {
        OkHttpClient client = new OkHttpClient();

        client.setConnectTimeout(10, SECONDS);
        client.setReadTimeout(10, SECONDS);
        client.setWriteTimeout(10, SECONDS);

        // Install an HTTP cache in the application cache directory.
        File  cacheDir = new File(context.getCacheDir(), "http");
        Cache cache    = new Cache(cacheDir, DISK_CACHE_SIZE);

        client.setCache(cache);

        return client;
    }

    @Provides
    public Scheduler provideSubscriptionScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Singleton
    @Provides
    Bus provideOttoBus() {
        return new PostFromAnyThreadBus();
    }

    @Provides
    @Singleton
    RestAdapter provideRestAdapter(OkHttpClient client, Gson gson, RestAdapterRequestInterceptor restRequestInterceptor) {
        return new RestAdapter.Builder().setClient(new OkClient(client))
                                        .setEndpoint(Constants.Http.URL_BASE)
                                        .setLogLevel(RestAdapter.LogLevel.BASIC)
                                        .setRequestInterceptor(restRequestInterceptor)
                                        .setConverter(new GsonConverter(gson))
                                        .build();
    }

    @Provides
    RestAdapterRequestInterceptor provideRestAdapterRequestInterceptor(UserAgentProvider userAgentProvider) {
        return new RestAdapterRequestInterceptor(userAgentProvider);
    }

    @Provides
    RestErrorHandler provideRestErrorHandler(Bus bus) {
        return new RestErrorHandler(bus);
    }

    @Provides
    @Singleton
    SqlBrite provideSqlBrite() {
        return SqlBrite.create(message -> Timber.tag("Database").v(message));
    }

    @Provides
    @Singleton
    BriteDatabase providerBriteDatabase(SqlBrite sqlBrite, Context context, Scheduler scheduler) {
        return sqlBrite.wrapDatabaseHelper(new NewsDatabase(context), scheduler);
    }

    @Singleton
    @Provides
    SourceRepository providesSourceRepository(BriteContentResolver briteContentResolver, BriteDatabase briteDatabase, NewsApi newsApi) {
        return new SourceRepositoryImpl(briteContentResolver, briteDatabase, newsApi);
    }

    @Singleton
    @Provides
    CategoryRepository providesCategoryRepository(BriteContentResolver briteContentResolver, BriteDatabase briteDatabase, NewsApi newsApi) {
        return new CategoryRepositoryImpl(briteContentResolver, briteDatabase, newsApi);
    }

    @Singleton
    @Provides
    ArticleRepository providesArticlesRepository(NewsApi newsApi, SourceRepository sourceRepository, CategoryRepository categoryRepository) {
        return new ArticleRepositoryImpl(newsApi, sourceRepository, categoryRepository);
    }

    @Provides
    UserAgentProvider providesUserAgentProvider(ApplicationInfo appInfo, PackageInfo packageInfo,
                                                TelephonyManager telephonyManager, ClassLoader classLoader) {
        return new UserAgentProvider(appInfo, packageInfo, telephonyManager, classLoader);
    }
}
