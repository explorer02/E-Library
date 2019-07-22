package com.example.e_library;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class StartActivity extends AppCompatActivity {

    TextInputLayout tilEmail, tilPass;
    ProSwipeButton btnLogin;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        tilEmail = findViewById(R.id.til_start_activity_email);
        tilPass = findViewById(R.id.til_start_activity_pass);
        btnLogin = findViewById(R.id.btn_start_activity_login);
        mAuth = FirebaseAuth.getInstance();
        btnLogin.setOnSwipeListener(this::checkInputs);
    }

    //to check whether email and password entered are valid
    //if not, raise an error
    //else proceed with signIn
    private void checkInputs() {
        tilEmail.setError(null);
        tilPass.setError(null);
        String email = tilEmail.getEditText().getText().toString().trim();
        String password = tilPass.getEditText().getText().toString();
        if (email.isEmpty() || !email.contains("@") || !email.contains("."))
            tilEmail.setError("Invalid Email Address");
        if (password.isEmpty() || password.length() < 6)
            tilPass.setError("Invalid Password");

        if (tilPass.getError() == null && tilEmail.getError() == null)
            signIntoAccount(email, password);
    }
    //to check whether email is verified and then sign in to account
    //if not verified, log out of account
    private void signIntoAccount(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (mAuth.getCurrentUser().isEmailVerified())
                    setButtonIcon(true);
                else {
                    showAlertDialog("Please Verify your Email Address!!");
                    mAuth.signOut();
                    setButtonIcon(false);
                }
            } else if (task.getException() != null) {
                showAlertDialog(task.getException().getMessage());
                setButtonIcon(false);
            }
        });
    }
    //show alert dialog with message
    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Oops!!")
                .setMessage(message)
                .setNegativeButton("Ok", null)
                .setCancelable(false)
                .show();
    }
    //set login button state to true and false
    private void setButtonIcon(boolean val) {
        new Handler().postDelayed(() -> btnLogin.showResultIcon(val), 100);
    }


    //check whether user want to exit app or not
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit??")
                .setMessage("Do you want to exit from the app?")
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", (dialogInterface, i) -> finishAffinity())
                .setCancelable(false)
                .show();
    }
}
