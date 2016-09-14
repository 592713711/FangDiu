package com.watch.customer.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.storage.StorageManager;
import android.util.Log;

import android.graphics.Bitmap;


public class FileUtil {
    private static final String TAG = "hjq";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static String storagePath = "";
    private static final String DST_FOLDER_NAME = "PlayCamera";

    /**
     * 删除文件
     *
     * @param context  程序上下文
     * @param fileName 文件名，要在系统内保持唯一
     * @return boolean 存储成功的标志
     */
    public static boolean deleteFile(Context context, String fileName) {
        return context.deleteFile(fileName);
    }

    /**
     * 文件是否存在
     *
     * @param context
     * @param fileName
     * @return
     */
    public static boolean exists(Context context, String fileName) {
        return new File(context.getFilesDir(), fileName).exists();
    }

    /**
     * 存储文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名，要在系统内保持唯一
     * @param content  文本内容
     * @return boolean 存储成功的标志
     */
    public static boolean writeFile(Context context, String fileName, String content) {
        boolean success = false;
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] byteContent = content.getBytes();
            fos.write(byteContent);

            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return success;
    }

    /**
     * 存储文本数据
     *

     * @param content  文本内容
     * @return boolean 存储成功的标志
     */
    public static boolean writeFile(String filePath, String content) {
        boolean success = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            byte[] byteContent = content.getBytes();
            fos.write(byteContent);

            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return success;
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readFile(Context context, String fileName) {
        if (!exists(context, fileName)) {
            return null;
        }
        FileInputStream fis = null;
        String content = null;
        try {
            fis = context.openFileInput(fileName);
            if (fis != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = fis.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                fis.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 读取文本数据
     *
     *
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readFile(String filePath) {
        if (filePath == null || !new File(filePath).exists()) {
            return null;
        }
        FileInputStream fis = null;
        String content = null;
        try {
            fis = new FileInputStream(filePath);
            if (fis != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = fis.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                fis.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容，失败返回null
     */
    public static String readAssets(Context context, String fileName) {
        InputStream is = null;
        String content = null;
        try {
            is = context.getAssets().open(fileName);
            if (is != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = is.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 存储单个Parcelable对象
     *
     * @param context      程序上下文
     * @param fileName     文件名，要在系统内保持唯一
     * @param parcelObject 对象必须实现Parcelable
     * @return boolean 存储成功的标志
     */
    public static boolean writeParcelable(Context context, String fileName, Parcelable parcelObject) {
        boolean success = false;
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            Parcel parcel = Parcel.obtain();
            parcel.writeParcelable(parcelObject, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
            byte[] data = parcel.marshall();
            fos.write(data);

            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return success;
    }

    /**
     * 存储List对象
     *
     * @param context  程序上下文
     * @param fileName 文件名，要在系统内保持唯一
     * @param list     对象数组集合，对象必须实现Parcelable
     * @return boolean 存储成功的标志
     */
    public static boolean writeParcelableList(Context context, String fileName, List<Parcelable> list) {
        boolean success = false;
        FileOutputStream fos = null;
        try {
            if (list instanceof List) {
                fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                Parcel parcel = Parcel.obtain();
                parcel.writeList(list);
                byte[] data = parcel.marshall();
                fos.write(data);

                success = true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return success;
    }

    /**
     * 读取单个数据对象
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return Parcelable, 读取到的Parcelable对象，失败返回null
     */
    @SuppressWarnings("unchecked")
    public static Parcelable readParcelable(Context context, String fileName, ClassLoader classLoader) {
        Parcelable parcelable = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = context.openFileInput(fileName);
            if (fis != null) {
                bos = new ByteArrayOutputStream();
                byte[] b = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(b)) != -1) {
                    bos.write(b, 0, bytesRead);
                }

                byte[] data = bos.toByteArray();

                Parcel parcel = Parcel.obtain();
                parcel.unmarshall(data, 0, data.length);
                parcel.setDataPosition(0);
                parcelable = parcel.readParcelable(classLoader);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            parcelable = null;
        } finally {
            if (fis != null) try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bos != null) try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return parcelable;
    }

    /**
     * 读取数据对象列表
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return List, 读取到的对象数组，失败返回null
     */
    @SuppressWarnings("unchecked")
    public static List<Parcelable> readParcelableList(Context context, String fileName, ClassLoader classLoader) {
        List<Parcelable> results = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = context.openFileInput(fileName);
            if (fis != null) {
                bos = new ByteArrayOutputStream();
                byte[] b = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(b)) != -1) {
                    bos.write(b, 0, bytesRead);
                }

                byte[] data = bos.toByteArray();

                Parcel parcel = Parcel.obtain();
                parcel.unmarshall(data, 0, data.length);
                parcel.setDataPosition(0);
                results = parcel.readArrayList(classLoader);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            results = null;
        } finally {
            if (fis != null) try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bos != null) try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    public static boolean saveSerializable(Context context, String fileName, Serializable data) {
        boolean success = false;
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            oos.writeObject(data);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    public static Serializable readSerialLizable(Context context, String fileName) {
        Serializable data = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(context.openFileInput(fileName));
            data = (Serializable) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

    /**
     * 从assets里边读取字符串
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 复制文件
     *
     * @param srcFile
     * @param dstFile
     * @return
     */
    public static boolean copy(String srcFile, String dstFile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {

            File dst = new File(dstFile);
            if (!dst.getParentFile().exists()) {
                dst.getParentFile().mkdirs();
            }

            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(dstFile);

            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return true;
    }

    /**
     * 初始化保存路径
     *
     * @return
     */
    private static String initPath(Context context) {
        if (storagePath.equals("")) {
            File file = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM);
            // Make sure the Pictures directory exists.
            file.mkdirs();
            storagePath = file.getPath()+"/Camera";
            Log.e("hjq","保存路径："+storagePath);
        }

        return storagePath;
    }



    public static String getStoragePath() {
        if ("".equals(storagePath)) {
            return initPath(null);
        }

        return storagePath;
    }

    /**
     * 保存Bitmap到sdcard
     *
     * @param b
     */
    public static String saveBitmap(Context context, Bitmap b) {
        String path = initPath(context);
        long dataTake = System.currentTimeMillis();
        String jpegName = path + "/" + dataTake + ".jpg";
        Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            Log.i(TAG, "saveBitmap成功");

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
//            MediaScannerConnection.scanFile(context,
//                    new String[]{path}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String path, Uri uri) {
//                            Log.i("ExternalStorage", "Scanned " + path + ":");
//                            Log.i("ExternalStorage", "-> uri=" + uri);
//                        }
//                    });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i(TAG, "saveBitmap:失败");
            e.printStackTrace();

            return null;
        }

        return jpegName;
    }
}
