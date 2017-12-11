package com.practice.broadcastreceiver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JisangYou on 2017-11-23.
 */

public class PermissionUtil {
    private final int REQ_CODE;
    private final String[] permissions;

    public PermissionUtil(int REQ_CODE, String[] permissions){
        this.REQ_CODE = REQ_CODE;
        this.permissions = permissions;
    }

    public void check(Activity activity, IPermissionGrant pGrant){
        // 2. 버전 체크후 권한 요청
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestPermission(activity, pGrant);
        else {
            pGrant.run();
            //init();
            //loadList();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(Activity activity, IPermissionGrant pGrant){
        // 3. 권한에 대한 승인여부
        List<String> requires = new ArrayList<>();
        for(String perm : permissions){
            if(activity.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED){
                requires.add(perm);
            }
        }

        // 4. 승인이 안된 권한이 있을경우 승인 요청
        if(requires.size() > 0){
            String[] perms = new String[requires.size()];
            perms = requires.toArray(perms);
            activity.requestPermissions(perms, REQ_CODE);
        }
        else {
            pGrant.run();
        }
    }

    public boolean afterPermissionResult(int requestCode, int[] grantResults, IPermissionGrant pGrant){
        if(requestCode == REQ_CODE){
            boolean granted = true;
            for(int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED) granted = false;
            }

            if(granted){
                pGrant.run();
                return true;
            }
            else{
                pGrant.fail();
                // 승인이 안된경우 finish() 처리한다
            }
        }

        return false;
    }

    public interface IPermissionGrant{
        void run();
        void fail();
    }
}
