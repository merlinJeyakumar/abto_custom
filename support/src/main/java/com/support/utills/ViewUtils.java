package com.support.utills;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mithrai.crowdai.Abstract.View.AlertDiag;
import com.mithrai.crowdai.Abstract.View.PopupDiag;

public class ViewUtils {
    private static String TAG = "ViewUtils";

    public static View getViewFromLayout(Activity mActivity, int mContainerID, int mLayoutID) {
        FrameLayout activityContainer = mActivity.findViewById(mContainerID);
        activityContainer.removeAllViews();
        View vi = mActivity.getLayoutInflater().inflate(mLayoutID, null);
        activityContainer.addView(vi);
        return activityContainer;
    }

    public static View getViewFromLayout(Activity mActivity, FrameLayout activityContainer, int mLayoutID) {
        activityContainer.removeAllViews();
        View vi = mActivity.getLayoutInflater().inflate(mLayoutID, null);
        activityContainer.addView(vi);
        return activityContainer;
    }

    public static View getViewFromLayout(Activity mActivity, int mLayoutID) {
        return mActivity.getLayoutInflater().inflate(mLayoutID, null, false);
    }

    public static AlertDiag getTransparentAlertDiag(Activity activity, View view) {
        AlertDiag alertDialog = new AlertDiag(activity);
        alertDialog.setView(view);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setCancelable(true);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return alertDialog;
    }

    public static void getSmoothAnimation(Activity activity) {
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    public static PopupDiag getPopup(Activity mInstance, PopupDiag popupWindow, View contentView, View anchorView) {
        if (popupWindow == null) {
            popupWindow = new PopupDiag(mInstance);
        }
        popupWindow.setFocusable(true);
        popupWindow.setContentView(contentView);
        popupWindow.setElevation(5);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchorView); // where u want show on view click event popupwindow.showAsDropDown(view, x, y);
        return popupWindow;
    }

    public static void setLeftDrawable() {
    }
}
