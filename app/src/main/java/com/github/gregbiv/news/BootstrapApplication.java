package com.github.gregbiv.news;

//~--- non-JDK imports --------------------------------------------------------

import android.app.Application;

import android.content.Context;

import com.squareup.leakcanary.RefWatcher;

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

    public static BootstrapApplication get(Context context) {
        return (BootstrapApplication) context.getApplicationContext();
    }

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

    public static BootstrapComponent component() {
        return instance.component;
    }

    protected abstract void onAfterInjection();

    protected abstract void init();

    public static BootstrapApplication getInstance() {
        return instance;
    }

    public BootstrapComponent getComponent() {
        return component;
    }

    protected RefWatcher installLeakCanary() {

        // return LeakCanary.install(this);
        return RefWatcher.DISABLED;
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }

    public final static class DaggerComponentInitializer {
        public static BootstrapComponent init() {
            return DaggerBootstrapComponent.builder().androidModule(new AndroidModule()).bootstrapModule(
                new BootstrapModule()).build();
        }
    }
}
