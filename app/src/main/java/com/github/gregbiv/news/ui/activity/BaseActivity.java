package com.github.gregbiv.news.ui.activity;

import javax.inject.Inject;

import com.github.gregbiv.news.BootstrapApplication;
import com.github.gregbiv.news.R;

import com.squareup.otto.Bus;

import android.os.Bundle;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

import timber.log.Timber;

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

    @CallSuper
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BootstrapApplication.get(this).getRefWatcher().watch(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
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
}
