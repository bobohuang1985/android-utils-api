package com.bobo.commonutils.deviceinfo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.bobo.commonutils.utils.RunCmdApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by bobohuang(bobohuang!985@qq.com) on 2016/7/5.
 */


public class DeviceInfoUtils {
    private static final String TAG = "DeviceInfoUtils";

    public DeviceInfoUtils() {
    }

    public static Map<String, String> getAllBuildMap() {
        return readPropFile("/system/build.prop", "=");
    }

    public static Map<String, String> getAllMTKBuildMap() {
        LinkedHashMap mtkMap = new LinkedHashMap();
        Map allBuildMap = getAllBuildMap();
        Iterator iterator = allBuildMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry next = (Map.Entry) iterator.next();
            if (((String) next.getKey()).contains("mediatek")) {
                mtkMap.put(next.getKey(), next.getValue());
            }
        }

        return mtkMap;
    }

    public static Map<String, String> getAllOsMap() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("Brand", Build.BRAND);
        map.put("Model", Build.MODEL);
        map.put("Radio", Build.getRadioVersion());
        map.put("Bootloader", Build.BOOTLOADER);
        map.put("Product", Build.PRODUCT);
        map.put("Manufacturer", Build.MANUFACTURER);
        map.put("Device", Build.DEVICE);
        map.put("Display", Build.DISPLAY);
        map.put("CodeName", Build.VERSION.CODENAME);
        map.put("Release", Build.VERSION.RELEASE);
        map.put("SDK", String.valueOf(Build.VERSION.SDK_INT));
        map.put("Host", Build.HOST);
        map.put("CPUABI", Build.CPU_ABI);
        map.put("CPUABI2", Build.CPU_ABI2);
        map.put("fingerprint", Build.FINGERPRINT);
        return map;
    }

    public static Map<String, String> getAndroidIds(Context context) {
        LinkedHashMap map = new LinkedHashMap();
        String androidId = Settings.Secure.getString(context.getContentResolver(), "android_id");
        String imeistring = null;
        String imsistring = null;
        String serialnum = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imeistring = telephonyManager.getDeviceId();
            imsistring = telephonyManager.getSubscriberId();
            Class c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", new Class[]{String.class, String.class});
            serialnum = (String) ((String) get.invoke(c, new Object[]{"ro.serialno", "unknown"}));
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.put("IMEI", imeistring);
        map.put("IMSI", imsistring);
        map.put("SERIALNUM", serialnum);
        map.put("ANDROID_ID", androidId);
        return map;
    }

    /*public static Map<String, String> getMtkImeis(Activity activity) {
        LinkedHashMap map = new LinkedHashMap();
        ImeiUtils imeiUtils = new ImeiUtils(activity);
        IMSInfo imsiInfo = imeiUtils.getIMSInfo();
        if (imsiInfo != null) {
            map.put("Chip", imsiInfo.chipName);
            map.put("IMEI1", imsiInfo.imei_1);
            map.put("IMSI1", imsiInfo.imsi_1);
            map.put("IMEI2", imsiInfo.imei_2);
            map.put("IMSI2", imsiInfo.imsi_2);
        }
        return map;
    }*/

    public static Map<String, String> getScreenMap(Activity activity) {
        LinkedHashMap map = new LinkedHashMap();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        map.put("Width", String.valueOf(dm.widthPixels));
        map.put("Height", String.valueOf(dm.heightPixels));
        map.put("Density", String.valueOf(dm.density));
        map.put("ScaledDensity", String.valueOf(dm.scaledDensity));
        map.put("Width*density", String.valueOf(Math.round((float) dm.widthPixels * dm.density)));
        map.put("Height*density", String.valueOf(Math.round((float) dm.heightPixels * dm.density)));
        map.put("DensityDpi", String.valueOf(dm.densityDpi));
        map.put("xdpi", String.valueOf(dm.xdpi));
        map.put("ydpi", String.valueOf(dm.ydpi));
        map.put("metrics", dm.toString());
        return map;
    }

    public static Map<String, String> getSDCARDStoreMap() {
        LinkedHashMap map = new LinkedHashMap();
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state)) {
            File externalStorageDirectory = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(externalStorageDirectory.getPath());
            statFs.getAvailableBlocks();
            map.put("AvailableBlocks", String.valueOf(statFs.getAvailableBlocks()));
            map.put("BlockCount", String.valueOf(statFs.getBlockCount()));
            map.put("BlockSize", String.valueOf(statFs.getBlockSize()));
            map.put("FreeBlocks", String.valueOf(statFs.getFreeBlocks()));
        }

        return map;
    }

    public static Map<String, String> getRootStoreMap() {
        LinkedHashMap map = new LinkedHashMap();
        File externalStorageDirectory = Environment.getRootDirectory();
        StatFs statFs = new StatFs(externalStorageDirectory.getPath());
        statFs.getAvailableBlocks();
        map.put("AvailableBlocks", String.valueOf(statFs.getAvailableBlocks()));
        map.put("BlockCount", String.valueOf(statFs.getBlockCount()));
        map.put("BlockSize", String.valueOf(statFs.getBlockSize()));
        map.put("FreeBlocks", String.valueOf(statFs.getFreeBlocks()));
        return map;
    }

    public static Map<String, String> getMemoryMap(Activity activity) {
        String meminfoFile = "/proc/meminfo";
        LinkedHashMap map = new LinkedHashMap();
        ActivityManager systemService = (ActivityManager) activity.getSystemService("activity");
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        systemService.getMemoryInfo(mi);
        map.put("AvailMemByAndroid", String.valueOf(mi.availMem));
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(meminfoFile);
            BufferedReader e = new BufferedReader(fileReader);

            String line;
            while (null != (line = e.readLine())) {
                String[] split = line.split("\\s+");
                map.put(split[0], split[1] + "Kb");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException eee) {
                }
            }
        }
        return map;
    }
    /*
    public static Map<String, String> getSign(Context context) {
        Object map = new LinkedHashMap();
        PackageManager pm = context.getPackageManager();
        List apps = pm.getInstalledPackages(64);
        Iterator iter = apps.iterator();

        while (iter.hasNext()) {
            PackageInfo packageinfo = (PackageInfo) iter.next();
            String packageName = packageinfo.packageName;
            if (packageName.equals(context.getPackageName())) {
                Signature[] signatures = packageinfo.signatures;
                map = parseSignature(signatures[0].toByteArray());
            }
        }

        return (Map) map;
    }

    private static Map<String, String> parseSignature(byte[] signature) {
        LinkedHashMap map = new LinkedHashMap();

        try {
            CertificateFactory e = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) e.generateCertificate(new ByteArrayInputStream(signature));
            String pubKey = cert.getPublicKey().toString();
            String signNumber = cert.getSerialNumber().toString();
            map.put("PublicKey", pubKey);
            map.put("SignNumber", signNumber);
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return map;
    }*/

    public static Map<String, String> getCpuMap() {
        String meminfoFile = "/proc/cpuinfo";
        LinkedHashMap map = new LinkedHashMap();
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(meminfoFile);
            BufferedReader e = new BufferedReader(fileReader);

            String line;
            while (null != (line = e.readLine())) {
                String[] split = line.split(":");
                if (split.length > 1) {
                    map.put(split[0].trim(), split[1].trim());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    ;
                }
            }

        }

        return map;
    }

    public static Map<String, String> getMTDMap() {
        String meminfoFile = "/proc/mtd";
        LinkedHashMap map = new LinkedHashMap();
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(meminfoFile);
            BufferedReader e = new BufferedReader(fileReader);

            String line;
            while (null != (line = e.readLine())) {
                String[] split = line.split(":");
                if (split.length > 1) {
                    map.put(split[0].trim(), split[1].trim());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    ;
                }
            }

        }

        return map;
    }

    public static Map<String, String> getTelephonyMap(Activity activity) {
        String meminfoFile = "/proc/mtd";
        LinkedHashMap map = new LinkedHashMap();
        TelephonyManager tele = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        map.put("DeviceId(IMEI/MEID)", tele.getDeviceId());
        return map;
    }

    public static Map<String, String> getKernelMap() {
        String meminfoFile = "/proc/version";
        LinkedHashMap map = new LinkedHashMap();
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(meminfoFile);
            BufferedReader e = new BufferedReader(fileReader);
            String line = e.readLine();
            map.put("Kernel:", line);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                }
            }

        }

        return map;
    }

    public static Map<String, String> getMountMap() {
        Vector mount = RunCmdApi.execCmdWithReturn("mount");
        Object[] objects = mount.toArray();
        if (objects == null) {
            return null;
        } else {
            LinkedHashMap map = new LinkedHashMap();

            for (int i = 0; i < objects.length; ++i) {
                Object object = objects[i];
                if (object != null) {
                    String[] split = object.toString().split(" ");
                    map.put(split[0], object.toString());
                }
            }

            return map;
        }
    }

    private static Map<String, String> readPropFile(String file, String propSplit) {
        LinkedHashMap map = new LinkedHashMap();
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(file);
            BufferedReader e = new BufferedReader(fileReader);

            String line;
            while ((line = e.readLine()) != null) {
                Log.d("SystemUntils-Flag", line);
                if (line.contains(propSplit)) {
                    String[] split = line.split(propSplit);
                    if (split.length == 2) {
                        map.put(split[0], split[1]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ee) {
            ee.printStackTrace();
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                }
            }
        }
        return map;
    }
}
