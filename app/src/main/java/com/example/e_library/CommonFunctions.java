package com.example.e_library;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

class CommonFunctions {

    //show alert dialog with message
    static void showAlertDialog(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Oops!!")
                .setMessage(message)
                .setNegativeButton("Ok", null)
                .setCancelable(false)
                .show();
    }
}
