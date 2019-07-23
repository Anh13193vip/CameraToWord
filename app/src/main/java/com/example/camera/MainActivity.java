package com.example.camera;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    ImageView imgView;
    TessBaseAPI m_tess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            prepareLanguageDir();
            m_tess = new TessBaseAPI();
            m_tess.init(getFilesDir() + "", "vie");
        } catch (Exception e) {
            // Logging here
        }
    }

    public void clickToTakePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);

    }


    // copy từ aset
    private void copyFile() throws IOException {
        // work with assets folder
        AssetManager assMng = getAssets();
        InputStream is = assMng.open("tessdata/vie.traineddata");
        OutputStream os = new FileOutputStream(getFilesDir() +
                "/tessdata/vie.traineddata");
        byte[] buffer = new byte[1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
            os.write(buffer, 0, read);
        }

        is.close();
        os.flush();
        os.close();
    }

    private void prepareLanguageDir() throws IOException {
        File dir = new File(getFilesDir() + "/tessdata");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File trainedData = new File(getFilesDir() + "/tessdata/vie.traineddata");
        if (!trainedData.exists()) {
            copyFile();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CANCELED){
            if (data != null) {
                if (m_tess == null) {
                    return;
                }

//                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                imgView.setImageBitmap(bitmap);

                try {
                    m_tess.setImage((Bitmap) data.getExtras().get("data"));
//                    m_tess.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cap));
                    String result = m_tess.getUTF8Text();
                    TextView resultView = (TextView) findViewById(R.id.txt_result);
                    resultView.setText(result);
                } catch (Exception e) {
                }

            }
        }
    }
}
