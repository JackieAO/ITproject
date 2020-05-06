package com.example.itprojects.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.itprojects.bean.PassDown;
import com.example.itprojects.bean.User;
import com.example.itprojects.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-09-07
 */

public class ProfileFragment extends Fragment {

    // Used for retrieve one recycle view's info that the user clicks to
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 0;

    // Image uri for Android's camera image capture
    private static Uri mImageUri = null;
    private Uri photoUri;

    /**
     * Layout component variable
     */

    private ImageView mAvatarIV;
    private EditText mSurNameET;
    private EditText mFirstNameET;
    private TextView mUserEmailTV;
    private EditText mPassDownET;
    private TextView mCodeTV;
    private TextView mEditTV;
    private Button mLogoutBtn;

    // Whether the user can edit or not is based on the boolean value, in default is display mode
    private KeyListener mSurNameKL;
    private KeyListener mFirstNameKL;
    private KeyListener mEditPassDownKL;
    private boolean edit_active = false;

    /**
     * Firebase variable
     */

    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private DatabaseReference mDatebaseRef;
    private String mUid;
    private String mEmail;
    private String mCode;

    /**
     * Required empty public constructor.
     */

    public ProfileFragment() {
    }
    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // To Change the subtitle
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("个人信息");

        // Get each component's ID
        mAvatarIV = view.findViewById(R.id.fragment_profile_avatar);
        mSurNameET = view.findViewById(R.id.fragment_profile_surName);
        mFirstNameET = view.findViewById(R.id.fragment_profile_firstName);
        mUserEmailTV = view.findViewById(R.id.fragment_profile_email);
        mPassDownET = view.findViewById(R.id.fragment_profile_artifact);
        mCodeTV = view.findViewById(R.id.fragment_profile_code);
        mEditTV = view.findViewById(R.id.fragment_profile_edit);
        mLogoutBtn = view.findViewById(R.id.fragment_profile_logout);

        // Retrieve the user's info
        mUid = FirebaseAuth.getInstance().getUid();
        mDatebaseRef = FirebaseDatabase.getInstance().getReference("Users/" + mUid);
        mDatebaseRef.keepSynced(true);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.child("image").getValue().toString();
                String surName = dataSnapshot.child("surName").getValue().toString();
                String firstName = dataSnapshot.child("firstName").getValue().toString();
                mEmail = dataSnapshot.child("email").getValue().toString();
                String passdown = dataSnapshot.child("passdown_Name").getValue().toString();
                mCode = dataSnapshot.child("invited_Code").getValue().toString();

                // Assign the values
                mSurNameET.setText(surName);
                mFirstNameET.setText(firstName);
                mUserEmailTV.setText(mEmail);
                mPassDownET.setText(passdown);
                mCodeTV.setText(mCode);
                Picasso.get().
                        load(imageUrl).
                        placeholder(R.mipmap.ic_unkown_user).
                        fit().
                        centerInside().
                        into(mAvatarIV);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Double tap auto copy invitation code -- Yat Yeung
        mCodeTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                ClipboardManager cm = (ClipboardManager)ProfileFragment.this.getActivity()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mCodeTV.getText());
                Toasty.success(getActivity(), "邀请码已复制", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // Disable the email edit option
        mUserEmailTV.setEnabled(false);

        // Edit the content or just display the content
        mSurNameKL = mSurNameET.getKeyListener();
        mFirstNameKL = mFirstNameET.getKeyListener();
        mEditPassDownKL = mPassDownET.getKeyListener();

        // First the activity is on "Finished" mode
        mSurNameET.setKeyListener(null);
        mFirstNameET.setKeyListener(null);
        mPassDownET.setKeyListener(null);

        // When clicked the user can edit the info
        mEditTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // The user has clicked "完成" to finish his modification
                if (edit_active) {
                    edit_active = false;
                    mEditTV.setText("编辑");
                    mEditTV.setTextColor(Color.WHITE);

                    // Change to gray btn -- Yat Yeung
                    mEditTV.setBackgroundResource(R.drawable.btn_gray_normal);

                    mSurNameET.setKeyListener(null);
                    mFirstNameET.setKeyListener(null);
                    mPassDownET.setKeyListener(null);

                    String surName = mSurNameET.getText().toString().trim();
                    String firstName = mFirstNameET.getText().toString().trim();
                    String passdownName = mPassDownET.getText().toString().trim();

                    // Update everything the use has edited
                    mDatebaseRef.child("surName").setValue(surName);
                    mDatebaseRef.child("firstName").setValue(firstName);
                    mDatebaseRef.child("passdown_Name").setValue(passdownName);
                    startPosting();
                }

                // The user has clicked "编辑" to edit mode
                else {
                    edit_active = true;
                    mEditTV.setText("完成");

                    // Change to gray btn -- Yat Yeung
                    mEditTV.setBackgroundResource(R.drawable.btn_custom_normal);

                    // The user can now edit his name and his Pass Down's name
                    mSurNameET.setKeyListener(mSurNameKL);
                    mFirstNameET.setKeyListener(mFirstNameKL);
                    mPassDownET.setKeyListener(mEditPassDownKL);
                    mAvatarIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SelectImage();
                        }
                    });
                }
            }
        });

        // Click to log out button
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Select the photo from the album.
     */

    private void SelectImage() {
        final String Camera = getString(R.string.postActivity_camera);
        final String Gallery = getString(R.string.postActivity_gallery);
        final String Cancel = getString(R.string.postActivity_cancel);
        final String AddImage = getString(R.string.postActivity_addImage);

        final CharSequence[] items = {Camera, Gallery, Cancel};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                    photoUri = getActivity().getApplicationContext().getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    getActivity().startActivityFromFragment(ProfileFragment.this,
                            cameraIntent, CAMERA_REQUEST);

                }
                else if (items[i].equals(Gallery)) {
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    getActivity().startActivityFromFragment(ProfileFragment.this,
                            galleryIntent, GALLERY_REQUEST);
                }
                else if (items[i].equals(Cancel)) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }
    /**
     * Posting new user's info.
     */

    private void startPosting() {
        final String surName = mSurNameET.getText().toString().trim();
        final String firstName = mFirstNameET.getText().toString().trim();
        final String passdownName = mPassDownET.getText().toString().trim();

        // The user has updated the image as well, need to store it in storage
        if (mImageUri != null) {
            final StorageReference filepath = mStorage.child("Users_Images").child("images/" + UUID
                    .randomUUID().toString());

            filepath.putFile(mImageUri).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener
                                    (new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Uri downloadUri = uri;
                                            mDatebaseRef.child("image").setValue(downloadUri.toString());
                                            final PassDown passDown=new PassDown(passdownName,surName+" "+firstName,mUid);

                                            mDatebaseRef.child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                        FirebaseDatabase.getInstance().getReference("Users/" + postSnapshot.getKey())
                                                                .child("friends").child(mUid).setValue(passDown);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }
                                    });

                            Toasty.success(getActivity(), "编辑成功!", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }
        else {
            final PassDown passDown=new PassDown(passdownName,surName+" "+firstName,mUid);
            mDatebaseRef.child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        FirebaseDatabase.getInstance().getReference("Users/" + postSnapshot.getKey())
                                .child("friends").child(mUid).setValue(passDown);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            Toasty.success(getActivity(), "编辑成功!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     *  Add the photo and return the result.
     *
     * @param requestCode       解释一下
     * @param resultCode
     * @param data
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                mImageUri =  photoUri;
            }
            else if (requestCode == GALLERY_REQUEST) {
                mImageUri = data.getData();
            }
            mAvatarIV.setImageURI(mImageUri);
        }
    }
}
