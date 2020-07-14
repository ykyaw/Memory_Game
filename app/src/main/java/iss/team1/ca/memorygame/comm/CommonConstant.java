package iss.team1.ca.memorygame.comm;

import android.Manifest;

public class CommonConstant {

    public static class HttpUrl {
        //        private static final String root="http://192.168.43.124:8080/Family/";
        private static final String root="http://www.jsjzx.top/Family/";
        public static final String  SENDMSG=root+"SendMSG";
        public static final String VERIFYSMS=root+"VerifySMS";
        public static final String REGISTER=root+"Register";
        public static final String RECODER_GEOLOCATION=root+"RecordGeolocation";
        public static final String ADDFAMILYMEMBER=root+"AddFamilyMember";
        public static final String GETFAMILYMEMBERS=root+"GetFamilyMembers";
        public static final String GETUSER=root+"GetUser";
        public static final String GETFAMILYMEMBERLOCATION=root+"GetFamilyMemberLocation";
    }

    public static class Permission {
        public static String[] PERMISSION_CAMERA={
                Manifest.permission.CAMERA,
                Manifest.permission.VIBRATE,
                Manifest.permission.READ_CONTACTS,
        };
    }
}
