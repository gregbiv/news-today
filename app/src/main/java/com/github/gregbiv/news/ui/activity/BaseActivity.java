package com.github.gregbiv.news.ui.activity;

//~--- non-JDK imports --------------------------------------------------------

import android.os.Bundle;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.squareup.otto.Bus;

import com.github.gregbiv.news.BootstrapApplication;
import com.github.gregbiv.news.R;

import timber.log.Timber;

//~--- JDK imports ------------------------------------------------------------

import javax.inject.Inject;

public abstract class BaseActivity extends AppCompatActivity {
    @Inject
    protected Bus bus;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar       mToolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BootstrapApplication.get(this).getRefWatcher().watch(this);
    }

    @CallSuper
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        setupToolbar();
    }

    @Nullable
    public final Toolbar getToolbar() {
        return mToolbar;
    }

    private void setupToolbar() {
        if (mToolbar == null) {
            Timber.w("Didn't find a toolbar");

            return;
        }

        ViewCompat.setElevation(mToolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
    }
}
