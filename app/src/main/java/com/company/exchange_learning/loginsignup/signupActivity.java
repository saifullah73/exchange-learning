package com.company.exchange_learning.loginsignup;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.company.exchange_learning.R;
import com.company.exchange_learning.model.BasicUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.util.Objects;

public class signupActivity extends AppCompatActivity {
    private EditText fname_v, lname_v, city_v, password_v, email_v;
    private Spinner countrySpinner, communitySpinner;
    private CardView signUp;
    private TextView goToLogin;
    private AVLoadingIndicatorView progressBar;
    private RadioGroup rgroup;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAuth = FirebaseAuth.getInstance();
        fname_v = findViewById(R.id.signUpFNametextInputEditText);
        lname_v = findViewById(R.id.signUpLNametextInputEditText);
        city_v = findViewById(R.id.signUpCitySignup);
        progressBar = findViewById(R.id.signup_prog);
        password_v = findViewById(R.id.signUpPasswordtextInputEditText);
        email_v = findViewById(R.id.signUpEmailtextInputEditText);
        countrySpinner = findViewById(R.id.country_spinner);
        communitySpinner = findViewById(R.id.community_spinner);
        rgroup = findViewById(R.id.signup_rg);
        signUp = findViewById(R.id.signUpButton);
        populateCommunity();
        populateCountry();
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });
        goToLogin = findViewById(R.id.goBackToLogin);
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private String getGender() {
        try {
            int selectedId = rgroup.getCheckedRadioButtonId();
            RadioButton radioButton = findViewById(selectedId);
            return radioButton.getText().toString();
        } catch (Exception e) {
            return null;
        }
    }


    public void signup() {
        final String fname = WordUtils.capitalize(fname_v.getText().toString().trim());

        final String lname = WordUtils.capitalize(lname_v.getText().toString().trim());

        final String email = email_v.getText().toString().trim();

        String password = password_v.getText().toString().trim();

        final String city = StringUtils.capitalize(city_v.getText().toString().trim());

        final String country = WordUtils.capitalize(countrySpinner.getSelectedItem().toString());
        final String community = WordUtils.capitalize(communitySpinner.getSelectedItem().toString());

        final String gender = getGender();


        if (fname.length() == 0 || email.length() == 0 || lname.length() == 0 || city.length() == 0 || gender == null
        ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else if (password.length() == 0) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        } else {
            signUp.setVisibility(View.INVISIBLE);
            hideKeyboard(signupActivity.this);
            progressBar.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(fname + " " + lname)
                                        .build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                }
                                            }
                                        });
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                } else {
                                                }
                                            }
                                        });
                                try {
                                    BasicUser newUser = new BasicUser(fname + " " + lname, gender, country, city, community, email, "none");
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("User_Information").child(user.getUid());
                                    myRef.setValue(newUser, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                signUp.setVisibility(View.VISIBLE);
                                                progressBar.hide();
                                                Toast.makeText(signupActivity.this, "Some Error occurred while signing up", Toast.LENGTH_SHORT).show();
                                            } else {
                                                signUp.setVisibility(View.VISIBLE);
                                                progressBar.hide();
                                                showEmailSentDialog();
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    signUp.setVisibility(View.VISIBLE);
                                    progressBar.hide();
                                    Toast.makeText(signupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                FirebaseAuth.getInstance().signOut();
                            } else {
                                signUp.setVisibility(View.VISIBLE);
                                progressBar.hide();
                                Toast.makeText(signupActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void populateCommunity() {
        String[] spinnerArray = getResources().getStringArray(R.array.community_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        communitySpinner.setAdapter(adapter);
    }

    private void populateCountry() {
        String[] spinnerArray = getResources().getStringArray(R.array.country_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);
    }


    public void showEmailSentDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.verification_email_sent_layout, null, false);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.show();
        CardView dismissBtn = view.findViewById(R.id.cancelDialog);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
    }
}
