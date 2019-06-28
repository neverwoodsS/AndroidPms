package com.aot.pms;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.aot.pms.abs.CustomPermission;
import com.aot.pms.abs.IExitListener;
import com.aot.pms.abs.IPermission;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created on 2019-06-28
 * Author: zhangll
 * Email: 413700858@qq.com
 */
public class PermissionRequest implements IPermission {

    private CustomPermission firstPermission;
    private WeakReference<Activity> weakRefActivity;

    private PermissionRequest(CustomPermission firstPermission, WeakReference<Activity> weakRefActivity) {
        this.firstPermission = firstPermission;
        this.weakRefActivity = weakRefActivity;
    }

    /**
     * 构建所有的权限后，调用该方法开始申请权限
     */
    public void requestPermissions() {
        //构建完所有的权限后，请求第一个权限；
        Activity activity = weakRefActivity.get();
        if (activity != null && firstPermission != null) {
            firstPermission.requestPermissions(activity);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Activity activity = weakRefActivity.get();
        if (firstPermission != null && activity != null) {
            firstPermission.onRequestPermissionsResult(activity, requestCode, permissions, grantResults);
        }
    }

    public static class Builder {
        private ArrayList<Item> permissions = new ArrayList<>();
        private CustomPermission firstPermission = null;
        private WeakReference<Activity> weakRefActivity;
        private IExitListener exitListener;

        public Builder setActivity(Activity activity) {
            weakRefActivity = new WeakReference<>(activity);
            return this;
        }


        /**
         * @param permissionName 申请的 权限，必须是Manifest.permission中定义的
         * @param tip            如果用户拒绝给该权限的提示
         * @return
         */
        public Builder addPermission(@NonNull String permissionName, String tip) {
            permissions.add(new Item(permissionName, null == tip ? permissionName : tip));
            return this;
        }

        /**
         * @param permissionName 申请的 权限，必须是Manifest.permission中定义的
         * @param tip            如果用户拒绝给该权限的提示
         * @param isForce        默认为true，权限必须要给，false 权限可忽略
         * @return
         */
        public Builder addPermission(@NonNull String permissionName, String tip, boolean isForce) {
            permissions.add(new Item(permissionName, null == tip ? permissionName : tip, isForce));
            return this;
        }

        /**
         * 设置退出的逻辑
         *
         * @param listener
         * @return
         */
        public Builder setExitListener(IExitListener listener) {
            exitListener = listener;
            return this;
        }

        /**
         * 构建所有的权限
         */
        public PermissionRequest build() {
            int i = 0;
            int resultCode = 1000;
            CustomPermission curPermission = null;//当前的权限
            CustomPermission prePermission = null;//上一个权限
            for (Item item : permissions) {
                curPermission = new CustomPermission(item.pmsName, item.pmsDesc, i, resultCode + i, item.isForce, exitListener);
                if (i == 0) {
                    firstPermission = curPermission;
                }
                if (prePermission != null) {
                    prePermission.setNextPermission(curPermission);
                }
                prePermission = curPermission;
                i++;
            }
            return new PermissionRequest(firstPermission, weakRefActivity);
        }
    }

    private static class Item {
        boolean isForce = true;
        String pmsName;
        String pmsDesc;

        public Item(String pmsName, String pmsDesc) {
            this.pmsName = pmsName;
            this.pmsDesc = pmsDesc;
        }

        public Item(String pmsName, String pmsDesc, boolean isForce) {
            this.pmsName = pmsName;
            this.pmsDesc = pmsDesc;
            this.isForce = isForce;
        }

        public boolean equals(String pmsName) {
            if (pmsName.equals(pmsName)) {
                return true;
            } else {
                return false;
            }
        }
    }
}