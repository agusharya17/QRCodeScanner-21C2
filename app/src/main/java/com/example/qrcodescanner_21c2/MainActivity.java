package com.example.qrcodescanner_21c2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatToggleButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //view Objects
    private Button buttonScan;
    private TextView textViewNama, textViewKelas, textViewNIM;
    //qr code scanner
    private IntentIntegrator qrScan;
    @SuppressLint("CutPasteID")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // View Object
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewNama = (TextView) findViewById(R.id.textViewNama);
        textViewKelas = (TextView) findViewById(R.id.textViewKelas);
        textViewNIM = (TextView) findViewById(R.id.textViewNIM);

        //insialisasi scan object
        qrScan = new IntentIntegrator(this);

        //implementasi onclick listener
        buttonScan.setOnClickListener(this);
        // Meminta izin untuk mengakses panggilan telepon
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, 1);
        }
}
    //untuk hasil scanning
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //jika qrcode tidak ada sama sekali
            if (result.getContents() == null) {
                Toast.makeText(this, "Hasil SCANNING tidak ada", Toast.LENGTH_LONG).show();

            } else {
                //jika QRCode ada atau ditemukan data
                //1.Logika jika data yang masuk url http://...
                String url = result.getContents();
                String address;
                String http = "http://";
                String https = "https://";
                address = result.getContents();
                if (address.contains(http) || address.contains(https)) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
                // 2.Untuk Logika Email
                String alamat = result.getContents();
                String at = "@gmail";
                if (alamat.contains(at)) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    String[] recipients = {alamat.replace("http://", "")};
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Email");
                    intent.putExtra(Intent.EXTRA_TEXT, "Type Here");
                    intent.putExtra(Intent.EXTRA_CC, "");
                    intent.setType("text/html");
                    intent.setPackage("com.google.android.gm");
                    startActivity(Intent.createChooser(intent, "Send mail"));
                }
                //3. Logika jika Nomor Telepon ada untuk persiapan Telepon
                try {
                    Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse(result.getContents()));
                    startActivity(intent2);
                } catch (Exception e2) {
                    Toast.makeText(this, "Not Scanned", Toast.LENGTH_LONG).show();
                }
                String number;
                number = result.getContents();
                if (number.matches("^[0-9,+]*$") && number.length() > 10) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    Intent dialIntent = new Intent(Intent.ACTION_CALL);
                    dialIntent.setData(Uri.parse("tel:" + number));
                    dialIntent.setPackage("https://api.whatsapp.com/send?phone=%s&text=%s" + number);
                    callIntent.setData(Uri.parse("tel:" + number));
                    startActivity(callIntent);
                    startActivity(dialIntent);
                }

                // 4. Maps
                // Mengecek apakah data yang di scan merupakan lokasi
                if (result.getContents().contains("geo:")) {
                // Memisahkan latitude dan longitude dari data yang di scan
                    String[] geoLocation = result.getContents().split(":")[1].split("\\?")[0].split(",");
                    double latitude = Double.parseDouble(geoLocation[0]);
                    double longitude = Double.parseDouble(geoLocation[1]);
                    }
                        //jika qrcode ada/ditemukan datanya
                        try {
                            //Konversi datanya ke json
                            JSONObject obj = new JSONObject(result.getContents());
                            //di set nilai datanya ke textview
                            textViewNama.setText(obj.getString("Nama"));
                            textViewKelas.setText(obj.getString("Kelas"));
                            textViewNIM.setText(obj.getString("NIM"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    super.onActivityResult(requestCode, resultCode, data);
                }
            }
            @Override
            public void onClick (View view){
            // Perintah Scanning QRCODE
                qrScan.initiateScan();
            }
        }