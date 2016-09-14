package com.watch.customer.ui;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;

import com.watch.customer.util.CamParaUtil;
import com.watch.customer.util.FileUtil;
import com.watch.customer.util.ImageUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 16-3-18.
 */
public class CameraInterface {
    private static final String TAG = "hjq";
    private Camera mCamera;
    Object mSync = new Object();
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private float mPreviwRate = -1f;
    private static CameraInterface mCameraInterface;
    private int cameraPosition = 0;
    private Context mContext;
    OnFinishCallback mFinishCb;

    public interface CamOpenOverCallback{
        public void cameraHasOpened();
    }

    private CameraInterface(){

    }

    public static synchronized CameraInterface getInstance(){
        if(mCameraInterface == null){
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    public int getCameraId() {
        return cameraPosition;
    }

    /**打开Camera
     * @param callback
     */
    public void doOpenCamera(CamOpenOverCallback callback){
        Log.e(TAG, "Camera open index  ");
        try {
            mCamera = Camera.open(cameraPosition);
            callback.cameraHasOpened();
            Log.e(TAG, "Camera open over....");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchCamera() {
        int n = Camera.getNumberOfCameras();
        Log.d("hjq", "nr of camera = " + n);
        if (n == 1) {
            return;
        }

        if (cameraPosition == 1) {
            cameraPosition = 0;
        } else {
            cameraPosition = 1;
        }
    }

    public List<String> getSupportedFlashModes() {
        List<String> modes = mParams.getSupportedFlashModes();
        return modes;
    }

    public void setFlashMode(String mode) {
        mParams.setFlashMode(mode);

        mCamera.setParameters(mParams);
    }


    /**开启预览
     * @param holder
     * @param previewRate
     */
    public void doStartPreview(SurfaceHolder holder, float previewRate, int degree){
        Log.i(TAG, "doStartPreview..., isPreviewing = " + isPreviewing);
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
            CamParaUtil.getInstance().printSupportPictureSize(mParams);
            CamParaUtil.getInstance().printSupportPreviewSize(mParams);
            //设置PreviewSize和PictureSize
            Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
                    mParams.getSupportedPictureSizes(), previewRate, 640);
            mParams.setPictureSize(pictureSize.width, pictureSize.height);
            Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
                    mParams.getSupportedPreviewSizes(), previewRate, 640);
            mParams.setPreviewSize(previewSize.width, previewSize.height);
            Log.e("hjq", "degree = " + degree);
            mCamera.setDisplayOrientation(degree);

            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-picture")) {
                Log.d("hjq", "set up continous picture");
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCamera.setParameters(mParams);

            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();//开启预览
                mCamera.cancelAutoFocus();      // TODO: HOW TO SETUP AUTOFOCUS MODE
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            isPreviewing = true;
            mPreviwRate = previewRate;

            mParams = mCamera.getParameters(); //重新get一次
            Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            Log.i(TAG, "最终设置:PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);
        }
    }

    /**
     * 停止预览，释放Camera
     */
    public void doStopCamera() {
        synchronized (mSync) {
            if (null != mCamera) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                isPreviewing = false;
                mPreviwRate = -1f;
                mCamera.release();
                mCamera = null;
            }
        }
    }

    /**
     * 拍照
     */
    public void doTakePicture(Context context, OnFinishCallback cb) {
        if (isPreviewing && (mCamera != null)) {
            mContext = context;
            mFinishCb = cb;
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    /*为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量*/
    ShutterCallback mShutterCallback = new ShutterCallback()
            //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    {
        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    PictureCallback mRawCallback = new PictureCallback()
            // 拍摄的未压缩原数据的回调,可以为null
    {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
    };
    PictureCallback mJpegPictureCallback = new PictureCallback()
            //对jpeg图像数据的回调,最重要的一个回调
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                mCamera.stopPreview();
                isPreviewing = false;
            }

            android.hardware.Camera.CameraInfo info =
                    new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraPosition, info);
            float degree = 90.0f;
            if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                degree = 270.0f;
            }

            //保存图片到sdcard
            if (null != b) {
                //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
                //图片竟然不能旋转了，故这里要旋转下
                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, degree);
                String path = FileUtil.saveBitmap(mContext, rotaBitmap);
                if (path != null) {
                    addImageToGallery(path, mContext);
                }
            }

            //再次进入预览
            mCamera.startPreview();
            isPreviewing = true;

            if (mFinishCb != null) {
                mFinishCb.onFinish();
            }
        }
    };

    private void resetCamera()
    {
        if (mCamera != null)
        {
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public void autoFocus(Camera.AutoFocusCallback cb){
        mCamera.autoFocus(cb);
    }

    public interface OnFinishCallback {
        void onFinish();
    }

}
