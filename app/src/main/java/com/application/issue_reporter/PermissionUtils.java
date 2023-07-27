package com.application.issue_reporter;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {
    public static final String TITLE = "Permission";

    public static final String PERMISSION_SD  = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_AUDIO  = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_CAMER  = Manifest.permission.CAMERA;
    public static final String PERMISSION_CONTACTS = Manifest.permission.READ_CONTACTS;


    public interface OnPermissionListener {


        void onPermissionGranted();


        void onPermissionDenied(String... permission);


        void alwaysDenied(String... permission);
    }

    private static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean checkPermission(Context context, String permission) {
        if(isOverMarshmallow()){
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }else {
                return false;
            }
        }
       return  true;
    }


    public static List<String> checkMultiplePermissions(Context context, String[] permissions) {
        List<String> permissionList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (!checkPermission(context, permissions[i])) {
                permissionList.add(permissions[i]);
            }
        }
        return permissionList;
    }


    public static void requestPermission(Context context, String permission, int requestCode) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
    }
    public static void requestFragmentPermission(Fragment fragment, String permission, int code) {
        fragment.requestPermissions(new String[]{permission},code);
    }


    public static void requestMultiplePermissions(Context context, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
    }


    public static boolean canAskAgainPermission(Context context, String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean canAgainFragmentPermission(Fragment fragment, String permission) {
        if (fragment.shouldShowRequestPermissionRationale(permission)) {
            return true;
        }else {
            return false;
        }
    }



    public static void checkPermission(Context context, String permission, OnPermissionListener callBack) {
        if (checkPermission(context, permission)) {
            callBack.onPermissionGranted();
        } else {
            if (canAskAgainPermission(context, permission)) {
                callBack.onPermissionDenied(permission);
            } else {
                callBack.alwaysDenied(permission);
            }
        }
    }


    public static boolean isPermissionRequestSuccess(int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }


    public static void onRequestPermissionResult(Context context, String permission, int[] grantResults, OnPermissionListener callback) {
        if (PermissionUtils.isPermissionRequestSuccess(grantResults)) {
            callback.onPermissionGranted();
        } else {
            if (PermissionUtils.canAskAgainPermission(context, permission)) {
                callback.onPermissionDenied(permission);
            } else {
                callback.alwaysDenied(permission);
            }
        }
    }


    public static void checkMultiplePermissions(Context context, String[] permissions, OnPermissionListener callBack) {
        List<String> noPassPermissionList = checkMultiplePermissions(context, permissions);
        if (noPassPermissionList.size() == 0) {
            callBack.onPermissionGranted();
        } else {
            boolean askAgain = true;
            for (int i = 0; i < noPassPermissionList.size(); i++) {
                String permission = noPassPermissionList.get(i);
                if (canAskAgainPermission(context, permission)) {
                    askAgain = false;
                    break;
                }
            }
            String[] noPassPermissionArray = noPassPermissionList.toArray(new String[noPassPermissionList.size()]);
            if (askAgain){
                callBack.alwaysDenied(noPassPermissionArray);
            }else {
                callBack.onPermissionDenied(noPassPermissionArray);
            }
        }
    }

    public static void onRequestFragmentPermissionResult(Fragment fragment, String permission, int[] grantResults, OnPermissionListener callback) {
        if (PermissionUtils.isPermissionRequestSuccess(grantResults)) {
            callback.onPermissionGranted();
        } else {
            if (PermissionUtils.canAgainFragmentPermission(fragment, permission)) {
                callback.onPermissionDenied(permission);
            } else {
                callback.alwaysDenied(permission);
            }
        }
    }
    /**
     * 用户申请多个权限回调
     */
    public static void onRequestMultiplePermissionsResult(Context context, String[] permissions, OnPermissionListener callback) {

        List<String> noPassPermissionList = checkMultiplePermissions(context, permissions);
        if (noPassPermissionList.size() == 0)
            callback.onPermissionGranted();
        else {
            boolean canAskAgain = false;
            for (int i = 0; i < noPassPermissionList.size(); i++) {
                if (!canAskAgainPermission(context, noPassPermissionList.get(i))) {
                    canAskAgain = true;
                    break;
                }
            }
            if (canAskAgain)
                callback.alwaysDenied(permissions);
            else
                callback.onPermissionDenied(permissions);
        }

    }
    public static void goToAppSetting(final Context context, String name) {
        String temp ="Permission Set";
        String tips = String.format(temp,name);
        new AlertDialog.Builder(context)
                .setTitle(PermissionUtils.TITLE)
                .setMessage(tips)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("cancel", null).show();

    }









}
