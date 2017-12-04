package budgetapp.napkkk.ourbudget2.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import budgetapp.napkkk.ourbudget2.R;
import budgetapp.napkkk.ourbudget2.model.GroupDao;
import budgetapp.napkkk.ourbudget2.model.UserDao;

public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
//    TextView status;
    TextView nameLogin, emailLogin, genderLogin;
    CallbackManager callbackManager;
    Button doLogin;
    ProfileTracker profileTracker;
    ProfilePictureView profilePictureView;
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

        doLogin = findViewById(R.id.docontinue);

//        nameLogin = findViewById(R.id.nameLogin);
//        emailLogin = findViewById(R.id.emailLogin);
//        genderLogin = findViewById(R.id.genderLogin);
        profilePictureView = findViewById(R.id.imageLogin2);


        ///// Login อยู่ ///////////////

        if (AccessToken.getCurrentAccessToken() != null){
            Toast.makeText(MainActivity.this, "Already Logged in", Toast.LENGTH_SHORT).show();
            doLogin.setEnabled(true);
            startActivity(intent);
        }
        ////// ยังไม่ได้ Login ///////////
        else{
            doLogin.setEnabled(false);
        }

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "budgetapp.napkkk.ourbudget2",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("Main", response.toString());
                        try {
                            editor.putString("name", object.getString("name").toString());
                            editor.putString("imageid", object.getString("id").toString());

                            addUser(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();
                        setProfileToView(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();


                Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

//                status.setText("Login Success \n" + loginResult.getAccessToken().getUserId() + "\n" + loginResult.getAccessToken().getToken());


//                startActivity(intent);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        doLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setProfileToView(JSONObject jsonObject) {
        try {
//            emailLogin.setText(jsonObject.getString("email"));
//            emailLogin.setText("eiei");
//            genderLogin.setText(jsonObject.getString("gender"));
//            nameLogin.setText(jsonObject.getString("name"));
            String dummy = jsonObject.getString("email");

            profilePictureView.setPresetSize(ProfilePictureView.NORMAL);
            profilePictureView.setProfileId(sp.getString("imageid","null"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void addUser(JSONObject jsonObject) throws JSONException {

        //checking if the value is provided
        if (!TextUtils.isEmpty(jsonObject.getString("name"))) {
            String id = databaseReference.child("User").push().getKey();

            editor.putString("loginHash", id);

            UserDao userDao = new UserDao();
            userDao.setUserName(jsonObject.getString("name"));
            userDao.setUserPic(jsonObject.getString("id"));

            databaseReference.child("User").child(jsonObject.getString("name")).setValue(userDao);

            Toast.makeText(this, "User Added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }


}
