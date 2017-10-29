
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.util;

//~--- non-JDK imports --------------------------------------------------------

import android.content.res.Resources;

import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;

import android.util.TypedValue;

import timber.log.Timber;

public final class ResourceUtils {
    private ResourceUtils() {
        throw new AssertionError("No instances.");
    }

    public static float getFloatDimension(@NonNull Resources resources, @DimenRes int dimenRes) {
        TypedValue value = new TypedValue();

        resources.getValue(dimenRes, value, true);
        Timber.d("Value type: " + (value.type == TypedValue.TYPE_FLOAT));

        return value.getFloat();
    }
}
