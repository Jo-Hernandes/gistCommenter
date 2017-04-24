
package hernandes.jo.gistcommenter.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Owner implements Parcelable {

    @SerializedName("login")
    @Expose
    private String login;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("avatar_url")
    @Expose
    private String avatarUrl;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("html_url")
    @Expose
    private String htmlUrl;
    @SerializedName("type")
    @Expose
    private String type;
    public final static Parcelable.Creator<Owner> CREATOR = new Creator<Owner>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Owner createFromParcel(Parcel in) {
            Owner instance = new Owner();
            instance.login = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.avatarUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            instance.htmlUrl = ((String) in.readValue((String.class.getClassLoader())));
            instance.type = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Owner[] newArray(int size) {
            return (new Owner[size]);
        }

    };

    public String getLogin() {
        return login;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(login);
        dest.writeValue(id);
        dest.writeValue(avatarUrl);
        dest.writeValue(url);
        dest.writeValue(htmlUrl);
        dest.writeValue(type);
    }

    public int describeContents() {
        return  0;
    }

}
