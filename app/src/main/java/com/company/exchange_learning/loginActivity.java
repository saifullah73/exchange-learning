package com.company.exchange_learning;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;

public class loginActivity extends AppCompatActivity {
    private static final String TAG = "loginActivity";
    private CardView loginbtn;
    private TextView goToSignUpBtn;
    private EditText passwordView,emailView;
    private AVLoadingIndicatorView progressBar;
    private TextView forgetPass;
    private CheckBox rememberMe_v;
    private SharedPreferences prefs;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        prefs = loginActivity.this.getPreferences(Context.MODE_PRIVATE);
        String email = prefs.getString("email","none");
        String password = prefs.getString("password","none");
//        if (!email.equals("none") && !password.equals("none")){
//            quickLogin(email,password);
//        }
        setViews();
    }

    private void setViews(){
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        loginbtn = findViewById(R.id.loginBtn);
        forgetPass = findViewById(R.id.forgetPass);
        rememberMe_v = findViewById(R.id.rememberMe);
        progressBar = findViewById(R.id.progressBar);
        goToSignUpBtn = findViewById(R.id.createOne);
        passwordView = findViewById(R.id.password);
        emailView = findViewById(R.id.email);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (perfromCheck()) {
                    login(emailView.getText().toString().trim(), passwordView.getText().toString().trim());
                }

            }
        });
        goToSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(loginActivity.this,signupActivity.class);
                startActivity(i);
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perfromForgotPassAction();
            }
        });
    }

    private void quickLogin(final String email,final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            Intent i = new Intent(loginActivity.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            setViews();
                        }
                    }
                });
    }

    private void login(final String email, final String password){
        hideKeyboard(loginActivity.this);
        loginbtn.setEnabled(false);
        progressBar.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified())
                            {
                                progressBar.hide();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("User_Information").child(user.getUid());
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("email_verified", "true");
                                myRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError == null){
                                            Log.d(TAG,"verification status updated successfully");
                                        }
                                        else{
                                            Log.d(TAG,"Error updating verification status " + databaseError.getMessage());
                                        }
                                    }
                                });
                                if (rememberMe_v.isChecked()){
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                    editor.apply();
                                }
                                Intent i = new Intent(loginActivity.this,MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else {
                                showVerificationFailDialog(user);
                                FirebaseAuth.getInstance().signOut();
                            }
                        } else {
                            progressBar.hide();
                            Toast.makeText(loginActivity.this,"Error logging in", Toast.LENGTH_SHORT).show();
                        }
                        loginbtn.setEnabled(true);
                    }
                });
    }

    private boolean perfromCheck(){
        if(emailView.getText().length() ==0) {
            Toast.makeText(this, "email can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (passwordView.getText().length() == 0) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showVerificationFailDialog(final FirebaseUser user){
        progressBar.hide();
        new AlertDialog.Builder(loginActivity.this)
                .setTitle("Email not verified")
                .setMessage("Please verify email before logging in, if you did not receive verification email, please use the option below")
                .setCancelable(false)
                .setPositiveButton("Resend Verfication Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseAuth.getInstance().signOut();
                                            Log.d(TAG,"Verfication Email sent");
                                        }
                                        else
                                        {
                                            Log.d(TAG,"Unable to send verfication email");
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel",null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void perfromForgotPassAction(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        builder.setCancelable(false);
        builder.setMessage("Please enter your email address, we'll send a password recovery details to that address");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 20;
        params.rightMargin = 20;
        input.setLayoutParams(params);
        builder.setView(input);
        builder.setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                String m_Text = input.getText().toString().trim();
                if (!m_Text.equals("")){
                    FirebaseAuth.getInstance().sendPasswordResetEmail(m_Text)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Toast.makeText(loginActivity.this,"Password Recovery Email Sent",Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                    else{
                                        Toast.makeText(loginActivity.this,"Error Sending Password Recovery Email",Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(loginActivity.this,"Please Enter an email",Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
