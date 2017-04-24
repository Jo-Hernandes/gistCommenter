package hernandes.jo.gistcommenter.restService;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import hernandes.jo.gistcommenter.R;
import retrofit2.adapter.rxjava.HttpException;
import rx.functions.Action1;


public class DefaultConnectionError implements Action1<Throwable> {

  private Context context;

  public DefaultConnectionError(Context context){
    this.context = context;
  }

  @Override
  public void call(Throwable throwable) {
    if (throwable instanceof HttpException) {
      try {
        JsonObject obj = (new JsonParser()).parse(((HttpException) throwable).response().errorBody().string() + "").getAsJsonObject();

        new AlertDialog.Builder(this.context)
            .setTitle(context.getResources().getString(R.string.connection_error_title))
            .setMessage(obj.get("message").toString())
            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
              }
            }).show();

      } catch (Exception e) {
        e.printStackTrace();
        new AlertDialog.Builder(this.context)
                .setTitle(context.getResources().getString(R.string.connection_error_title))
                .setMessage(context.getResources().getString(R.string.connection_error_genericMessage))
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

        }
    } else new AlertDialog.Builder(this.context)
        .setTitle(context.getResources().getString(R.string.connection_error_title))
        .setMessage(context.getResources().getString(R.string.connection_error_genericMessage))
        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
          }
        }).show();
    throwable.printStackTrace();
  }
}
