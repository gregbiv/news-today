
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.provider;

import android.net.Uri;

import android.provider.BaseColumns;

/**
 * Contract class for interacting with {@link NewsProvider}.
 */
public final class NewsContract {
    public static final String  QUERY_PARAMETER_DISTINCT = "distinct";
    public static final String  CONTENT_AUTHORITY        = "com.github.gregbiv.news.sync";
    public static final Uri     BASE_CONTENT_URI         = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_SOURCES             = "sources";
    private static final String PATH_CATEGORIES          = "categories";

    private NewsContract() {
        throw new AssertionError("No instances.");
    }

    public interface SourcesColumns {
        String SOURCE_ID          = "source_id";
        String SOURCE_NAME        = "source_name";
        String SOURCE_TITLE       = "source_title";
        String SOURCE_DESCRIPTION = "source_description";
        String SOURCE_URL         = "source_url";
        String SOURCE_LANGUAGE    = "source_language";
        String SOURCE_COUNTRY     = "source_country";
    }

    public interface CategoriesColumns {
        String CATEGORY_ID    = "category_id";
        String CATEGORY_NAME  = "category_name";
        String CATEGORY_TITLE = "category_title";
    }

    /**
     * Source classifications.
     */
    public static class Sources implements SourcesColumns, BaseColumns {
        public static final Uri    CONTENT_URI       = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SOURCES).build();
        public static final String CONTENT_TYPE      = "vnd.android.cursor.dir/vnd.news.source";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.news.source";

        /** Build a {@link Uri} that references a given source. */
        public static Uri buildSourceUri(String sourceId) {
            return CONTENT_URI.buildUpon().appendPath(sourceId).build();
        }

        /**
         * Build {@link Uri} that references all categories.
         */
        public static Uri buildSourcesUri() {
            return CONTENT_URI;
        }
    }

    /**
     * Category classifications.
     */
    public static class Categories implements CategoriesColumns, BaseColumns {
        public static final Uri    CONTENT_URI       = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORIES).build();
        public static final String CONTENT_TYPE      = "vnd.android.cursor.dir/vnd.news.category";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.news.category";

        /** Build a {@link Uri} that references a given source. */
        public static Uri buildCategoryUri(String categoryId) {
            return CONTENT_URI.buildUpon().appendPath(categoryId).build();
        }

        /**
         * Build {@link Uri} that references all categories.
         */
        public static Uri buildCategoriesUri() {
            return CONTENT_URI;
        }
    }
}
