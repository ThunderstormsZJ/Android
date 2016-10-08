package cn.spinsoft.wdq.mine.biz;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by hushujun on 15/12/25.
 */
public class PublishVideoBean implements Parcelable, Serializable {
    private String title;
    private String videoUrl;
    private String posterUrl;
    private String desc;
    private int danceType;
    private int userId;
    private int original;
    private String location;
    private double longitude;
    private double latitude;

    public PublishVideoBean() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getDanceType() {
        return danceType;
    }

    public void setDanceType(int danceType) {
        this.danceType = danceType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getOriginal() {
        return original;
    }

    public void setOriginal(int original) {
        this.original = original;
    }

    @Override
    public String toString() {
        return "PublishVideoBean{" +
                "title='" + title + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", desc='" + desc + '\'' +
                ", danceType=" + danceType +
                ", userId=" + userId +
                ", location='" + location + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PublishVideoBean that = (PublishVideoBean) o;

        if (danceType != that.danceType) return false;
        if (userId != that.userId) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (videoUrl != null ? !videoUrl.equals(that.videoUrl) : that.videoUrl != null)
            return false;
        if (posterUrl != null ? !posterUrl.equals(that.posterUrl) : that.posterUrl != null)
            return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        return !(location != null ? !location.equals(that.location) : that.location != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = title != null ? title.hashCode() : 0;
        result = 31 * result + (videoUrl != null ? videoUrl.hashCode() : 0);
        result = 31 * result + (posterUrl != null ? posterUrl.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + danceType;
        result = 31 * result + userId;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.videoUrl);
        dest.writeString(this.posterUrl);
        dest.writeString(this.desc);
        dest.writeInt(this.danceType);
        dest.writeInt(this.userId);
        dest.writeInt(this.original);
        dest.writeString(this.location);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
    }

    protected PublishVideoBean(Parcel in) {
        this.title = in.readString();
        this.videoUrl = in.readString();
        this.posterUrl = in.readString();
        this.desc = in.readString();
        this.danceType = in.readInt();
        this.userId = in.readInt();
        this.original = in.readInt();
        this.location = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
    }

    public static final Creator<PublishVideoBean> CREATOR = new Creator<PublishVideoBean>() {
        public PublishVideoBean createFromParcel(Parcel source) {
            return new PublishVideoBean(source);
        }

        public PublishVideoBean[] newArray(int size) {
            return new PublishVideoBean[size];
        }
    };
}
