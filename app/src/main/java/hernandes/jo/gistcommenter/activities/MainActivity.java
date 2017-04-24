package hernandes.jo.gistcommenter.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import java.io.IOException;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hernandes.jo.gistcommenter.R;
import hernandes.jo.gistcommenter.utils.TokenPersistence;
import hernandes.jo.gistcommenter.models.AccessToken;
import hernandes.jo.gistcommenter.oauthServices.LoginService;
import hernandes.jo.gistcommenter.oauthServices.LoginServiceCall;
import hernandes.jo.gistcommenter.restService.DefaultConnectionError;
import hernandes.jo.gistcommenter.utils.LoadingAnimation;
import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    private static final String clientId = "43cbd224bd77ca5034e9";
    private static final  String clientSecret = "c38ceb96a76311acfe9de410c36b66e9e32bb9bd";
    private static final String redirectUri = "gistcommenterapp://hernandes.jo.gistcommenter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_mainActivity_login)
    public void loginUser(){

        boolean isLogged = TokenPersistence.existsUser(this);

        if (!isLogged) {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(LoginServiceCall.API_BASE_URL + "login/oauth/authorize" + "?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&scope=gist,user"));
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.attention_label)
                    .setMessage(R.string.authenticated_user_error)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        }
    }


    @OnClick(R.id.button_mainActivity_scanGist)
    public void scanGistQRCode(){
        startActivity(new Intent(this, QrCodeActivity.class));
    }

    public void showErrorMessage(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_gettingToken_title)
                .setMessage(R.string.connection_error_genericMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {

                final LoadingAnimation loadingAnimation = new LoadingAnimation(((ViewGroup) getWindow().getDecorView()));
                loadingAnimation.start();
                LoginServiceCall.createService(LoginService.class, clientId, clientSecret)
                        .getAccessToken(code, "authorization_code")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .doOnTerminate(new Action0() {
                            @Override
                            public void call() {
                                loadingAnimation.stop();
                            }
                        })
                        .subscribe(new Action1<ResponseBody>() {
                            @Override
                            public void call(ResponseBody accessToken) {
                                try {
                                    String query =  accessToken.string();
                                    UrlQuerySanitizer sanitzer = new UrlQuerySanitizer(query);
                                    sanitzer.parseQuery(query);

                                    AccessToken token = AccessToken.newInstance(sanitzer.getValue("access_token"), sanitzer.getValue("token_type"));
                                    TokenPersistence.saveAccessToken(token, MainActivity.this);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    showErrorMessage();
                                }
                            }
                        }, new DefaultConnectionError(this));

            } else if (uri.getQueryParameter("error") != null) {
                showErrorMessage();
            }
        }
    }
}
