package com.example.imagetotextapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    //progress dialog
    ProgressDialog pd;
    //Firestore insdialog
    FirebaseFirestore db;
    //

    String pId,pFilename,pText;



    Button btn_post,btn_list;


    Login user = new Login();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    EditText mResultEt,mFilename;
    ImageView mPreviewIv;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("Click + button to insert Image");


        mResultEt =  findViewById(R.id.resultEt);
        mPreviewIv = findViewById(R.id.imageIv);
        mFilename = findViewById(R.id.Filename);
        btn_post = findViewById(R.id.addToDatabase);

        final Bundle bundle  = getIntent().getExtras();
            if(bundle != null){
                //Uodate data
               // actionBar.setTitle("Update Data");
                btn_post.setText("Update");

                pId = bundle.getString("pId");
                pFilename = bundle.getString("pFilename");
                pText = bundle.getString("pText");
                //set data
                mFilename.setText(pFilename);
                mResultEt.setText(pText);


            }
            else{
                //new Data
                actionBar.setTitle("Add Data");


                btn_post.setText("Add to Database");
            }


        //camera permission
        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //storage permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        btn_list = findViewById(R.id.listBtn);

        // firebaseDatabase = FirebaseDatabase.getInstance();
        // databaseReference = firebaseDatabase.getReference("Text");
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

       btn_post.setOnClickListener(new View.OnClickListener() {
       @Override

         public void onClick(View v) {

           Bundle bundle1 = getIntent().getExtras();
           if (bundle != null) {
               //update
               String id = pId;
               String text = mResultEt.getText().toString().trim();
               String Filename = mFilename.getText().toString().trim();
               FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
               String users = user.getEmail();


               //function call to callto update data
               updateData(id,Filename,text,users);
           } else {


               Toast.makeText(MainActivity.this, "Upload", Toast.LENGTH_SHORT).show();
               String text = mResultEt.getText().toString().trim();
               String Filename = mFilename.getText().toString().trim();
               FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
               String users = user.getEmail();
               Toast.makeText(MainActivity.this, users, Toast.LENGTH_SHORT).show();
               uploadData(text, users, Filename);
               //    databaseReference.setValue(text);
               //   postCommet();
           }
       }
    });

       //click btn to start ListActivity\
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ListActivity.class));
                finish();
            }
        });
    }

    private void updateData(String id,String Filename,String text ,String users){
        users= users +" Update";
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String Timer = formatter.format(date);
        //set title of progress bar
        pd.setTitle("Update...");
        //show progress bar when user click save button
        pd.show();

        db.collection("Documents").document(id)
                .update("Filename",
                        Filename,"search",Filename.toLowerCase(),
                        "text",text,
                        "user",users,"Date",Timer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //called when updated successfully
                        pd.dismiss();
                        Toast.makeText(MainActivity.this,"Updated",Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is any error
                        pd.dismiss();
                        //get and show error message
                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT);
                    }
                });
    }

      private void uploadData(String text,String user,String Filename){
          Date date = new Date();

          SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
          String Timer = formatter.format(date);
          Long ID = (System.currentTimeMillis());;
          String id= ID.toString();
          //set title of progress bar
          pd.setTitle("Add text");
          //show progress bar when user click save button
          pd.show();
          //random id for each data to be stored
          //String id = UUID.randomUUID().toString();

          Map<String, Object> doc= new HashMap<>();
          doc.put("id",id);
          doc.put("Filename",Filename);
          doc.put("text",text);
          doc.put("user",user);
          doc.put("Date",Timer);
          doc.put("search",Filename.toLowerCase());


          //add this data
          db.collection("Documents").document(id).set(doc)
                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();

                          Toast.makeText(MainActivity.this, "Upload", Toast.LENGTH_SHORT).show();
                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          pd.dismiss();
                          Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                      }
                  });
      }





    //actionbar menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //handle actionbar item clicks

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addImage) {
            showImageImportDialog();
        }
        if (id == R.id.Logout) {
            FirebaseAuth.getInstance().signOut();
           // user.mAuth.signOut();
            finish();
            startActivity(new Intent(MainActivity.this,Login.class));

        }

        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {
        //items to display in dialog
        String[] items = {" Camera", " Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //set title
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //camera option clicked
                    if (!checkCameraPermission()) {
                        //camera permission not allow, request it
                        requestCameraPermission();
                    } else {
                        //permission allowed, take picture
                        pickCamera();
                    }
                }
                if (which == 1) {
                    //gallery option click
                    if (!checkStoreagePermission()) {
                        //Storage permission not allowed,request it
                        requestStoragePermission();
                    } else {
                        //permission allowed, take picture
                        pickGallery();
                    }
                }

            }
        });
        dialog.create().show();//show dialog
    }

    private void pickGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickCamera() {
        //intent to take image from camera, it will also be save to storage to get high quality image
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");// tittle of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image To text");//description
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoreagePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;

    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        /*Check camera permission and return the result
         *In order to get high quality image we have to save image to external stprage first
         * before inserting to image view that's why storage permission will also be required
         */
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    //handle permission result


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted || writeStorageAccepted) {
                        pickCamera();
                    } else {
                        pickCamera();
                         //Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

//handle image result


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //got image from gallery now crop it
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)//enable image guidlines
                        .start(this);
            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //got image from camera now crop it
                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)//enable image guidlines
                        .start(this);

            }
        }

        //get cropped image
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();//get image uri
                //set image to image view
                mPreviewIv.setImageURI(resultUri);

                //get drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable)mPreviewIv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if(!recognizer.isOperational()){
                    Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
                }
                else{
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    //get text from sb inti; there is no text
                    for(int i = 0 ; i< items.size(); i++){
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());
                        sb.append("\n");
                    }
                    mResultEt.setText(sb.toString());
                }
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                //if there is any error show it
                Exception error = result.getError();
                Toast.makeText(this,""+error,Toast.LENGTH_SHORT).show();
            }
        }



    }
}

