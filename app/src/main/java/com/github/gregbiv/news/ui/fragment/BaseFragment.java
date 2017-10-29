
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.ui.fragment;

//~--- non-JDK imports --------------------------------------------------------

import android.os.Bundle;

import android.support.annotation.CallSuper;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import android.view.View;

import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.squareup.leakcanary.RefWatcher;

import com.github.gregbiv.news.BootstrapApplication;

//~--- JDK imports ------------------------------------------------------------

import java.util.Collections;
import java.util.List;

/**
 * Base class for all fragments.
 * Binds views, watches memory leaks and performs dependency injections
 *
 * @see ButterKnife
 * @see RefWatcher
 */
public abstract class BaseFragment extends Fragment {
    private Toast mToast;
    private Unbinder unbinder;

    @CallSuper
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @CallSuper
    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        super.onDestroy();
        BootstrapApplication.get(getActivity()).getRefWatcher().watch(this);
    }

    protected void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    protected void showToast(@StringRes int resId) {
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT);
        mToast.show();
    }

    protected List<Object> getModules() {
        return Collections.emptyList();
    }
}
