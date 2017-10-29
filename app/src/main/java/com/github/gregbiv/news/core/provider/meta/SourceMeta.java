
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.provider.meta;

import java.util.HashMap;
import java.util.Map;

import com.github.gregbiv.news.core.model.Source;
import com.github.gregbiv.news.core.provider.NewsContract;
import com.github.gregbiv.news.util.DbUtils;

import com.squareup.sqlbrite.SqlBrite;

import android.content.ContentValues;

import android.database.Cursor;

import rx.functions.Func1;

public interface SourceMeta {
    String[] PROJECTION = {
            NewsContract.Sources._ID,
            NewsContract.Sources.SOURCE_ID,
            NewsContract.Sources.SOURCE_NAME,
            NewsContract.Sources.SOURCE_TITLE,
            NewsContract.Sources.SOURCE_DESCRIPTION,
            NewsContract.Sources.SOURCE_URL,
            NewsContract.Sources.SOURCE_LANGUAGE,
            NewsContract.Sources.SOURCE_COUNTRY,
    };
    Func1<SqlBrite.Query, Map<Integer, Source>> PROJECTION_MAP = query -> {
        Cursor cursor = query.run();

        try {
            Map<Integer, Source> values = new HashMap<>(cursor.getCount());

            while (cursor.moveToNext()) {
                int id = DbUtils.getInt(cursor,
                        NewsContract.Sources.SOURCE_ID);

                values.put(id,
                        new Source()
                                .setId(id)
                                .setTitle(DbUtils.getString(cursor, NewsContract.Sources.SOURCE_TITLE))
                                .setName(DbUtils.getString(cursor, NewsContract.Sources.SOURCE_NAME))
                                .setDescription(DbUtils.getString(cursor, NewsContract.Sources.SOURCE_DESCRIPTION))
                                .setUrl(DbUtils.getString(cursor, NewsContract.Sources.SOURCE_URL))
                                .setLanguage(DbUtils.getString(cursor, NewsContract.Sources.SOURCE_LANGUAGE))
                                .setCountry(DbUtils.getString(cursor, NewsContract.Sources.SOURCE_COUNTRY)));
            }

            return values;
        } finally {
            cursor.close();
        }
    };

    final class Builder {
        private final ContentValues values = new ContentValues();

        public ContentValues build() {
            return values;
        }

        public Builder id(int id) {
            values.put(NewsContract.Sources.SOURCE_ID, id);

            return this;
        }

        public Builder name(String name) {
            values.put(NewsContract.Sources.SOURCE_NAME, name);

            return this;
        }

        public Builder title(String title) {
            values.put(NewsContract.Sources.SOURCE_TITLE, title);

            return this;
        }

        public Builder description(String description) {
            values.put(NewsContract.Sources.SOURCE_DESCRIPTION, description);

            return this;
        }

        public Builder url(String url) {
            values.put(NewsContract.Sources.SOURCE_URL, url);

            return this;
        }

        public Builder language(String language) {
            values.put(NewsContract.Sources.SOURCE_LANGUAGE, language);

            return this;
        }

        public Builder country(String country) {
            values.put(NewsContract.Sources.SOURCE_COUNTRY, country);

            return this;
        }
    }
}
