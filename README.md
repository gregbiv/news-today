<a href="https://travis-ci.org/gregbiv/news-today" target="_blank">
<img src="https://travis-ci.org/gregbiv/news-today.svg?branch=master" alt="Travis build status" />
</a>

## Building

The build requires [Gradle](http://www.gradle.org/downloads)
v1.10+ and the [Android SDK](http://developer.android.com/sdk/index.html)
to be installed in your development environment. In addition you'll need to set
the `ANDROID_HOME` environment variable to the location of your SDK:

    export ANDROID_HOME=/path/to/your/android-sdk

After satisfying those requirements, the build is pretty simple:

* Run `gradlew` or `gradle assembleDebug` or `gradle assembleRelease` from the `app` directory to build the APK only
* Run one of the commands above from the root directory to build the app and also run
  the integration tests, this requires a connected Android device or running
  emulator.

You might find that your device doesn't let you install your build if you
already have the version from the Android Market installed. This is standard
Android security as it it won't let you directly replace an app that's been
signed with a different key.  Manually uninstall Android Bootstrap from your device and
you will then be able to install your own built version.


## Acknowledgements

News Today is built on the REST API [News-API](https://github.com/gregbiv/news-api)
and uses many great open-source libraries from the Android dev community:

* [AppCompat](http://www.youtube.com/watch?v=6TGgYqfJnyc) for a
  consistent, great looking header across all Android platforms
* [Dagger](https://github.com/square/dagger) for dependency-injection.
* [ButterKnife](https://github.com/JakeWharton/butterknife) for view injection
* [Otto](https://github.com/square/otto) as the event bus
  for driving our app during integration tests.
* [Retrofit](http://square.github.io/retrofit/) for interacting with
  remote HTTP resources (API's in this case).
* [OkHttp](https://github.com/square/okhttp) part of the interaction with remote API
* [SQLBrite](https://github.com/square/sqlbrite) for sync with local SQLite DB
* [RxJava](https://github.com/ReactiveX/RxJava) to make application reactive
* [Glide](https://github.com/bumptech/glide) to download images

### With Android Studio
The easiest way to build is to install [Android Studio](https://developer.android.com/sdk/index.html) v3.+
with [Gradle](https://www.gradle.org/) v3.4.1
Once installed, then you can import the project into Android Studio:

1. Open `File`
2. Import Project
3. Select `build.gradle` under the project directory
4. Click `OK`

Then, Gradle will do everything for you.

## Contributing

There are several ways you could contribute to the development.

* Pull requests are always welcome! You could contribute code by fixing bugs, adding new features or automated tests.
Take a look at the [bug tracker](https://github.com/gregbiv/news-today/issues?state=open)
for ideas where to start.

For development, it is recommended to use the Android Studio for development which is available for free.
Import the project into the IDE using the build.gradle file. The IDE will resolve dependencies automatically.

## License

MIT License.

