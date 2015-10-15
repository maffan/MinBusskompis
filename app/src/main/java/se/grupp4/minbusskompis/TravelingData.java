package se.grupp4.minbusskompis;

        import android.os.Parcel;
        import android.os.Parcelable;
        import com.google.android.gms.maps.model.LatLng;

public class TravelingData implements Parcelable {
    public LatLng bussStopCoordinates;
    public LatLng destinationCoordinates;
    public String destinationName = "null";
    public String bussStationName = "null";
    public String bussStationChar = "null";
    public String bussName = "null";
    public long time = 0;

    public TravelingData() {
    }

    protected TravelingData(Parcel in) {
        bussStopCoordinates = (LatLng) in.readValue(LatLng.class.getClassLoader());
        destinationCoordinates = (LatLng) in.readValue(LatLng.class.getClassLoader());
        destinationName = in.readString();
        bussStationName = in.readString();
        bussStationChar = in.readString();
        bussName = in.readString();
        time = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(bussStopCoordinates);
        dest.writeValue(destinationCoordinates);
        dest.writeString(destinationName);
        dest.writeString(bussStationName);
        dest.writeString(bussStationChar);
        dest.writeString(bussName);
        dest.writeLong(time);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TravelingData> CREATOR = new Parcelable.Creator<TravelingData>() {
        @Override
        public TravelingData createFromParcel(Parcel in) {
            return new TravelingData(in);
        }

        @Override
        public TravelingData[] newArray(int size) {
            return new TravelingData[size];
        }
    };

    @Override
    public String toString() {
        return "TravelingData{" +
                "bussStopCoordinates=" + bussStopCoordinates +
                ", destinationCoordinates=" + destinationCoordinates +
                ", destinationName='" + destinationName + '\'' +
                ", bussStationName='" + bussStationName + '\'' +
                ", bussStationChar='" + bussStationChar + '\'' +
                ", bussName='" + bussName + '\'' +
                ", time=" + time +
                '}';
    }
}