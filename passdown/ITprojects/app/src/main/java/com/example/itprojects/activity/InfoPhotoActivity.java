package com.example.itprojects.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.KeyListener;
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
import com.example.itprojects.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import es.dmoral.toasty.Toasty;


import static com.example.itprojects.activity.ArtifactslistActivity.CLICKED_DESC;
import static com.example.itprojects.activity.ArtifactslistActivity.CLICKED_IMAGE;
import static com.example.itprojects.activity.ArtifactslistActivity.CLICKED_KEY;
import static com.example.itprojects.activity.ArtifactslistActivity.CLICKED_STATUS;
import static com.example.itprojects.activity.ArtifactslistActivity.CLICKED_TITLE;
import static com.example.itprojects.fragment.HomeFragment.CLICKED_EDITABLE;
import static com.example.itprojects.fragment.HomeFragment.CLICKED_UserId;

//import static com.example.itprojects.fragment.ArtifactsFragment.;

/**
 * This activiry displays one artifact information includes it's image, title and description.
 * Additionally the user can click the edit button to edit them and click finish button to update
 * back to the Firebase database.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-04
 */

public class InfoPhotoActivity extends AppCompatActivity {

    // Image url needed to be passed to "InfoPhotoActivity" to display
    public static final String DISPLAYED_IMAGE = "mImageUrl";

    // Code for onActivityResult
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 0;

    /**
     * Layout component variable
     */

    // Show progress on updating
    private ProgressBar mUpdatePB;

    // Artifact's image
    private ImageView mDisplayIV;

    // Artifact's title
    private EditText mTitleET;

    // Artifact's description
    private EditText mDescET;

    // When click it the user returns back to the photos fragment page
    private ImageView mBackIV;

    // When click it the user can edit info about this one particular photo
    private TextView mSubOrFinishTV;

    // The shadow bottom in the view mode
    private View mBottomShadowV;

    // The Switch bottom for status of artifacts
    private Switch mSwitchBtn;

    // Whether the user can edit or not is based on these boolean value, in default is display mode
    private KeyListener mEditTitleKL;
    private KeyListener mEditDescKL;
    private TextView mPrivacyTV;
    private boolean edit_active = false;
    private boolean isEdit_active=false;

    /**
     * Firebase variable
     */

    // Reference to the storage
    private StorageReference mStorageRef;

    // Reference to the database's child "Artifact"
    private DatabaseReference mDatabaseArtisRef;

    // Reference to the user's Firebase authentication mUid
    private String mUid;
    // Check which user's info photo page it is
    private String mClickedUid;

    // Reference to the key value for each child under database's child "Artifact"
    private String mkeyContent;

    // Artifact's image
    private String mImageUrl;

    // Artifact's title
    private String mTitleName;

    // Artifact's description
    private String mDescContent;

    //Artifacts's status
    private boolean mStatus;

    // Artifact's image uri for Firebase storage upload and camera and gallery's image selection
    private static Uri sImageUri = null;

    // Image uri for Android's camera image capture
    private Uri photoUri;

    // Check whether can be edited
    private String editable;

    /**
     * Show the images and display the view.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_photo);

        // Hide action bar
        getSupportActionBar().hide();

        // Assign Firebase variables' paths
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseArtisRef = FirebaseDatabase.getInstance().getReference().child("Artifact");
        mUid = FirebaseAuth.getInstance().getUid();

        // Retrieve clicked row info
        final Intent thisIntent = getIntent();
        mImageUrl = thisIntent.getStringExtra(CLICKED_IMAGE);
        mTitleName = thisIntent.getStringExtra(CLICKED_TITLE);
        mDescContent = thisIntent.getStringExtra(CLICKED_DESC);
        mkeyContent = thisIntent.getStringExtra(CLICKED_KEY);
        mClickedUid = thisIntent.getStringExtra(CLICKED_UserId);
        mStatus = thisIntent.getBooleanExtra(CLICKED_STATUS,false);

        // Information passed
        if (thisIntent.getStringExtra(CLICKED_EDITABLE) != null) {
            editable = thisIntent.getStringExtra(CLICKED_EDITABLE);
        }

        // Get each component's ID
        mDisplayIV = findViewById(R.id.photo_info_image);
        mTitleET = findViewById(R.id.photo_info_title);
        mDescET = findViewById(R.id.photo_info_desc);
        mUpdatePB = findViewById(R.id.pb_infoPhoto);
        mSubOrFinishTV = findViewById(R.id.photo_info_submit);
        mBackIV = findViewById(R.id.photo_info_back);
        mBottomShadowV = findViewById(R.id.photo_info_bottomView);
        mSwitchBtn =findViewById(R.id.switch2);
        mPrivacyTV = findViewById(R.id.photo_info_privacy);

        // Assign the values
        mTitleET.setText(mTitleName);
        mDescET.setText(mDescContent);
        if (mStatus==true) {
            mSwitchBtn.setChecked(true);
            mPrivacyTV.setText("私密");
            mPrivacyTV.setTextColor(this.getColor(R.color.golden_d8));

        }
        else {
            mSwitchBtn.setChecked(false);
            mPrivacyTV.setText("公开");
            mPrivacyTV.setTextColor(this.getColor(R.color.colorPrimaryDark));
        }

        // Check whether can be edited
        if (editable != null && editable.equals("No")) {
            mSubOrFinishTV.setVisibility(View.GONE);
            mSwitchBtn.setVisibility(View.GONE);
            mPrivacyTV.setVisibility(View.GONE);
        }

        // Load the image
        Picasso.get().load(mImageUrl).placeholder(R.mipmap.ic_loading).fit().centerInside().
                into(mDisplayIV);

        // Return back to previous activity
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InfoPhotoActivity.this.finish();
            }
        });

        // Show full image
        if (!edit_active) {
            mDisplayIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(InfoPhotoActivity.this,
                            FullImageActivity.class);
                    intent.putExtra(DISPLAYED_IMAGE, mImageUrl);
                    startActivity(intent); 
                }
            });
        }

        // Can not edit others
        if (mClickedUid != null && !mClickedUid.equals(mUid)) {
            mSubOrFinishTV.setVisibility(View.GONE);
            mSwitchBtn.setVisibility(View.GONE);
            mPrivacyTV.setVisibility(View.GONE);
        }

        // Edit the content or just display the content
        mEditTitleKL = mTitleET.getKeyListener();
        mEditDescKL = mDescET.getKeyListener();

        // First the activity is on "Finished" mode
        mSubOrFinishTV.setText("编辑");
        mTitleET.setKeyListener(null);
        mDescET.setKeyListener(null);
        mSwitchBtn.setEnabled(false);
        // When clicked the user can edit the blog
        mSubOrFinishTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The user has clicked "完成" to finish his modification
                if (edit_active) {
                    edit_active = false;

                    mTitleET.setKeyListener(null);
                    mDescET.setKeyListener(null);

                    // Shadow disappear botto, reappear in view mode
                    mBottomShadowV.setVisibility(View.VISIBLE);
                    System.out.println("a");
                    // Update everything the user has edited
                    startPosting();
                    if(!isEdit_active){
                        edit_active = false;
                        mTitleET.setKeyListener(null);
                        mDescET.setKeyListener(null);
                    }else{
                        edit_active = true;
                        mTitleET.setKeyListener(mEditTitleKL);
                        mDescET.setKeyListener(mEditDescKL);
                    }
                }
                // The user has clicked "编辑" to edit mode
                else {
                    System.out.println("b");
                    edit_active = true;
                    mSubOrFinishTV.setText("完成");
                    mSubOrFinishTV.setTextColor(Color.parseColor("#D8BB81"));

                    // Shadow disappear in edit mode
                    mBottomShadowV.setVisibility(View.GONE);

                    // The user can now edit all of them
                    mTitleET.setKeyListener(mEditTitleKL);
                    mDescET.setKeyListener(mEditDescKL);

                    mSwitchBtn.setEnabled(true);
                    mSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                mStatus=true;
                                mPrivacyTV.setText("私密");
                                mPrivacyTV.setTextColor(getColor(R.color.golden_d8));
                            }
                            else {
                                mStatus=false;
                                mPrivacyTV.setText("公开");
                                mPrivacyTV.setTextColor(getColor(R.color.colorPrimaryDark));
                            }
                        }
                    });
                    mDisplayIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SelectImage();
                        }
                    });
                }
            }
        });
    }

    /**
     * Upload the new image to the Firebase storage and update informaion to the database.
     */

    private void startPosting() {
        // Retrieve the text edited by the user, even though there are no changes, upload again
        final String title_val = mTitleET.getText().toString().trim();
        final String desc_val = mDescET.getText().toString().trim();

        // Activate the progress bar
        mUpdatePB.setVisibility(View.VISIBLE);

        // Start uploading
        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) ){
            if (sImageUri != null) {


                // Assign the storage's path
                final StorageReference filepath = mStorageRef.child("Blog_Images").child(
                        "images/" + UUID.randomUUID().toString());

                filepath.putFile(sImageUri).addOnSuccessListener
                        (new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                // Deactivate the progress bar
                                mUpdatePB.setVisibility(View.GONE);

                                // Retrieve the image's downloadable url from the storage
                                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Assign the url
                                        Uri downloadUri = uri;

                                        // Record current time
                                        long millis = System.currentTimeMillis();
                                        java.util.Date date = new java.util.Date(millis);

                                        // Generate a new artifact
                                        Artifact artifact = new Artifact(title_val, desc_val,
                                                downloadUri.toString(), date, mkeyContent,mStatus,mUid);

                                        // Find the key value of child artifact in the database
                                        String uploadId = mkeyContent;

                                        // Replace the old artifact
                                        mDatabaseArtisRef.child(mUid).child(uploadId).setValue(artifact);
                                    }
                                });

                                // The process is done
                                Toasty.success(InfoPhotoActivity.this, "编辑成功！",
                                        Toast.LENGTH_SHORT).show();

                                // Jump back to the main page
                                //InfoPhotoActivity.this.finish();
                                Intent mainIntent = new Intent(InfoPhotoActivity.this,
                                        MainActivity.class);
                                mainIntent.putExtra("clicked_id",1);
                                startActivity(mainIntent);
                            }
                        });
            }
            // Same process but without access the storage because the image is not changed
            else {
                long millis = System.currentTimeMillis();
                java.util.Date date = new java.util.Date(millis);
                mUpdatePB.setVisibility(View.GONE);

                Artifact artifact = new Artifact(title_val, desc_val, mImageUrl,
                        date, mkeyContent,mStatus,mUid);
                String uploadId = mkeyContent;
                mDatabaseArtisRef.child(mUid).child(uploadId).setValue(artifact);

                Toasty.success(InfoPhotoActivity.this, "编辑成功！", Toast.LENGTH_SHORT)
                        .show();
                //InfoPhotoActivity.this.finish();
                Intent mainIntent = new Intent(InfoPhotoActivity.this,
                        MainActivity.class);
                mainIntent.putExtra("clicked_id",1);
                startActivity(mainIntent);
            }
        }
        else{
            isEdit_active=true;
            mUpdatePB.setVisibility(View.GONE);

            // Show warning, text is required
            Toasty.warning(InfoPhotoActivity.this, "请输入文本", Toast.LENGTH_LONG)
                    .show();

        }

    }

    /**
     * This function opens a dialog and allows user to choose upload method base.
     *
     */
    private void SelectImage() {
        // Each options' texts
        final String AddImage = getString(R.string.postActivity_addImage);
        final String Camera = getString(R.string.postActivity_camera);
        final String Gallery = getString(R.string.postActivity_gallery);
        final String Cancel = getString(R.string.postActivity_cancel);

        // Pop-up options menu
        final CharSequence[] items = {Camera, Gallery, Cancel};

        // Generate the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(InfoPhotoActivity.this);
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
                if (sImageUri == null) {
                    if (photoUri != null) {
                        sImageUri = photoUri;
                    }
                }
                else {
                    sImageUri = data.getData();
                }

                mDisplayIV.setImageURI(sImageUri);
            }
            // Assign the image from gallery
            else if (requestCode == GALLERY_REQUEST) {
                sImageUri = data.getData();
                mDisplayIV.setImageURI(sImageUri);
            }
        }
    }
}