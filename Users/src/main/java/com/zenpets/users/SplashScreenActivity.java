package com.zenpets.users;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zenpets.users.creators.ProfileCreatorActivity;
import com.zenpets.users.credentials.LoginActivity;
import com.zenpets.users.landing.MainLandingActivity;
import com.zenpets.users.landing.NewMainLandingActivity;
import com.zenpets.users.landing.TestMainLandingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreenActivity extends AppCompatActivity {

    /** CAST THE LAYOUT ELEMENT/S **/
    @BindView(R.id.txtAppName) AppCompatTextView txtAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity);
        ButterKnife.bind(this);
        FirebaseAnalytics.getInstance(this);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.zenpets.users", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("MY KEY HASH:",
//                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//        } catch (NoSuchAlgorithmException e) {
//        }

        /** CONFIGURE THE ANIMATIONS **/
        Animation animIn = new AlphaAnimation(0.0f, 1.0f);
        animIn.setDuration(500);

        /** ANIMATE THE APP NAME **/
        txtAppName.startAnimation(animIn);
        animIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                /** DETERMINE IF THE USER IS LOGGED IN **/
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)   {
                    /** CHECK IF THE USER HAS CREATED HIS / HER PROFILE **/
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChildren()) {
                                new MaterialDialog.Builder(SplashScreenActivity.this)
                                        .icon(ContextCompat.getDrawable(SplashScreenActivity.this, R.drawable.ic_info_outline_black_24dp))
                                        .title("Add Details?")
                                        .cancelable(true)
                                        .content("Would you to add a few details about yourself. Click \"Add Details\" to do it now. Or, click \"Later\" to add your details later")
                                        .positiveText("Add details")
                                        .negativeText("Later")
                                        .theme(Theme.LIGHT)
                                        .typeface("HelveticaNeueLTW1G-MdCn.otf", "HelveticaNeueLTW1G-Cn.otf")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                Intent showProfileCreator = new Intent(SplashScreenActivity.this, ProfileCreatorActivity.class);
                                                showProfileCreator.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(showProfileCreator);
                                                finish();
                                                dialog.dismiss();
                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                Intent showLanding = new Intent(SplashScreenActivity.this, TestMainLandingActivity.class);
                                                showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(showLanding);
                                                finish();
                                                dialog.dismiss();
                                            }
                                        }).show();
                            } else {
                                Intent showLanding = new Intent(SplashScreenActivity.this, TestMainLandingActivity.class);
                                showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(showLanding);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    Intent showLogin = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    showLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(showLogin);
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}