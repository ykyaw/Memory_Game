package iss.team1.ca.memorygame.comm;

import android.Manifest;

public class CommonConstant {

    public static class HttpUrl {
        private static final String root="http://www.jsjzx.top/Family/";

    }

    public static class Permission {
        public static String[] PERMISSION_CAMERA={
                Manifest.permission.CAMERA,
                Manifest.permission.VIBRATE,
                Manifest.permission.READ_CONTACTS,
        };
    }

    public static class DEFAULT_RES{
        public static final int DEFAULT_RES=200;
        public static final int DEFAULT_ERROR=0;
    }
}
