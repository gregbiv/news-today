
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news;

import com.squareup.leakcanary.RefWatcher;

import android.app.Application;

import android.content.Context;

/**
 * Article application
 */
public abstract class BootstrapApplication extends Application {
    private static BootstrapApplication instance;
    private BootstrapComponent          component;
    private RefWatcher                  refWatcher;

    /**
     * Create main application
     */
    public BootstrapApplication() {}

    public static BootstrapComponent component() {
        return instance.component;
    }

    protected abstract void init();

    protected RefWatcher installLeakCanary() {

        // return LeakCanary.install(this);
        return RefWatcher.DISABLED;
    }

    protected abstract void onAfterInjection();

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        instance   = this;
        refWatcher = installLeakCanary();

        // Perform injection
        // Injector.init(this, )
        component = DaggerComponentInitializer.init();
        onAfterInjection();
    }

    public BootstrapComponent getComponent() {
        return component;
    }

    public static BootstrapApplication get(Context context) {
        return (BootstrapApplication) context.getApplicationContext();
    }

    public static BootstrapApplication getInstance() {
        return instance;
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }

    public final static class DaggerComponentInitializer {
        public static BootstrapComponent init() {
            return DaggerBootstrapComponent.builder()
                                           .androidModule(new AndroidModule())
                                           .bootstrapModule(new BootstrapModule())
                                           .build();
        }
    }
}
