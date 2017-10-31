
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news;

import com.crashlytics.android.Crashlytics;

import com.github.gregbiv.news.logging.CrashlyticsTree;

import io.fabric.sdk.android.Fabric;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

public class BootstrapApplicationImpl extends BootstrapApplication {
    @Override
    protected void init() {

        // Start Crashlytics.
        Fabric.with(this, new Crashlytics());

        // Set the type of logger, crashlytics in release mode
        Timber.plant(new CrashlyticsTree());
    }

    protected RefWatcher installLeakCanary() {
        return LeakCanary.install(this);
    }

    @Override
    protected void onAfterInjection() {}
}
