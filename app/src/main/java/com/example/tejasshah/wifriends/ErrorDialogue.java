package com.example.tejasshah.wifriends;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Tejas Shah on 5/2/2016.
 */
public class ErrorDialogue {
    public void showErrorText(String errorText,Context context){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("Error !!");

        // set dialog message
        alertDialogBuilder
                .setMessage(errorText)
                .setCancelable(false)
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }

}
