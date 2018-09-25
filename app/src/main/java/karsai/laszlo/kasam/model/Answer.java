package karsai.laszlo.kasam.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Answer implements Parcelable{
    private int originalValue;
    private int addedValue;

    public Answer(int originalValue, int addedValue) {
        this.originalValue = originalValue;
        this.addedValue = addedValue;
    }

    private Answer(Parcel in) {
        originalValue = in.readInt();
        addedValue = in.readInt();
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

    @Override
    public String toString() {
        if (this.originalValue != this.addedValue) {
            return "* -> "
                    + "original: "
                    + this.originalValue
                    + ", added in real: "
                    + this.addedValue;
        } else {
            return " -> "
                    + this.originalValue;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(originalValue);
        parcel.writeInt(addedValue);
    }
}
