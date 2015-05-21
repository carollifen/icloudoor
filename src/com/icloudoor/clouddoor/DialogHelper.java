package com.icloudoor.clouddoor;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * Created by Administrator on 2015/5/16.
 */
public class DialogHelper {

    public static void Confirm(Context ctx, CharSequence title, CharSequence message,
                               CharSequence okText, OnClickListener oklistener, CharSequence cancelText,
                               OnClickListener cancellistener) {
        AlertDialog.Builder builder = createDialog(ctx, title, message);
        builder.setPositiveButton(okText, oklistener);
        builder.setNegativeButton(cancelText, cancellistener);
        builder.create().show();
    }

    public static void Confirm(Context ctx, int titleId, int messageId,
                               int okTextId, OnClickListener oklistener,  int cancelTextId,
                               OnClickListener cancellistener) {
        Confirm(ctx, ctx.getText(titleId), ctx.getText(messageId), ctx.getText(okTextId), oklistener, ctx.getText(cancelTextId), cancellistener);
    }

    private static AlertDialog.Builder createDialog(Context ctx, CharSequence title,
                                                    CharSequence message) {
        AlertDialog.Builder builder = new Builder(ctx);
        builder.setMessage(message);
        if(title!=null)
        {
            builder.setTitle(title);
        }
        return builder;
    }

    @SuppressWarnings("unused")
    private static AlertDialog.Builder createDialog(Context ctx,int titleId, int messageId) {
        AlertDialog.Builder builder = new Builder(ctx);
        builder.setMessage(messageId);
        builder.setTitle(titleId);
        return builder;
    }
}
