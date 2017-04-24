
package hernandes.jo.gistcommenter.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gist implements Parcelable {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("owner")
    @Expose
    private Owner owner;
    @SerializedName("html_url")
    @Expose
    private String htmlUrl;
    public final static Parcelable.Creator<Gist> CREATOR = new Creator<Gist>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Gist createFromParcel(Parcel in) {
            Gist instance = new Gist();
            instance.url = ((String) in.readValue((String.class.getClassLoader())));
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            instance.owner = ((Owner) in.readValue((Owner.class.getClassLoader())));
            instance.htmlUrl = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Gist[] newArray(int size) {
            return (new Gist[size]);
        }

    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(url);
        dest.writeValue(id);
        dest.writeValue(description);
        dest.writeValue(owner);
        dest.writeValue(htmlUrl);

    }

    public int describeContents() {
        return  0;
    }

}
