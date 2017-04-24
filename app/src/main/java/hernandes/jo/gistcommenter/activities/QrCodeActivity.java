package hernandes.jo.gistcommenter.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hernandes.jo.gistcommenter.R;
import hernandes.jo.gistcommenter.models.Gist;
import hernandes.jo.gistcommenter.restService.DefaultConectionError;
import hernandes.jo.gistcommenter.restService.GistAPI;
import hernandes.jo.gistcommenter.restService.ServiceCall;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class QrCodeActivity extends AppCompatActivity {


    public static final int PERMISSION_CAMERA = 10;

    @BindView(R.id.barcode_scanner) CompoundBarcodeView barcodeScannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_reader);

        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        ButterKnife.bind(this);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(QrCodeActivity.this,
                    android.Manifest.permission.CAMERA)) {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.camera_permission)
                        .setMessage(R.string.camera_rationale)
                        .setPositiveButton(R.string.positive_dialog, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(QrCodeActivity.this,
                                        new String[]{android.Manifest.permission.CAMERA},
                                        PERMISSION_CAMERA);
                            }
                        })
                        .setNegativeButton(R.string.negative_dialog, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA},
                        PERMISSION_CAMERA);

            }
        } else setupBarcodeReader();
    }


    private void setupBarcodeReader(){
        barcodeScannerView = (CompoundBarcodeView)findViewById(R.id.barcode_scanner);
        barcodeScannerView.removeViewAt(1);
        barcodeScannerView.setStatusText("");

        barcodeScannerView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(final BarcodeResult result) {
                MediaActionSound sound;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    sound = new MediaActionSound();
                    sound.play(MediaActionSound.SHUTTER_CLICK);
                }
                checkGistWithId(result.getText());
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {}
        });

    }




    @Override
    protected void onResume() {
        super.onResume();
        try {
            barcodeScannerView.resume();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            barcodeScannerView.pause();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull  int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    finish();
                    startActivity(getIntent());
                } else {
                    finish();
                    Toast.makeText(this, "Não é possível utilizar o leitor de QR Code. Uso da camêra não permitido pelo usuário", Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    public void checkGistWithId(String gistId){
        ServiceCall
                .getService(GistAPI.class)
                .getSingleGist(gistId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Gist>() {
                    @Override
                    public void call(Gist gist) {
                        Intent i = new Intent(QrCodeActivity.this, GistActivity.class);
                        i.putExtra(GistActivity.EXTRA_GIST, gist);
                        startActivity(i);
                    }
                }, new DefaultConectionError(this));
    }

}

