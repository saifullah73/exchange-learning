package com.company.exchange_learning.loginsignup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.company.exchange_learning.Constants;
import com.company.exchange_learning.MainActivity;
import com.company.exchange_learning.R;
import com.company.exchange_learning.model.BasicUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;


public class loginActivity extends AppCompatActivity {
    private static final String TAG = "loginActivity";
    private CardView loginbtn, retryBtn;
    private TextView goToSignUpBtn;
    private EditText passwordView, emailView;
    private AVLoadingIndicatorView progressBar;
    private TextView forgetPass;
    private CheckBox rememberMe_v;
    private SharedPreferences prefs;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        checkPrefsAndLogin();
    }

    private void checkPrefsAndLogin() {
        prefs = getSharedPreferences("loginDetails", MODE_PRIVATE);
        String email = prefs.getString("email", "none");
        String password = prefs.getString("password", "none");
        if (!email.equals("none") && !password.equals("none")) {
            showGeneralLoadingLayout();
            quickLogin(email, password);
        } else {
            setViews();
        }
    }

    private void showGeneralLoadingLayout() {
        setContentView(R.layout.loading_layout);
    }

    private void setViews() {
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                Intent i = new Intent(loginActivity.this, signupActivity.class);
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

    private void quickLogin(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Constants.uid= user.getUid();
                            getUserInformation();
                        } else {
                            setViews();
                        }
                    }
                });
    }

    private void getUserInformation() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BasicUser basicUser = dataSnapshot.getValue(BasicUser.class);
                if (basicUser != null) {
                    Constants.uName = basicUser.getName();
                    Constants.uCommunity = basicUser.getCommunity();
                    Intent i = new Intent(loginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    showErrorLayout();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showErrorLayout();
            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User_Information").child(Constants.getConstantUid());
        myRef.addListenerForSingleValueEvent(listener);
    }

    private void showErrorLayout() {
        setContentView(R.layout.error_layout);
        retryBtn = findViewById(R.id.retryBtn);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserInformation();
            }
        });
    }

    private void login(final String email, final String password) {
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
                            if (user.isEmailVerified()) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("User_Information").child(user.getUid());
                                Map<String, Object> updates = new HashMap<>();
                                myRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            Log.d(TAG, "verification status updated successfully");
                                        } else {
                                            Log.d(TAG, "Error updating verification status " + databaseError.getMessage());
                                        }
                                    }
                                });
                                if (rememberMe_v.isChecked()) {
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                    editor.apply();
                                }
                                Constants.uid = user.getUid();
                                getUserInformation();
                                progressBar.hide();
                            } else {
                                showVerificationFailDialog(user);
                                FirebaseAuth.getInstance().signOut();
                            }
                        } else {
                            progressBar.hide();
                            Toast.makeText(loginActivity.this, "Error signing in", Toast.LENGTH_SHORT).show();
                        }
                        loginbtn.setEnabled(true);
                    }
                });
    }

    private boolean perfromCheck() {
        if (emailView.getText().length() == 0) {
            Toast.makeText(this, "email can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordView.getText().length() == 0) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void showVerificationFailDialog(final FirebaseUser user) {
        progressBar.hide();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.email_not_verified_layout, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.show();
        CardView resendEmail = view.findViewById(R.id.resend);
        TextView cancel = view.findViewById(R.id.cancelDl);
        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(loginActivity.this, "Verification email sent",
                                            Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(loginActivity.this, "Error while sending verification email",
                                            Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }
                        });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void perfromForgotPassAction() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.forgot_password_layout, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.show();
        final EditText recoverEmail = view.findViewById(R.id.recoverEmail);
        TextView submit = view.findViewById(R.id.submitEmail);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m_Text = recoverEmail.getText().toString().trim();
                if (!m_Text.equals("")) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(m_Text)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(loginActivity.this, "Password Recovery Email Sent", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    } else {
                                        Toast.makeText(loginActivity.this, "Error Sending Password Recovery Email", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(loginActivity.this, "Please Enter an email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPrefsAndLogin();
    }
}
