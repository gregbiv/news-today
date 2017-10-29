
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author  Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.gregbiv.news.core.provider.meta.CategoryMeta;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable, Parcelable, CategoryMeta {
    private static final long           serialVersionUID = -104137709256566564L;
    public static final Creator<Category> CREATOR          = new Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
    @Expose
    private int    id;
    @Expose
    private String name;
    @Expose
    private String title;

    public Category() {}

    protected Category(Parcel in) {
        this.id    = in.readInt();
        this.name  = in.readString();
        this.title = in.readString();
    }

    public int getId() {
        return id;
    }

    public Category setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Category setName(String name) {
        this.name = name;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Category setTitle(String title) {
        this.title = title;
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
        dest.writeString(this.title);
    }

    public static final class Response {
        @Expose
        public List<Category> result = new ArrayList<>();
        public String         response;
    }
}
