
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.provider;

//~--- non-JDK imports --------------------------------------------------------

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.provider.BaseColumns;

import static com.github.gregbiv.news.core.provider.NewsContract.*;

public final class NewsDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME    = "news.db";
    private static final int    DB_VERSION = 1;
    private final Context       mContext;

    public NewsDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.SOURCES + "(" + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + SourcesColumns.SOURCE_ID + " INTEGER NOT NULL,"
                + SourcesColumns.SOURCE_NAME + " TEXT NOT NULL,"
                + SourcesColumns.SOURCE_DESCRIPTION + " TEXT NOT NULL,"
                + SourcesColumns.SOURCE_URL + " TEXT NOT NULL,"
                + SourcesColumns.SOURCE_CATEGORY + " TEXT NOT NULL,"
                + SourcesColumns.SOURCE_LANGUAGE + " TEXT NOT NULL,"
                + SourcesColumns.SOURCE_COUNTRY + " TEXT NOT NULL,"
                + "UNIQUE (" + SourcesColumns.SOURCE_ID + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DB_NAME);
    }

    public interface Tables {
        String SOURCES = "sources";
    }
}
