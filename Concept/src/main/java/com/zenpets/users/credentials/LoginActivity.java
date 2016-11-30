package com.zenpets.users.credentials;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zenpets.users.R;
import com.zenpets.users.creators.ProfileCreator;
import com.zenpets.users.landing.LandingActivity;
import com.zenpets.users.landing.NewLandingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    /** THE FIREBASE AUTH INSTANCE **/
    private FirebaseAuth mAuth;

    /** THE FIREBASE AUTH STATE LISTENER INSTANCE **/
    private FirebaseAuth.AuthStateListener mAuthListener;

    /** A GOOGLE API CLIENT INSTANCE **/
    private GoogleApiClient apiClient;

    /** THE STATIC REQUEST CODE FOR PERFORMING THE GOOGLE SIGN IN **/
    private static final int GOOGLE_SIGN_IN = 101;

    /** THE FACEBOOK CALLBACK MANAGER **/
    private CallbackManager callbackManager;

    /** CAST THE LAYOUT ELEMENTS **/
    @BindView(R.id.fbLogin) LoginButton fbLogin;

    @OnClick(R.id.imgbtnGoogleSignIn) void googleLogin()   {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @OnClick(R.id.imgbtnFacebookSignIn) void facebookLogin()   {
        fbLogin.performClick();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        /** INITIALIZE THE FIREBASE AUTH INSTANCE **/
        mAuth = FirebaseAuth.getInstance();

        /** START THE AUTH STATE LISTENER **/
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    final String USER_ID = user.getUid();
                    Toast.makeText(getApplicationContext(), user.getUid(), Toast.LENGTH_SHORT).show();

                    Intent showLanding = new Intent(LoginActivity.this, NewLandingActivity.class);
                    showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(showLanding);
                    finish();

                    /** CHECK IF THE USER HAS CREATED HIS / HER PROFILE **/
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChildren()) {
                                /** SAVE THE PROFILE IN THE FIREBASE DATABASE **/
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").push();
                                reference.child("userID").setValue(USER_ID);
                                reference.child("userName").setValue(user.getDisplayName());
                                reference.child("userEmail").setValue(user.getEmail());
                            }else {
                                Intent showLanding = new Intent(LoginActivity.this, NewLandingActivity.class);
                                showLanding.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(showLanding);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        };

        /** CONFIGURE THE GOOGLE SIGN IN OPTIONS **/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        /** CONFIGURE THE GOOGLE API CLIENT INSTANCE **/
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /** CONFIGURE FOR THE FACEBOOK LOGIN **/
        callbackManager = CallbackManager.Factory.create();
        fbLogin.setReadPermissions("email", "public_profile");
        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                Log.e("FB SUCCESS", String.valueOf(loginResult));
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
//                Log.e("FB ERROR", String.valueOf(error));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN)  {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
//                Log.e("Google Login Status", String.valueOf(result.getStatus()));
                Toast.makeText(getApplicationContext(), "Google sign in failed. Please try again..", Toast.LENGTH_LONG).show();
            }
        } else {
            /** FOR THE FACEBOOK LOGIN **/
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /** HANDLE THE FACEBOOK LOGIN SUCCESS **/
    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())    {
                            Toast.makeText(getApplicationContext(), "Facebook login successful", Toast.LENGTH_SHORT).show();
                        } else {
//                            Log.e("FB Login", String.valueOf(task.getException()));
                            Toast.makeText(getApplicationContext(), "Facebook sign in failed. Please try again..", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /** HANDLE THE GOOGLE LOGIN SUCCESS **/
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())    {
                            Toast.makeText(getApplicationContext(), "Google sign in successful", Toast.LENGTH_SHORT).show();
                        } else {
//                            Log.e("Google Login", String.valueOf(task.getException()));
//                            Toast.makeText(getApplicationContext(), "Google authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}