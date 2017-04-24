package hernandes.jo.gistcommenter.restService;


import com.google.gson.JsonObject;

import java.util.List;

import hernandes.jo.gistcommenter.models.Comment;
import hernandes.jo.gistcommenter.models.Gist;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;


public interface GistAPI {

    @GET("gists/{gistId}")
    Observable<Gist> getSingleGist(@Path("gistId") String gistId);

    @GET("gists/{gistId}/comments")
    Observable<List<Comment>> getGistComments(@Path("gistId") String gistId);

    @POST("gists/{gistId}/comments")
    @Headers("Content-Type: application/vnd.github.V3.raw+json")
    Observable<Void> createGistComment(@Path("gistId") String gistId, @Body JsonObject commentBody, @Query("access_token") String bearerToken );
}
