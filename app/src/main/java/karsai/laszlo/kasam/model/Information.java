package karsai.laszlo.kasam.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Information implements Parcelable {

    private String header;
    private String content;
    private boolean isExpanded;

    public Information(String header, String content) {
        this.header = header;
        this.content = content;
        this.isExpanded = false;
    }

    private Information(Parcel in) {
        header = in.readString();
        content = in.readString();
        isExpanded = in.readByte() != 0;
    }

    public static final Creator<Information> CREATOR = new Creator<Information>() {
        @Override
        public Information createFromParcel(Parcel in) {
            return new Information(in);
        }

        @Override
        public Information[] newArray(int size) {
            return new Information[size];
        }
    };

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getHeader() {
        return header;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(header);
        parcel.writeString(content);
        parcel.writeByte((byte) (isExpanded ? 1 : 0));
    }
}
