package com.example.e_library;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import in.shadowfax.proswipebutton.ProSwipeButton;

import static com.example.e_library.CommonFunctions.showAlertDialog;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout tilEmail, tilPass, tilCPass, tilName;
    ProSwipeButton btnSignUp;
    FirebaseAuth mAuth;
    DatabaseReference mUserReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        tilEmail = findViewById(R.id.til_signup_activity_email);
        tilCPass = findViewById(R.id.til_signup_activity_cpass);
        tilPass = findViewById(R.id.til_signup_activity_pass);
        tilName = findViewById(R.id.til_signup_activity_name);
        btnSignUp = findViewById(R.id.btn_signup_activity_signup);
        btnSignUp.setOnSwipeListener(this::checkInputs);
        mAuth = FirebaseAuth.getInstance();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    //to check whether email and password entered are valid and confirm pass matches with pass
    //if not, raise an error
    //else proceed with signIn
    private void checkInputs() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPass.setError(null);
        tilCPass.setError(null);
        String email = tilEmail.getEditText().getText().toString().trim();
        String password = tilPass.getEditText().getText().toString();
        String cpass = tilCPass.getEditText().getText().toString();
        String name = tilName.getEditText().getText().toString().trim();
        if (email.isEmpty() || !email.contains("@") || !email.contains("."))
            tilEmail.getEditText().setError("Invalid Email Address!!");
        if (password.isEmpty() || password.length() < 6)
            tilPass.getEditText().setError("Password Length must be atleast 6!!");
        if (cpass.isEmpty() || cpass.length() < 6 || !cpass.equals(password))
            tilCPass.getEditText().setError("Password does not match!!");
        if (name.isEmpty())
            tilName.getEditText().setError("Name should Not be Empty!!");
        if (tilPass.getEditText().getError() == null && tilEmail.getEditText().getError() == null
                && tilCPass.getEditText().getError() == null && tilName.getEditText().getError() == null)
            signUpAccount(email, password, name);
        else setButtonIcon(false);
    }

    //create an account, send verification email and add record to database
    private void signUpAccount(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isComplete()) {
                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                    if (task1.isComplete()) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Name", name);
                        map.put("Email", email);
                        mUserReference.child(mAuth.getUid()).setValue(map).addOnSuccessListener(aVoid -> {
                            Toast.makeText(SignUpActivity.this, "A verification email is Sent to " + email + ". Please Verify your Email!!", Toast.LENGTH_LONG).show();
                            setButtonIcon(true);
                            mAuth.signOut();
                            new Handler().postDelayed(SignUpActivity.this::finish, 300);
                        });

                    } else {
                        showAlertDialog(task1.getException().getMessage(), SignUpActivity.this);
                        setButtonIcon(false);
                    }
                });
            } else {
                showAlertDialog(task.getException().getMessage(), SignUpActivity.this);
                setButtonIcon(false);
            }
        });
    }

    //set signup button state to true and false
    private void setButtonIcon(boolean val) {
        new Handler().postDelayed(() -> {
            btnSignUp.showResultIcon(val);
            btnSignUp.setCornerRadius(40);
        }, 100);
    }


}
