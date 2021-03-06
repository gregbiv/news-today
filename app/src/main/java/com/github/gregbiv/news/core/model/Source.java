
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import com.github.gregbiv.news.core.provider.meta.SourceMeta;

import com.google.gson.annotations.Expose;

import android.os.Parcel;
import android.os.Parcelable;

public class Source implements Serializable, Parcelable, SourceMeta {
    private static final long           serialVersionUID = -104137709256566564L;
    public static final Creator<Source> CREATOR          = new Creator<Source>() {
        public Source createFromParcel(Parcel source) {
            return new Source(source);
        }
        public Source[] newArray(int size) {
            return new Source[size];
        }
    };
    @Expose
    private int    id;
    @Expose
    private String name;
    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private String url;
    @Expose
    private String language;
    @Expose
    private String country;
    @Expose
    private int    category;

    public Source() {}

    protected Source(Parcel in) {
        this.id          = in.readInt();
        this.name        = in.readString();
        this.title       = in.readString();
        this.description = in.readString();
        this.url         = in.readString();
        this.language    = in.readString();
        this.country     = in.readString();
        this.category    = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.language);
        dest.writeString(this.country);
        dest.writeInt(this.category);
    }

    public int getId() {
        return id;
    }

    public Source setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Source setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getName() {
        return name;
    }

    public Source setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Source setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Source setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public Source setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public Source setCountry(String country) {
        this.country = country;
        return this;
    }

    public int getCategory() {
        return category;
    }

    public Source setCategory(int category) {
        this.category = category;

        return this;
    }

    public static final class Response {
        @Expose
        public List<Source> result = new ArrayList<>();
        public String       response;
    }
}
