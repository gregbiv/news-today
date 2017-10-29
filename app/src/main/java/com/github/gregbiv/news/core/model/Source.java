
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.model;

//~--- non-JDK imports --------------------------------------------------------

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import com.github.gregbiv.news.core.provider.meta.SourceMeta;

//~--- JDK imports ------------------------------------------------------------

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Source implements Serializable, Parcelable, SourceMeta {
    private static final long             serialVersionUID = -104137709256566564L;
    public static final Creator<Source> CREATOR            = new Creator<Source>() {
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
    private String description;
    @Expose
    private String url;
    @Expose
    private String category;
    @Expose
    private String language;
    @Expose
    private String country;

    public Source() {}

    protected Source(Parcel in) {
        this.id          = in.readInt();
        this.name        = in.readString();
        this.description = in.readString();
        this.url         = in.readString();
        this.category    = in.readString();
        this.language    = in.readString();
        this.country     = in.readString();
    }

    public int getId() {
        return id;
    }

    public Source setId(int id) {
        this.id = id;
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

    public String getCategory() {
        return category;
    }

    public Source setCategory(String category) {
        this.category = category;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.category);
        dest.writeString(this.language);
        dest.writeString(this.country);
    }

    public static final class Response {
        @Expose
        public List<Source> sources = new ArrayList<>();
        public String       status;
    }
}
