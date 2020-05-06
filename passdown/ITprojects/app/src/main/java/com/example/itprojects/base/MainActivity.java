package com.example.itprojects.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itprojects.activity.LoginActivity;
import com.example.itprojects.activity.PostActivity;
import com.example.itprojects.adapter.UserAdapter;
import com.example.itprojects.bean.User;
import com.example.itprojects.fragment.ArtifactsFragment;
import com.example.itprojects.fragment.FamilyFragment;
import com.example.itprojects.fragment.HomeFragment;
import com.example.itprojects.fragment.ProfileFragment;
import com.example.itprojects.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

/**
 * The main activity for the four fragments, bottom navigation and top options menu.
 *
 * @author  Team Perfect World
 * @version 8.0
 * @since   2019-08-30
 */

public class MainActivity extends AppCompatActivity {

    // Code for onActivityResult
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 0;

    // Image uri for Android's camera image capture
    private static Uri sImageUri = null;
    private Uri photoUri;

    /**
     * Firebase variable
     */

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseRef, mNotificationRef;

    private String Invited_Code;

    // UID for the logged in user
    private String uid;

    // UID for the other users searched
    private String UserId;

    // This user's name on database
    private String nameUser;

    // This user's PassDown name on database
    private String namePassdown;

    // Other user's name on database
    private String otherNameUser;

    // Other users' PassDown name on database
    private String otherPassdownName;

    // Whether the user searched exists or not
    private boolean user_exist_status = false;

    // View for user searched
    private RecyclerView mSearchResultRV;

    // The recycler view's adapter
    private UserAdapter mSRAdapter;

    // Assign the searched user to the list
    private ArrayList<User> mUsers;

    private EditText Code;
    private RelativeLayout mNoUserRL;
    private View dialogView;

    // Getter
    public static Uri getImageUri() {
        return sImageUri;
    }

    /**
     * Show the images and display the view.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseRef.keepSynced(true);
        mNotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        // Initialize the list
        mUsers = new ArrayList<>();

        // Get the component's ID
        BottomNavigationView navigationView = findViewById(R.id.btm_nav);

        // Change page from other activity to fragments in main activity
        int clicked_id = getIntent().getIntExtra("clicked_id", 0);
        if (clicked_id == 1) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new HomeFragment())
                    .addToBackStack(null)
                    .commit();
            //change clicked item
            navigationView.setSelectedItemId(R.id.home);
        }
        else if (clicked_id  == 2) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new ArtifactsFragment())
                    .addToBackStack(null)
                    .commit();
            navigationView.setSelectedItemId(R.id.artifacts);
        }
        else if (clicked_id  == 3) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new FamilyFragment())
                    .addToBackStack(null)
                    .commit();
            navigationView.setSelectedItemId(R.id.family);
        }
        else if (clicked_id  == 4) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
            navigationView.setSelectedItemId(R.id.profile);
        }

        // Trigger events based on the options selected
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView
                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                // The id of the navigation view
                int id = menuItem.getItemId();

                // Main page
                if (id == R.id.home) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_layout, new HomeFragment())
                            .addToBackStack(null)
                            .commit();

                }

                // Artifacts page
                else if (id == R.id.artifacts) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_layout, new ArtifactsFragment())
                            .addToBackStack(null)
                            .commit();
                }

                // Family page
                else if (id == R.id.family) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_layout, new FamilyFragment())
                            .addToBackStack(null)
                            .commit();

                }

                // Profile page
                else if (id == R.id.profile) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_layout, new ProfileFragment())
                            .addToBackStack(null)
                            .commit();
                }

                return true;
            }
        });

        // Check the user's authentication status
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                // Go to log in activity
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this,
                            LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Add the status checker
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * Clickable action_bar.
     *
     * @param menu
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * The specific clicked action for action_bar.
     *
     * @param item
     */

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // Upload image
        if (item.getItemId() == R.id.action_add) {
            SelectImage();
        }

        // Search and add one user
        else if (item.getItemId() == R.id.action_find) {
            showInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Select one image from different sources.
     */

    private void SelectImage() {

        // Each options' texts
        final String Camera = getString(R.string.postActivity_camera);
        final String Gallery = getString(R.string.postActivity_gallery);
        final String Cancel = getString(R.string.postActivity_cancel);
        final String AddImage = getString(R.string.postActivity_addImage);

        // Pop-up options menu
        final CharSequence[] items = {Camera, Gallery, Cancel};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

    /**
     * Initialize and show the search dialog.
     */

    private void showInputDialog() {

        // Add one edit view
        final EditText editText = new EditText(MainActivity.this);
        dialogView = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.dialog_layout, null);
        Code =  dialogView.findViewById(R.id.invited_pwd);
        Code.setVisibility(View.GONE);

        // getUserRL
        mNoUserRL = dialogView.findViewById(R.id.not_found_user_email);
        mNoUserRL.setVisibility(View.VISIBLE);

        // Build the dialog
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(MainActivity.this);
        inputDialog.setTitle("搜索用户").setView(editText);
        inputDialog.setPositiveButton("搜索",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        // The user has typed one email, now to search
                        SearchUserByEmail(editText.getText().toString());
                        showCustomizeDialog();

                    }
                }).show();
    }

    /**
     * Access Firebase to find a match for the typed in email.
     *
     * @param searchInput           你解释一下.
     *
     */
    //核对该用户是否已经是我的好友 或者邮箱不存在
    private void SearchUserByEmail(String searchInput) {

        Query query = mDatabaseRef.orderByChild("email").equalTo(searchInput);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // The user has found
                if (dataSnapshot.exists()) {

                    // Clear out the previous searched users first
                    mUsers.clear();

                    // Show the user matched by the input
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User search_user = postSnapshot.getValue(User.class);
                        mUsers.add(search_user);
                        UserId = postSnapshot.getKey();
                        Invited_Code=search_user.getInvited_Code();
                        otherNameUser = search_user.getSurName() + " " +
                                search_user.getFirstName();
                        otherPassdownName = search_user.getPassdown_Name();
                    }

                    // Set up the adpater and show the user
                    mSRAdapter = new UserAdapter(MainActivity.this, mUsers);
                    mSearchResultRV.setAdapter(mSRAdapter);
                    Code.setVisibility(View.VISIBLE);
                    mNoUserRL.setVisibility(View.GONE);
                }

                // There is no match
                else {
                    Toasty.warning(MainActivity.this, "无任何匹配用户",
                            Toast.LENGTH_SHORT).show();
                    mUsers.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Add the user to be a friend
        mDatabaseRef.child(uid).child("friends").addListenerForSingleValueEvent
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (UserId != null) {
                                if (UserId.equals(postSnapshot.getKey())) {

                                    user_exist_status = true;

                                }
                                else{
                                    user_exist_status =false;
                                }
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Show the searched result.
     */

    private void showCustomizeDialog() {
        AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(MainActivity.this);

        mSearchResultRV =
                dialogView.findViewById(R.id.search_result);

        mSearchResultRV.setHasFixedSize(true);

        mSearchResultRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        customizeDialog.setTitle("搜索结果");
        customizeDialog.setView(dialogView);
        customizeDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        customizeDialog.setPositiveButton("添加",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CheckSearchResult(Code.getText().toString().trim());
                        dialog.dismiss();
                    }
                });

        // show the dialog
        customizeDialog.show();
    }

    /**
     * Check the result, the user can not add himself as a friend.
     */

    public void CheckSearchResult( String InviteCode) {
        if (UserId != null) {
            if (uid.equals(UserId) && user_exist_status == false) {
                Toasty.warning(this, "您不可以添加自己", Toast.LENGTH_SHORT).
                        show();
            }
            else if (!uid.equals(UserId) && user_exist_status == false) {

                if (Invited_Code.equals(InviteCode)) {
                    // Record current time
                    long millis = System.currentTimeMillis();
                    final java.util.Date date = new java.util.Date(millis);

                    // Add the user
                    mDatabaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User search_user = dataSnapshot.getValue(User.class);
                                nameUser = search_user.getSurName() + " " + search_user.
                                        getFirstName();
                                namePassdown = search_user.getPassdown_Name();
                                mDatabaseRef.child(UserId).child("friends").child(uid).
                                        child("passdown_Username").setValue(nameUser);
                                mDatabaseRef.child(UserId).child("friends").child(uid).
                                        child("passdown_Name").setValue(namePassdown);
                                mDatabaseRef.child(UserId).child("friends").child(uid).child("UserId").
                                        setValue(uid);
                                mDatabaseRef.child(uid).child("friends").child(UserId).child("UserId").
                                        setValue(UserId);
                                mDatabaseRef.child(uid).child("friends").child(UserId).child("passdown_Name").
                                        setValue(otherPassdownName);
                                mDatabaseRef.child(uid).child("friends").child(UserId).child("passdown_Username").
                                        setValue(otherNameUser);

                                // Friend added notification
                                HashMap<String, String> addNotification = new HashMap<>();
                                addNotification.put("From", uid);
                                addNotification.put("Type", "Add");
                                mNotificationRef.child(UserId).push().setValue(addNotification);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    // Jump to friends fragment
                    Toasty.success(MainActivity.this, "已添加好友",
                            Toast.LENGTH_SHORT).show();

                    /*Intent mainIntent = new Intent(MainActivity.this,
                            MainActivity.class);
                    mainIntent.putExtra("clicked_id",3);
                    startActivity(mainIntent);*/
                }
                else {
                    Toasty.error(MainActivity.this, "邀请码错误",
                            Toast.LENGTH_SHORT).show();
                }
            }
            else if (user_exist_status == true) {
                Toasty.warning(MainActivity.this, "该好友已存在",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toasty.warning(MainActivity.this, "请重新输入", Toast.LENGTH_SHORT).show();
            showInputDialog();
        }
    }

    /**
     * The result received from selecting a image.
     *
     * @param requestCode       解释一下
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Assign the image from camera
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST) {
            sImageUri = photoUri;
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }
        // Assign the image from gallery
        else if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST) {
            sImageUri = data.getData();
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }
        else {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}