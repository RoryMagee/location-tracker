package rory.pmcm01_mobile;


import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable {
    private String id, name;

    public Location(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String x) {
        this.name = x;
    }

    public void setId(String x) {
        this.id = x;
    }


    @Override
    public String toString() {
        return name;
    }

    public Location(Parcel parcel) {
        this.id = parcel.readString();
        this.name = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
    }
}
