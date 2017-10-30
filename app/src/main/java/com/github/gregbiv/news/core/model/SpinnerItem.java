
/**
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 *
 * @author Gregory Kornienko <gregbiv@gmail.com>
 * @license MIT
 */
package com.github.gregbiv.news.core.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SpinnerItem implements Parcelable {
    public static final Creator<SpinnerItem> CREATOR = new Creator<SpinnerItem>() {
        @Override
        public SpinnerItem createFromParcel(Parcel in) {
            return new SpinnerItem(in);
        }

        @Override
        public SpinnerItem[] newArray(int size) {
            return new SpinnerItem[size];
        }
    };
    boolean isHeader;
    String mode, title;
    boolean indented;

    protected SpinnerItem(Parcel in) {
        this.isHeader = in.readByte() != 0;
        this.mode = in.readString();
        this.title = in.readString();
        this.indented = in.readByte() != 0;
    }

    public SpinnerItem(boolean isHeader, String mode, String title, boolean indented) {
        this.isHeader = isHeader;
        this.mode = mode;
        this.title = title;
        this.indented = indented;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (this.isHeader
                ? 1
                : 0));
        dest.writeString(this.mode);
        dest.writeString(this.title);
        dest.writeByte((byte) (this.indented
                ? 1
                : 0));
    }

    public boolean isHeader() {
        return isHeader;
    }

    public boolean isIndented() {
        return indented;
    }

    public void setIndented(boolean indented) {
        this.indented = indented;
    }

    public void setIsHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
