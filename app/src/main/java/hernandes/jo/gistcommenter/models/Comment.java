
package hernandes.jo.gistcommenter.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("user")
    @Expose
    private Owner user;
    public final static Parcelable.Creator<Comment> CREATOR = new Creator<Comment>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Comment createFromParcel(Parcel in) {
            Comment instance = new Comment();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.body = ((String) in.readValue((String.class.getClassLoader())));
            instance.user = ((Owner) in.readValue((Owner.class.getClassLoader())));
            return instance;
        }

        public Comment[] newArray(int size) {
            return (new Comment[size]);
        }

    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Owner getUser() {
        return user;
    }

    public void setUser(Owner user) {
        this.user = user;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(body);
        dest.writeValue(user);
    }

    public int describeContents() {
        return  0;
    }

}
