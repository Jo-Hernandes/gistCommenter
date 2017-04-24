package hernandes.jo.gistcommenter.oauthServices;

import android.text.TextUtils;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginServiceCall {

    public static final String API_BASE_URL = "https://github.com/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());



    public static <S> S createService(Class<S> serviceClass, String clientId, String clientSecret) {
        if (!TextUtils.isEmpty(clientId)
                && !TextUtils.isEmpty(clientSecret)) {
            String authToken = Credentials.basic(clientId, clientSecret);
            return createService(serviceClass, authToken);
        }

        return createService(serviceClass, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, final String authToken) {
        Retrofit retrofit = null;

        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = builder
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();
            }
        }
        return retrofit.create(serviceClass);
    }
}
