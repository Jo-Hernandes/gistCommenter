package hernandes.jo.gistcommenter.models;


import android.support.annotation.NonNull;

public class AccessToken {


    private String accessToken;
    private String tokenType;

    private AccessToken(String accessToken, String tokenType){
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    public static AccessToken newInstance(@NonNull String accessToken, @NonNull String tokenType){
        return new AccessToken(accessToken, tokenType);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        if (! Character.isUpperCase(tokenType.charAt(0))) {
            tokenType =
                    Character
                            .toString(tokenType.charAt(0))
                            .toUpperCase() + tokenType.substring(1);
        }

        return tokenType;
    }
}
