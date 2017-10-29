
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.provider;

import java.util.Arrays;

import com.github.gregbiv.news.util.SelectionBuilder;

import static com.github.gregbiv.news.core.provider.NewsContract.*;
import static com.github.gregbiv.news.core.provider.NewsDatabase.Tables;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.net.Uri;

import android.text.TextUtils;

import timber.log.Timber;

public final class NewsProvider extends ContentProvider {
    private static final String     TAG         = NewsProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int        SOURCES     = 100;
    private static final int        CATEGORIES  = 200;
    private SQLiteOpenHelper        mOpenHelper;

    /**
     * Build an advanced {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually only used by {@link #query}, since it
     * performs table joins useful for {@link Cursor} data.
     */
    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();

        switch (match) {
            case SOURCES : {
                return builder.table(Tables.SOURCES);
            }

            case CATEGORIES : {
                return builder.table(Tables.CATEGORIES);
            }

            default : {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually enough to support {@link #insert},
     * {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int              match   = sUriMatcher.match(uri);

        switch (match) {
            case SOURCES : {
                return builder.table(Tables.SOURCES);
            }

            case CATEGORIES : {
                return builder.table(Tables.CATEGORIES);
            }

            default : {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }

    /**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher   = new UriMatcher(UriMatcher.NO_MATCH);
        final String     authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, "sources", SOURCES);

        return matcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Timber.tag(TAG).v("delete(uri=" + uri + ")");

        if (uri.equals(NewsContract.BASE_CONTENT_URI)) {
            deleteDatabase();
            notifyChange(uri);

            return 1;
        }

        final SQLiteDatabase   db      = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int                    retVal  = builder.where(selection, selectionArgs).delete(db);

        notifyChange(uri);

        return retVal;
    }

    private void deleteDatabase() {
        mOpenHelper.close();

        Context context = getContext();

        NewsDatabase.deleteDatabase(context);
        mOpenHelper = new NewsDatabase(getContext());
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Timber.tag(TAG).v("insert(uri=" + uri + ", values=" + values.toString() + ")");

        final SQLiteDatabase db    = mOpenHelper.getWritableDatabase();
        final int            match = sUriMatcher.match(uri);

        switch (match) {
            case SOURCES : {
                db.insertOrThrow(Tables.SOURCES, null, values);
                notifyChange(uri);

                return Sources.buildSourceUri(values.getAsString(Sources.SOURCE_ID));
            }

            case CATEGORIES : {
                db.insertOrThrow(Tables.CATEGORIES, null, values);
                notifyChange(uri);

                return Sources.buildSourceUri(values.getAsString(Categories.CATEGORY_ID));
            }

            default : {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new NewsDatabase(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db    = mOpenHelper.getReadableDatabase();
        final int            match = sUriMatcher.match(uri);

        Timber.tag(TAG).v("uri=" + uri + " match=" + match + " proj=" + Arrays.toString(projection) + " selection="
                   + selection + " args=" + Arrays.toString(selectionArgs) + ")");

        final SelectionBuilder builder  = buildExpandedSelection(uri, match);
        boolean                distinct =
            !TextUtils.isEmpty(uri.getQueryParameter(NewsContract.QUERY_PARAMETER_DISTINCT));
        Cursor                 cursor   = builder.where(selection, selectionArgs).query(db, distinct, projection, sortOrder, null);
        Context                context  = getContext();

        if (null != context) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Timber.tag(TAG).v("update(uri=" + uri + ", values=" + values.toString() + ")");

        final SQLiteDatabase   db      = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int                    retVal  = builder.where(selection, selectionArgs).update(db, values);

        notifyChange(uri);

        return retVal;
    }

    /** {@inheritDoc} */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SOURCES :
                return Sources.CONTENT_TYPE;

            case CATEGORIES :
                return Categories.CONTENT_TYPE;

            default :
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }
}
