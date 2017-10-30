
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    private int      id;
    @Expose
    private String   author;
    @Expose
    private String   title;
    @Expose
    private String   description;
    @Expose
    private String   url;
    @Expose
    private String   urlToImage;
    @Expose
    private String   publishedAt;
    @Expose
    @SerializedName("category")
    private int      categoryId;
    private Category category;
    @Expose
    @SerializedName("source")
    private int      sourceId;
    private Source   source;

    protected Article(Parcel in) {
        this.id          = in.readInt();
        this.author      = in.readString();
        this.title       = in.readString();
        this.description = in.readString();
        this.url         = in.readString();
        this.urlToImage  = in.readString();
        this.publishedAt = in.readString();
        this.categoryId  = in.readInt();
        this.category    = in.readParcelable(Category.class.getClassLoader());
        this.sourceId    = in.readInt();
        this.source      = in.readParcelable(Source.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.author);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.urlToImage);
        dest.writeString(this.publishedAt);
        dest.writeInt(this.categoryId);
        dest.writeParcelable(this.category, flags);
        dest.writeInt(this.sourceId);
        dest.writeParcelable(this.source, flags);
    }

    public String getAuthor() {
        return author;
    }

    public Article setAuthor(String author) {
        this.author = author;

        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Article setCategory(Category category) {
        this.category = category;

        return this;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public Article setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Article setDescription(String description) {
        this.description = description;

        return this;
    }

    public Integer getId() {
        return id;
    }

    public Article setId(Integer id) {
        this.id = id;

        return this;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public Article setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;

        return this;
    }

    public Source getSource() {
        return source;
    }

    public Article setSource(Source source) {
        this.source = source;

        return this;
    }

    public int getSourceId() {
        return sourceId;
    }

    public Article setSourceId(int sourceId) {
        this.sourceId = sourceId;
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
        @Expose
        public List<Article>      articles = new ArrayList<>();
        public int                limit;
        public int                offset;
        public int                nbTotal;
        public ArrayList<Integer> ids;
        public String             status;
    }
}
