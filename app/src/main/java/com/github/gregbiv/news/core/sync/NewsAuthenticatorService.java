
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.sync;

import android.app.Service;

import android.content.Intent;

import android.os.IBinder;

/**
 * The service which allows the sync adapter framework to access the authenticator.
 */
public class NewsAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private NewsAuthenticator mAuthenticator;

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new NewsAuthenticator(this);
    }
}
