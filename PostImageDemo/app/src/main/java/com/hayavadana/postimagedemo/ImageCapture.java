package com.hayavadana.postimagedemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ImageCapture extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int MEDIA_TYPE_IMAGE = 1;
    //private Bitmap bitmap;
    //private ImageView imageView;
    private ImageView imgPreview;
    private Uri fileUri;
    private Button btnCapturePic;
    private TextView textView;
    static public ArrayList<PlateNumber> AllPlates = null;
    TextView tvPlateNum;

    private static final String IMAGE_DIRECTORY_NAME = "TestPics";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCapturePic = (Button) findViewById(R.id.btnCapturePicture);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        tvPlateNum = (TextView) findViewById(R.id.plateText);



        if (AllPlates !=null && AllPlates.size() > 0){
            tvPlateNum.setText(AllPlates.get(0).getPlateNum());
        }
        btnCapturePic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });

        this.AllPlates = new ArrayList<PlateNumber>();

        Button btnShowOptions = (Button)findViewById(R.id.btnShowOptions);
        btnShowOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPlateNum.setText("");
                Intent intent = new Intent(ImageCapture.this, ActivityPlatesList.class);
                ImageCapture.this.startActivity(intent);
            }
        });

    }
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AllPlates !=null && AllPlates.size() > 0){
            tvPlateNum.setText(AllPlates.get(0).getPlateNum());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (AllPlates.size() > 0){
            TextView textView = (TextView) findViewById(R.id.plateText);
            textView.setText(AllPlates.get(0).getPlateNum());
        }

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }



    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage(data);

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
  /*  private void launchUploadActivity(boolean isImage) {
        Intent i = new Intent(ImageCapture.this, UploadActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);
    }
*/

    /**
     * Display image from a path to ImageView
     */
    private void previewCapturedImage(Intent data) {
        try {

            imgPreview.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            imgPreview.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] byteFormat = stream.toByteArray();
            // get the base 64 string
            String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
            System.out.println("Sreeni:"+imgString);
            RestServiceInvoker restInvoker = new RestServiceInvoker();
            restInvoker.setTextViewRef(tvPlateNum);
            restInvoker.setHttpMethod("POST");
            restInvoker.setAllPlates(AllPlates);
            restInvoker.setNextActivity(ActivityPlatesList.class);
            restInvoker.setCurActivity(ImageCapture.this);
            restInvoker.setPostRequestParams(imgString);
            restInvoker.execute();

           // textView.getText()

            /**********

            URL url = new URL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(httpMethod);


            conn.setRequestProperty("content-type","application/json; charset=utf-8");
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream ());
            wr.writeBytes(postRequestParams);
            System.out.println("after writing the json object string");


        */


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
               // return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }
        else {
            return null;
        }

        return mediaFile;
    }
    }