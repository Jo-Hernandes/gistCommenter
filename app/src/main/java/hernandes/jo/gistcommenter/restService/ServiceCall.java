package hernandes.jo.gistcommenter.restService;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceCall {


    public static Retrofit r;
    private static final String API_URL = "https://api.github.com/";


    static{

        OkHttpClient client = new OkHttpClient.Builder().build();

        r = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(API_URL)
                .build();
    }


    public static <S> S getService(Class<S> serviceClass){
        return r.create(serviceClass);
    }




}
