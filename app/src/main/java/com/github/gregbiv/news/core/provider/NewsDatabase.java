
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.provider;

import static com.github.gregbiv.news.core.provider.NewsContract.*;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.provider.BaseColumns;

public final class NewsDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME    = "news.db";
    private static final int    DB_VERSION = 1;
    private final Context       mContext;

    public NewsDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DB_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Sources
        db.execSQL("CREATE TABLE " + Tables.SOURCES + "(" + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                    + SourcesColumns.SOURCE_ID + " INTEGER NOT NULL,"
                    + SourcesColumns.SOURCE_NAME + " TEXT NOT NULL,"
                    + SourcesColumns.SOURCE_DESCRIPTION + " TEXT NOT NULL,"
                    + SourcesColumns.SOURCE_URL + " TEXT NOT NULL,"
                    + SourcesColumns.SOURCE_CATEGORY + " TEXT NOT NULL,"
                    + SourcesColumns.SOURCE_LANGUAGE + " TEXT NOT NULL,"
                    + SourcesColumns.SOURCE_COUNTRY  + " TEXT NOT NULL,"
                + "UNIQUE (" + SourcesColumns.SOURCE_ID + ") ON CONFLICT REPLACE)");

        // Categories
        db.execSQL("CREATE TABLE " + Tables.CATEGORIES + "(" + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + CategoriesColumns.CATEGORY_ID + " INTEGER NOT NULL,"
                + CategoriesColumns.CATEGORY_NAME + " TEXT NOT NULL,"
                + CategoriesColumns.CATEGORY_TITLE + " TEXT NOT NULL,"
                + "UNIQUE (" + CategoriesColumns.CATEGORY_ID + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public interface Tables {
        String SOURCES = "sources";
        String CATEGORIES = "categories";
    }
}
