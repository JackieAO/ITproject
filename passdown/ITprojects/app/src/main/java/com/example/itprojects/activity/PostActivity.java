package com.example.itprojects.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itprojects.base.MainActivity;
import com.example.itprojects.bean.Artifact;
import com.example.itprojects.util.OnMultiClickListener;
import com.example.itprojects.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

/**
 * This activity allows the user to create a particular artifact and upload it the Firebase server.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-08-31
 */

public class PostActivity extends AppCompatActivity {

    // Code for onActivityResult
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 0;

    /**
     * Layout component variable
     */

    // Progress bar for posting
    private ProgressBar mPostPB;

    // Artifact's image
    private ImageView mselectImage;

    // Artifact's title
    private EditText mPostTitle;

    // Artifact's description
    private EditText mPostDesc;

    // Click to post button
    private TextView mSubmitBtn;

    // Return back to the previous activity button
    private ImageView mBackBtn;
    private TextView mPrivacyTV;

    private Switch mSwitch;

    /**
     * Firebase variable
     */

    // Reference to the storage
    private StorageReference mStorageRef;

    // Reference to the database's child "Artifact"
    private DatabaseReference mDatabaseArtisRef;

    // Reference to the user's Firebase authentication mUid
    private String mUid;

    // Artifact's image uri for Firebase storage upload and camera and gallery's image selection

    private Uri mUri;
    private Uri photoUri;


    private boolean artifact_Status=false;
    /**
     * Show the images and display the view.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_post);

        // Assign Firebase variables' paths
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseArtisRef = FirebaseDatabase.getInstance().getReference().child("Artifact");
        mUid = FirebaseAuth.getInstance().getUid();

        // Get each component's ID
        mselectImage = findViewById(R.id.imageSelect);
        mPostTitle = findViewById(R.id.titleField);
        mPostDesc = findViewById(R.id.descField);
        mSubmitBtn = findViewById(R.id.submitBtn);
        mBackBtn = findViewById(R.id.btn_back_to_log);
        mPostPB = findViewById(R.id.pb_post);
        mSwitch=findViewById(R.id.switch1);
        mPrivacyTV = findViewById(R.id.photo_info_privacy2);

        // Set private or public view
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    artifact_Status=true;
                    mPrivacyTV.setText("私密");
                    mPrivacyTV.setTextColor(getColor(R.color.golden_d8));

                }
                else {
                    artifact_Status=false;
                    mPrivacyTV.setText("公开");
                    mPrivacyTV.setTextColor(getColor(R.color.colorPrimaryDark));
                }
            }
        });

        // Constantly check title's text is correctly typed
        mPostTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (null != mPostTitle.getLayout() && mPostTitle.getLayout().getLineCount() > 2) {
                    mPostTitle.getText().delete(mPostTitle.getText().length() - 1,
                            mPostTitle.getText().length());
                }
            }
        });

        // Show the image uploaded
        mUri = MainActivity.getImageUri();

        mselectImage.setImageURI(mUri);
        //Select picture
        mselectImage.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                SelectImage();
            }
        });
        // Upload artifact
        mSubmitBtn.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                startPosting();
            }
        });

        // Returns to "mainactivity" activity
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostActivity.this.finish();
                /*Intent PhotoFragmentIntent = new Intent(PostActivity.this,
                        MainActivity.class);
                PhotoFragmentIntent.putExtra("clicked_id", 2);
                startActivity(PhotoFragmentIntent);*/
            }
        });
    }

    /**
     * Upload the new image to the Firebase storage and update informaion to the database.
     *
     */
    private void startPosting() {

        // Retrieve the text edited by the user
        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();

        // Activate the progress bar
        mPostPB.setVisibility(View.VISIBLE);

        // Start uploading
        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mUri != null) {

            // Assign the storage's path
            final StorageReference filepath = mStorageRef.child("Blog_Images").child("images/" +
                    UUID.randomUUID().toString());

            filepath.putFile(mUri).addOnSuccessListener(new OnSuccessListener
                    <UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Deactivate the progress bar
                    mPostPB.setVisibility(View.GONE);

                    // Retrieve the image's downloadable url from the storage
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            // Assign the url
                            Uri downloadUri = uri;

                            // Record current time
                            long millis = System.currentTimeMillis();
                            java.util.Date date = new java.util.Date(millis);

                            // Reference to the key value under database's child "Artifact"
                            String uploadId = mDatabaseArtisRef.push().getKey();
                            // Generate the artifact
                            Artifact artifact = new Artifact(title_val, desc_val,
                                    downloadUri.toString(), date, uploadId,artifact_Status,mUid);
                            // Replace the artifact
                            mDatabaseArtisRef.child(mUid).child(uploadId).setValue(artifact);
                        }
                    });

                    // The process is done
                    Toasty.success(PostActivity.this, "已上传", Toast.LENGTH_LONG)
                            .show();

                    PostActivity.this.finish();
                }
            });
        }
        else {
            mPostPB.setVisibility(View.GONE);

            // Show warning, text is required
            Toasty.warning(PostActivity.this, "请输入文本", Toast.LENGTH_LONG)
                    .show();
        }
    }

    /**
     * This function opens a dialog and allows user to choose upload method base.
     *
     */

    private void SelectImage() {

        // Each options' texts
        final String Camera = getString(R.string.postActivity_camera);
        final String Gallery = getString(R.string.postActivity_gallery);
        final String Cancel = getString(R.string.postActivity_cancel);
        final String AddImage = getString(R.string.postActivity_addImage);

        // Pop-up options menu
        final CharSequence[] items = {Camera, Gallery, Cancel};

        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
        builder.setTitle(AddImage);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals(Camera)) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                            "yyyy_MM_dd_HH_mm_ss");
                    String filename = timeStampFormat.format(new Date());
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, filename);
                    photoUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                }
                else if (items[i].equals(Gallery)) {
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
                else if (items[i].equals(Cancel)) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Show the dialog
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            // Assign the image from camera
            if (requestCode == CAMERA_REQUEST) {


                mUri = photoUri;
                mselectImage.setImageURI(mUri);
            }

            // Assign the image from gallery
            else if (requestCode == GALLERY_REQUEST) {
                mUri = data.getData();


                mselectImage.setImageURI(mUri);
            }
        }
    }
}