
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.sync;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.github.gregbiv.news.BootstrapApplication;
import com.github.gregbiv.news.R;
import com.github.gregbiv.news.core.api.NewsApi;
import com.github.gregbiv.news.core.model.Category;
import com.github.gregbiv.news.core.model.Source;
import com.github.gregbiv.news.core.provider.NewsContract;
import com.github.gregbiv.news.core.provider.NewsDatabase;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.BriteDatabase.Transaction;

import android.accounts.Account;
import android.accounts.AccountManager;

import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;

import android.os.Build;
import android.os.Bundle;

import timber.log.Timber;

public class NewsSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String STATE_PERIODIC = "state_periodic";
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private final Context mContext;
    @Inject
    NewsApi                 mNewsApi;
    @Inject
    BriteDatabase           mDatabase;

    public NewsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        BootstrapApplication.component().inject(this);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account   = getSyncAccount(context);
        String  authority = NewsContract.CONTENT_AUTHORITY;
        Bundle  extras    = new Bundle();

        extras.putBoolean(STATE_PERIODIC, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime)
                    .setExtras(extras)
                    .setSyncAdapter(account, authority)
                    .build();

            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, extras, syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        /*
         * Since we've created an account
         */
        NewsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, NewsContract.CONTENT_AUTHORITY, true);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
                              SyncResult syncResult) {
        Timber.d("onPerformSync Called.");

        if (extras.getBoolean(STATE_PERIODIC)) {
            Timber.d("Periodic sync Called.");

            syncSources();
            syncCategories();
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();

        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), NewsContract.CONTENT_AUTHORITY, bundle);
    }

    /**
     * Launch Source sync
     */
    private void syncSources() {
        mNewsApi.sources()
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .subscribe(response -> {
                    Transaction transaction = mDatabase.newTransaction();

                    try {
                        for (Source source : response.result) {
                            mDatabase.insert(NewsDatabase.Tables.SOURCES, new Source.Builder()
                                    .id(source.getId())
                                    .name(source.getName())
                                    .title(source.getTitle())
                                    .description(source.getDescription())
                                    .url(source.getUrl())
                                    .language(source.getLanguage())
                                    .country(source.getCountry())
                                    .category(source.getCategory())
                                    .build());
                        }
                        transaction.markSuccessful();
                    } finally {
                        transaction.end();
                    }
                }, throwable -> {
                    Timber.e(throwable, "Sync sources failed");
                });
    }

    /**
     * Launch Categories sync
     */
    private void syncCategories() {
        mNewsApi.categories()
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .subscribe(categories -> {
                    Transaction transaction = mDatabase.newTransaction();

                    try {
                        for (Category category : categories.result) {
                            mDatabase.insert(NewsDatabase.Tables.CATEGORIES, new Category.Builder()
                                    .id(category.getId())
                                    .title(category.getTitle())
                                    .name(category.getName())
                                    .build());
                        }
                        transaction.markSuccessful();
                    } finally {
                        transaction.end();
                    }
                }, throwable -> {
                    Timber.e(throwable, "Sync categories failed");
                });
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {

        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ((accountManager != null) && (null == accountManager.getPassword(newAccount))) {

            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }
}
