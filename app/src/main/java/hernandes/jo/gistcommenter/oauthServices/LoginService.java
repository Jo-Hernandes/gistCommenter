package hernandes.jo.gistcommenter.oauthServices;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

public interface LoginService {


    @FormUrlEncoded
    @POST("login/oauth/access_token")
    Observable<ResponseBody> getAccessToken(
            @Field("code") String code,
            @Field("grant_type") String grantType);
}
