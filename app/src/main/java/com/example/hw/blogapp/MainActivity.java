package com.example.hw.blogapp;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TextView textView;

    private String current_user_id;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private FloatingActionButton addPostBtn;

    private BottomNavigationView mainbottomNav;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        drawerLayout = findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.nav);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);


            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    userMenuSelector(item);
                    return false;
                }

            });


            if (mAuth.getCurrentUser() != null) {

                mainbottomNav = findViewById(R.id.mainBottomNav);

                // FRAGMENTS
                homeFragment = new HomeFragment();
                notificationFragment = new NotificationFragment();
                accountFragment = new AccountFragment();

                initializeFragment();

                mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                        switch (item.getItemId()) {

                            case R.id.bottom_action_home:

                                replaceFragment(homeFragment, currentFragment);
                                return true;

                            case R.id.bottom_action_account:

                                replaceFragment(accountFragment, currentFragment);
                                return true;

                            case R.id.bottom_action_notif:

                                replaceFragment(notificationFragment, currentFragment);
                                return true;

                            default:
                                return false;


                        }

                    }
                });


                addPostBtn = findViewById(R.id.add_post_btn);
                addPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                        startActivity(newPostIntent);

                    }
                });

            }


    }





    @Override
    protected void onStart() {
        super.onStart();


            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {

                sendToLogin();

            } else {

                current_user_id = mAuth.getCurrentUser().getUid();

                firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            if (!task.getResult().exists()) {

                                Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                                startActivity(setupIntent);
                                finish();

                            }

                        } else {

                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                        }

                    }

                });
            }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {

            case R.id.aset:
                Intent settingsIntent = new Intent(MainActivity.this, SetupActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.loooo:
                logOut();
                return true;

            case R.id.video:

                Uri uri = Uri.parse("https://www.youtube.com/user/Balwanhsr/videos"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

                return true;

            case R.id.fb:

                Uri ri = Uri.parse("https://www.facebook.com/ASPAMfoundation/?ref=br_rs"); // missing 'http://' will cause crashed
                Intent ntent = new Intent(Intent.ACTION_VIEW, ri);
                startActivity(ntent);

                return true;


            default:
                   return false;


        }

    }

    private void logOut() {


        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, notificationFragment);
        fragmentTransaction.add(R.id.main_container, accountFragment);

        fragmentTransaction.hide(notificationFragment);
        fragmentTransaction.hide(accountFragment);

        fragmentTransaction.commit();

    }

    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == homeFragment){

            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == accountFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == notificationFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(accountFragment);

        }
        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }


    private boolean userMenuSelector(MenuItem item) {

        switch (item.getItemId()){




            case R.id.mission:
                Intent bx = new Intent(MainActivity.this,Mission.class);
                startActivity(bx);
                return true;

            case R.id.done:
                Intent d = new Intent(MainActivity.this,Done.class);
                startActivity(d);
                return true;

            case R.id.aspam:

                Uri xri = Uri.parse("http://www.aspamfoundation.com/"); // missing 'http://' will cause crashed
                Intent xntent = new Intent(Intent.ACTION_VIEW, xri);
                startActivity(xntent);

                return true;

            case R.id.contactus:

                Intent x = new Intent(MainActivity.this,ContactUs.class);
                startActivity(x);

                return true;


            default:
                return false;
        }


    }

    private boolean amIConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

}
