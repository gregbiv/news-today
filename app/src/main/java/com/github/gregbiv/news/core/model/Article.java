
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {
    public static final Creator<Article> CREATOR = new Creator<Article>() {
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
    @Expose
    private String author;
    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private String url;
    @Expose
    private String urlToImage;
    @Expose
    private String publishedAt;

    protected Article(Parcel in) {
        this.author      = in.readString();
        this.title       = in.readString();
        this.description = in.readString();
        this.url         = in.readString();
        this.urlToImage  = in.readString();
        this.publishedAt = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.urlToImage);
        dest.writeString(this.publishedAt);
    }

    public String getAuthor() {
        return author;
    }

    public Article setAuthor(String author) {
        this.author = author;

        return this;
    }

    public String getDescription() {
        return description;
    }

    public Article setDescription(String description) {
        this.description = description;

        return this;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public Article setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;

        return this;
    }

    public String getTitle() {
        return title;
    }

    public Article setTitle(String title) {
        this.title = title;

        return this;
    }

    public String getUrl() {
        return url;
    }

    public Article setUrl(String url) {
        this.url = url;

        return this;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public Article setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;

        return this;
    }

    public static final class Response {
        public List<Article> articles = new ArrayList<>();
        @Expose
        public String        status;
        public String        source;
        public String        sortBy;
    }
}
