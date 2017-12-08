package budgetapp.napkkk.ourbudget2.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import budgetapp.napkkk.ourbudget2.R;
import budgetapp.napkkk.ourbudget2.model.UserDao;

public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        final Intent intent = new Intent(MainActivity.this, OnGroupActivity.class);

        sp = getSharedPreferences("FB_PROFILE", Context.MODE_PRIVATE);
        editor = sp.edit();
        initFirebase();

        ///// Login อยู่ ///////////////

        if (AccessToken.getCurrentAccessToken() != null) {
            Toast.makeText(MainActivity.this, "Already Logged in", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }

        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "budgetapp.napkkk.ourbudget2",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException ignored) {

        }

        //////////////////////////////////////////////////////////////////////////////////////

        ///////////////////// Login Access & Get Data ////////////////////////////////////
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Collections.singletonList("public_profile, email, user_birthday, user_friends"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("Main", response.toString());
                        try {
                            editor.putString("name", object.getString("name"));
                            editor.putString("imageid", object.getString("id"));
                            editor.apply();
                            addUser(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();

                Intent intent = new Intent(MainActivity.this, OnGroupActivity.class);
                startActivity(intent);


                Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


    }

    /////////////////////////// init&Setting ///////////////////////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    ///////////////////////////////////////////////////////////////////////////////

    //////////////////////////// Query /////////////////////////////////////////////////////

    private void addUser(JSONObject jsonObject) throws JSONException {

        if (!TextUtils.isEmpty(jsonObject.getString("name"))) {
            String id = databaseReference.child("User").push().getKey();

            editor.putString("loginHash", id);

            UserDao userDao = new UserDao();
            userDao.setUserName(jsonObject.getString("name"));
            userDao.setUserPic(jsonObject.getString("id"));


            databaseReference.child("User").child(jsonObject.getString("name")).setValue(userDao);
            databaseReference.child("User").child(jsonObject.getString("name")).child("inmember").child("dummy").setValue("dummy");
            databaseReference.child("User").child(jsonObject.getString("name")).child("own").child("dummy").setValue("dummy");
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "User Added failed", Toast.LENGTH_LONG).show();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

}
