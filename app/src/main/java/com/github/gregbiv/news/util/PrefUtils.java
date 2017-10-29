
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.util;

import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;

/**
 * Utilities and constants related to app preferences.
 */
public final class PrefUtils {
    public static final String PREF_BROWSE_NEWS_MODE  = "pref_browse_news_mode";
    public static final String PREF_SELECTED_POSITION = "pref_selected_position";
    public static final String MODE_FEED              = "mode_feed";

    public static void clear(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().clear().apply();
    }

    public static String getBrowseNewsMode(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getString(PREF_BROWSE_NEWS_MODE, MODE_FEED);
    }

    public static void setBrowseNewsMode(final Context context, String mode) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().putString(PREF_BROWSE_NEWS_MODE, mode).apply();
    }

    public static int getSelectedPosition(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getInt(PREF_SELECTED_POSITION, -1);
    }

    public static void setSelectedPosition(final Context context, int position) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        sp.edit().putInt(PREF_SELECTED_POSITION, position).apply();
    }
}
