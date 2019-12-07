package cc.kevinliao.xixi.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class OutputBean implements Parcelable {
    private String text;
    private String path;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.text);
        dest.writeString(this.path);
    }

    public OutputBean() {
    }

    protected OutputBean(Parcel in) {
        this.text = in.readString();
        this.path = in.readString();
    }

    public static final Parcelable.Creator<OutputBean> CREATOR = new Parcelable.Creator<OutputBean>() {
        @Override
        public OutputBean createFromParcel(Parcel source) {
            return new OutputBean(source);
        }

        @Override
        public OutputBean[] newArray(int size) {
            return new OutputBean[size];
        }
    };
}
