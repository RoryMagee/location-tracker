package rory.pmcm01_mobile;


import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    public static final Creator<User> CREATOR = new Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int i) {
            return new User[i];
        }
    };

    private String id, firstName, lastName;

    public User(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    public void setId(String x) { this.id = x; }
    public void setFirstName(String x) { this.firstName = x; }
    public void setLastName(String x) { this.lastName = x; }

    public User(Parcel parcel) {
        this.id = parcel.readString();
        this.firstName = parcel.readString();
        this.lastName = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
    }


}
