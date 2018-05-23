package com.app.appathon.blooddonateapp.activities;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.app.appathon.blooddonateapp.BuildConfig;
import com.app.appathon.blooddonateapp.config.Config;
import com.app.appathon.blooddonateapp.R;
import com.app.appathon.blooddonateapp.fragments.FavoriteFragment;
import com.app.appathon.blooddonateapp.fragments.LocatingDonors;
import com.app.appathon.blooddonateapp.model.User;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity
        implements ValueEventListener, SearchView.OnQueryTextListener {

    private FragmentTransaction fragmentTransaction;
    public MaterialSpinner spinner;
    private Fragment fragment;
    private FirebaseUser firebaseUser;
    private TextView headerText;
    public FragmentCommunicator fragmentCommunicator;
    public int someIntValue = 1;
    private CallbackManager callbackManager;
    private String emailText;
    private ProfileTracker profileTracker;
    private CircleImageView imageView;
    private DatabaseReference mDatabase;
    private boolean isLoaded;
    private SharedPreferences myPref;
    private Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/HelveticaNeue.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        callbackManager = CallbackManager.Factory.create();
        myPref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        //Initializing Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //Setting The Tag for sending notification
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String userID = status.getSubscriptionStatus().getUserId();
        OneSignal.setSubscription(true);
        mDatabase.child("users").child(firebaseUser.getUid()).child("sendNotification").setValue(userID);

        //Inflating NavHeader
        View navHeader = View.inflate(this, R.layout.navbar_head, null);
        headerText = (TextView) navHeader.findViewById(R.id.user_name);
        imageView = (CircleImageView) navHeader.findViewById(R.id.profileThumb);

        //inflating bottomView
        ViewGroup bottomView = (ViewGroup) View.inflate(this, R.layout.custom_drawer_item, null);

        //Initializing Navigation Drawer
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(navHeader)
                .withTranslucentStatusBar(true)
                .withStickyFooter(bottomView)
                .withStickyFooterDivider(true)
                .withStickyFooterShadow(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Find Donors")
                                .withIcon(GoogleMaterial.Icon.gmd_search)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withIdentifier(1),
                        new PrimaryDrawerItem()
                                .withName("Inbox")
                                .withIcon(GoogleMaterial.Icon.gmd_inbox)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withIdentifier(2),
                        new PrimaryDrawerItem()
                                .withName("Favorite")
                                .withIcon(GoogleMaterial.Icon.gmd_favorite_outline)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withIdentifier(4),
                        new SectionDrawerItem().withName("More")
                                .withTextColor(Color.GRAY),
                        new PrimaryDrawerItem()
                                .withName("Profile")
                                .withIcon(GoogleMaterial.Icon.gmd_account)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withIdentifier(3),
                        new PrimaryDrawerItem()
                                .withName("Sign Out")
                                .withIcon(FontAwesome.Icon.faw_sign_out)
                                .withIconColor(Color.GRAY)
                                .withTextColor(Color.GRAY)
                                .withSelectedTextColor(Color.DKGRAY)
                                .withIdentifier(5)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(onDrawerItemClickListener)
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(1)
                // build only the view of the Drawer (don't inflate it automatically in our layout which is done with .build())
                .build();
        result.setSelection(1, true);

        LoginButton loginButton = (LoginButton) bottomView.findViewById(R.id.login_button);
        loginButton.setBackgroundResource(R.drawable.facebook_login_background);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                // App code
                Uri imgUrl = currentProfile.getProfilePictureUri(120,120);
                Picasso.with(MainActivity.this)
                    .load(imgUrl)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();
                            String encodedImg = Base64.encodeToString(b, Base64.DEFAULT);
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("users").child(firebaseUser.getUid())
                                    .child("facebook").child("pic_url").setValue(encodedImg);
                            isLoaded = true;
                            SharedPreferences.Editor edit = myPref.edit();
                            edit.putBoolean("isLoaded", isLoaded);
                            edit.apply();
                            onFacebookProfilePictureSetup();
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                    }
                };
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        onFacebookProfilePictureSetup();
    }

    private void onFacebookProfilePictureSetup(){
        if (myPref.getBoolean("isLoaded", false)){
            mDatabase.child("users").child(firebaseUser.getUid()).child("facebook").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String imgUrl = String.valueOf(dataSnapshot.child("pic_url").getValue());
                    byte[] imageAsBytes = Base64.decode(imgUrl, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    imageView.setImageBitmap(bitmap);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_map:
                startActivity(new Intent(MainActivity.this, NearbyDonorActivity.class));
                return true;
            case R.id.menu_search:
                return true;
            case R.id.rate:
                rateMyApp();
                return true;
            case R.id.about:
                aboutMyApp();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            User uData = snapshot.getValue(User.class);
            if (uData != null) {
                if (snapshot.getKey().equals(firebaseUser.getUid())) {
                    String displayName = uData.getName();
                    emailText = uData.getEmail();
                    headerText.setText(displayName);
                    Config config = new Config();
                    config.setName(uData.getName());
                    config.setPhone(uData.getPhone());
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        fragmentCommunicator.passDataToFragment(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public interface FragmentCommunicator {
        void passDataToFragment(String value);
    }

    private void aboutMyApp() {
        MaterialDialog.Builder bulder = new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .customView(R.layout.about, true)
                .backgroundColorRes(android.R.color.white)
                .titleColorRes(R.color.dialog_color)
                .positiveText("MORE APPS")
                .icon(getResources().getDrawable(R.mipmap.ic_launcher))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Uri uri = Uri.parse("market://search?q=pub:" + "NerdGeeks");
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/search?q=pub:" + "NerdGeeks")));
                        }
                    }
                });

        MaterialDialog materialDialog = bulder.build();

        TextView versionCode = (TextView) materialDialog.findViewById(R.id.version_code);
        TextView versionName = (TextView) materialDialog.findViewById(R.id.version_name);
        versionCode.setText(String.valueOf("vCode : " + BuildConfig.VERSION_CODE));
        versionName.setText("vName : " + BuildConfig.VERSION_NAME);

        materialDialog.show();
    }

    private void rateMyApp() {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }
    }

    private Drawer.OnDrawerItemClickListener onDrawerItemClickListener = new Drawer.OnDrawerItemClickListener() {

        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            if (drawerItem != null) {
                if (drawerItem.getIdentifier() == 1) {
                    fragment = LocatingDonors.newInstance();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                    toolbar.setSubtitle("Find Donors");
                } else if (drawerItem.getIdentifier() == 3) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    finish();
                } else if (drawerItem.getIdentifier() == 5) {
                    FirebaseAuth.getInstance().signOut();
                    OneSignal.setSubscription(false);
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                } else if (drawerItem.getIdentifier() == 2) {
                    startActivity(new Intent(MainActivity.this, InboxActivity.class));
                    finish();
                } else if (drawerItem.getIdentifier() == 4) {
                    fragment = FavoriteFragment.newInstance();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
                    toolbar.setSubtitle("Favorite");
                }
            }
            return false;
        }
    };
}
