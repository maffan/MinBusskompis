package se.grupp4.minbusskompis;

        import android.os.Parcel;
        import android.os.Parcelable;
        import com.google.android.gms.maps.model.LatLng;

public class TravelingData implements Parcelable {
    public LatLng bussStopCoordinates;
    public LatLng destinationCoordinates;
    public String destinationName = "Defaultstorp";
    public String bussStationName = "Defaultsgatan";
    public String busStopName = "Defaultsvägen";
    public String bussStationChar = "D";
    public String bussName = "666";
    public String busLeavingAt = "12:00:00";
    public String busArrivingAt = "13:30:00";

    public static final int INACTIVE = 0;
    public static final int WALKING = 1;
    public static final int AT_BUS_STATION = 2;
    public static final int ON_BUS = 3;
    public static final int LEAVING_BUS = 4;

    public TravelingData() {
    }

    protected TravelingData(Parcel in) {
        bussStopCoordinates = (LatLng) in.readValue(LatLng.class.getClassLoader());
        destinationCoordinates = (LatLng) in.readValue(LatLng.class.getClassLoader());
        destinationName = in.readString();
        bussStationName = in.readString();
        busStopName = in.readString();
        bussStationChar = in.readString();
        bussName = in.readString();
        busLeavingAt = in.readString();
        busArrivingAt = in.readString();
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
        dest.writeString(busStopName);
        dest.writeString(bussStationChar);
        dest.writeString(bussName);
        dest.writeString(busLeavingAt);
        dest.writeString(busArrivingAt);
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
                ", bussStopName='" + busStopName + '\'' +
                ", bussStationChar='" + bussStationChar + '\'' +
                ", bussName='" + bussName + '\'' +
                ", busLeavingAt=" + busLeavingAt + '\'' +
                ", busArrivingAt=" + busArrivingAt +
                '}';
    }
}