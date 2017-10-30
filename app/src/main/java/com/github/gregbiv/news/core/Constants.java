
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core;

import com.github.gregbiv.news.BuildConfig;

/**
 * Bootstrap constants
 */
public final class Constants {
    private Constants() {}

    public static final class Auth {

        /**
         * Account type id
         */
        public static final String BOOTSTRAP_ACCOUNT_TYPE = "com.github.gregbiv.news";

        /**
         * Account name
         */
        public static final String BOOTSTRAP_ACCOUNT_NAME = "News";

        /**
         * Provider id
         */
        public static final String BOOTSTRAP_PROVIDER_AUTHORITY = "com.github.gregbiv.news.sync";

        /**
         * Auth token type
         */
        public static final String AUTHTOKEN_TYPE = BOOTSTRAP_ACCOUNT_TYPE;

        private Auth() {}
    }


    public static final class Extra {
        public static final String SPINNER_ITEMS  = "spinner_items";
        public static final String ARTICLE_ITEMS = "article_items";
        public static final String ARTICLE_ITEM  = "article_item";
        public static final String CATEGORY_ITEM = "category_item";
        public static final String SOURCE_ITEM   = "source_item";

        private Extra() {}
    }


    /**
     * All HTTP is done through a REST style API built for demonstration purposes on Parse.com
     * Thanks to the nice people at Parse for creating such a nice system for us to use for bootstrap!
     */
    public static final class Http {
        public static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;    // 50MB

        /**
         * Base URL for all requests
         */
        public static final String URL_BASE                     = "https://private-f7848-newstoday.apiary-mock.com";
        public static final String ARTICLES_GET_ONE             = "/articles/get-one";
        public static final String ARTICLES_SEARCH              = "/articles/search";
        public static final String ARTICLES_SEARCH_AND_GROUP_BY = "/articles/search";
        public static final String SOURCES                      = "/sources";
        public static final String CATEGORIES                   = "/categories";
        public static final int    PER_PAGE                     = 10;

        /**
         * PARAMS for auth
         */
        public static final String QUERY_API_KEY = "apiKey";
        public static final String QUERY_VERSION = "version";
        public static final String API_KEY       = "11b8404b7de4428a94a49a9d34a68b72";
        public static final String VERSION       = BuildConfig.VERSION_NAME;

        private Http() {}
    }


    public static final class Intent {

        /**
         * Action prefix for all intents created
         */
        public static final String INTENT_PREFIX     = "com.github.gregbiv.news.";
        public static final String IS_LOADING        = "is_loading";
        public static final String SCROLL_VIEW       = "scroll_view";
        public static final String CURRENT_PAGE      = "current_page";
        public static final String SELECTED_POSITION = "selected_position";
        public static final String SELECTED_CATEGORY = "selected_category";

        private Intent() {}
    }


    public static class Notification {
        public static final int TIMER_NOTIFICATION_ID = 1000;    // Why 1000? Why not? :)

        private Notification() {}
    }
}
