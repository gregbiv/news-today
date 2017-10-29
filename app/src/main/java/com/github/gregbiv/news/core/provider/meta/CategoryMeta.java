
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.provider.meta;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.gregbiv.news.core.model.Category;
import com.github.gregbiv.news.core.provider.NewsContract;
import com.github.gregbiv.news.util.DbUtils;
import com.squareup.sqlbrite.SqlBrite;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Func1;

public interface CategoryMeta {
    String[] PROJECTION = {
            NewsContract.Categories._ID,
            NewsContract.Categories.CATEGORY_ID,
            NewsContract.Categories.CATEGORY_NAME,
            NewsContract.Categories.CATEGORY_TITLE,
    };
    Func1<SqlBrite.Query, Map<Integer, Category>> PROJECTION_MAP = query -> {
        Cursor cursor = query.run();

        try {
            Map<Integer, Category> values = new HashMap<>(cursor.getCount());

            while (cursor.moveToNext()) {
                int id = DbUtils.getInt(cursor,
                        NewsContract.Categories.CATEGORY_ID);

                values.put(id,
                        new Category()
                                .setId(id).setTitle(DbUtils.getString(cursor, NewsContract.Categories.CATEGORY_ID))
                                .setName(DbUtils.getString(cursor, NewsContract.Categories.CATEGORY_NAME))
                                .setTitle(DbUtils.getString(cursor, NewsContract.Categories.CATEGORY_TITLE))
                );
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
            values.put(NewsContract.Categories.CATEGORY_ID, id);

            return this;
        }

        public Builder name(String name) {
            values.put(NewsContract.Categories.CATEGORY_NAME, name);

            return this;
        }

        public Builder title(String title) {
            values.put(NewsContract.Categories.CATEGORY_TITLE, title);

            return this;
        }
    }
}
