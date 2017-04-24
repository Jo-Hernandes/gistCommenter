package hernandes.jo.gistcommenter.utils;


import android.content.Context;
import android.content.SharedPreferences;

import hernandes.jo.gistcommenter.models.AccessToken;

public class TokenPersistence {

    private static final String SESSION_PREFERENCES = "sessionToken_saved";
    private static final String SESSION_TOKEN = "session_auth_token";
    private static final String TOKEN_TYPE = "type_of_saved_token";
    private static final String EXISTS_TOKEN = "is_there_a_saved_token";


    public static void saveAccessToken(AccessToken token, Context context){

        SharedPreferences.Editor editor = context.getSharedPreferences(SESSION_PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putString(SESSION_TOKEN, token.getAccessToken());
        editor.putString(TOKEN_TYPE, token.getTokenType());
        editor.putBoolean(EXISTS_TOKEN, token != null);
        editor.apply();
    }

    public static AccessToken retrieveAccessToken(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SESSION_PREFERENCES, Context.MODE_PRIVATE);
        String restoredToken = prefs.getString(SESSION_TOKEN, null);
        String token_type = prefs.getString(TOKEN_TYPE, null);
        if (restoredToken != null && token_type != null) {
            return AccessToken.newInstance(restoredToken, token_type);
        } else
            return null;
    }

    public static boolean existsUser(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SESSION_PREFERENCES, Context.MODE_PRIVATE);
        return prefs.getBoolean(EXISTS_TOKEN, false);
    }


}
