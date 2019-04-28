package common.view.camera;

import android.app.Application;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.presentation.utils.Lg;
import common.presentation.utils.ToastUtils;

/**
 * Created by you on 2016/10/22.
 * 相机管理类
 */

public final class CameraManager {
    private Application context;
    /**
     * camera
     */
    private Camera mCamera;
    /**
     * 视频录制
     */
    private MediaRecorder mMediaRecorder;
    /**
     * 相机闪光状态
     */
    private int cameraFlash;
    /**
     * 前后置状态
     */
    private int cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
    /**
     * 是否支持前置摄像,是否支持闪光
     */
    private boolean isSupportFrontCamera, isSupportFlashCamera;
    /**
     * 录制视频的相关参数
     */
    private CamcorderProfile mProfile;
    /**
     * 0为拍照, 1为录像
     */
    private int cameraType;

    private CameraManager(Application context) {
        this.context = context;
        isSupportFrontCamera = CameraUtils.isSupportFrontCamera();
        isSupportFlashCamera = CameraUtils.isSupportFlashCamera(context);
        if (isSupportFrontCamera) {
            cameraFacing = CameraUtils.getCameraFacing(context, Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if (isSupportFlashCamera) {
            cameraFlash = CameraUtils.getCameraFlash(context);
        }
    }

    private static CameraManager INSTANCE;

    public static CameraManager getInstance(Application context) {
        if (INSTANCE == null) {
            synchronized (CameraManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CameraManager(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 打开camera
     */
    public void openCamera(SurfaceTexture surfaceTexture, int width, int height) {
        if (mCamera == null) {
            mCamera = Camera.open(cameraFacing);//打开当前选中的摄像头
            mProfile = CamcorderProfile.get(cameraFacing, CamcorderProfile.QUALITY_HIGH);
            try {
                mCamera.setDisplayOrientation(90);//默认竖直拍照
                mCamera.setPreviewTexture(surfaceTexture);
                initCameraParameters(cameraFacing, width, height);
                mCamera.startPreview();
            } catch (Exception e) {
                Lg.i("info", e.toString());
                if (mCamera != null) {
                    mCamera.release();
                    mCamera = null;
                }
            }
        }
    }

    /**
     * 开启预览,前提是camera初始化了
     */
    public void restartPreview() {
        if (mCamera == null) return;
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            int zoom = parameters.getZoom();
            if (zoom > 0) {
                parameters.setZoom(0);
                mCamera.setParameters(parameters);
            }
            mCamera.startPreview();
        } catch (Exception e) {
            Lg.i("info", e.toString());
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
    }

    public void initCameraParameters(int cameraId, int width, int height) {
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes != null) {
                    if (cameraType == 0) {
                        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        }
                    } else {
                        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                        }
                    }
                }
            }
            parameters.setRotation(90);//设置旋转代码,
            switch (cameraFlash) {
                case 0:
                    // parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    break;
                case 1:
                    //  parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    break;
                case 2:
                    //    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    break;
            }
            List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            if (!isEmpty(pictureSizes) && !isEmpty(previewSizes)) {
                for (Camera.Size size : pictureSizes) {
                    //  Lg.i("info","---------------pictureSize " + size.width + "  " + size.height+"///"+ size.width/size.height);
                }
                for (Camera.Size size : pictureSizes) {
                    //  Lg.i("info","---------------previewSize " + size.width + "  " + size.height+"///"+ size.width/size.height);
                }
                Camera.Size optimalPicSize = getOptimalSize(pictureSizes, width, height);
                Camera.Size optimalPreSize = getOptimalSize(previewSizes, width, height);
                Lg.i("info", "TextureSize " + width + " " + height + " optimalSize pic " + optimalPicSize.width + " " + optimalPicSize.height + " pre " + optimalPreSize.width + " " + optimalPreSize.height);
                parameters.setPictureSize(optimalPicSize.width, optimalPicSize.height);
                parameters.setPreviewSize(optimalPreSize.width, optimalPreSize.height);
                mProfile.videoFrameWidth = optimalPreSize.width;
                mProfile.videoFrameHeight = optimalPreSize.height;

                // parameters.setPictureFormat(PixelFormat.JPEG);//设置照片输出的格式
                //  parameters.set("jpeg-quality", 85);//设置照片质量
                mProfile.videoBitRate = 5000000;//此参数主要决定视频拍出大小
            }
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            //  ToastUtils.show("该相机不支持拍摄切换");
        }
    }

    public void get(boolean isBigScreen, int widths, int heights) {
        int PreviewWidth = 0;
        int PreviewHeight = 0;
        Camera.Parameters parameters = mCamera.getParameters();
        // 选择合适的预览尺寸
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        try {
            if (isBigScreen) {
                PreviewWidth = widths;
                PreviewHeight = heights;
            } else {
                if (!isEmpty(pictureSizes) && !isEmpty(previewSizes)) {
                    PreviewWidth = pictureSizes.get(0).width;
                    PreviewHeight = pictureSizes.get(0).height;
                }
            }
            Camera.Size optimalPicSize = getOptimalSize(pictureSizes, PreviewWidth, PreviewHeight);
            Camera.Size optimalPreSize = getOptimalSize(previewSizes, PreviewWidth, PreviewHeight);
            //    Lg.i("info","TextureSize "+PreviewWidth+" "+PreviewHeight+" optimalSize pic " + optimalPicSize.width + " " + optimalPicSize.height + " pre " + optimalPreSize.width + " " + optimalPreSize.height);
            parameters.setPictureSize(optimalPicSize.width, optimalPicSize.height);
            parameters.setPreviewSize(optimalPreSize.width, optimalPreSize.height);
            mProfile.videoFrameWidth = optimalPreSize.width;
            mProfile.videoFrameHeight = optimalPreSize.height;
            mCamera.setParameters(parameters);//把上面的设置 赋给摄像头
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show("该设备不支持该比例摄像");
        }
    }

    public int dip2px(float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 释放摄像头
     */
    public void closeCamera() {
        this.cameraType = 0;
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                Lg.i("info", e.toString());
                if (mCamera != null) {
                    mCamera.release();
                    mCamera = null;
                }
            }
        }
    }

    /**
     * 集合不为空
     *
     * @param list
     * @param <E>
     * @return
     */
    private <E> boolean isEmpty(List<E> list) {
        return list == null || list.isEmpty();
    }

    /**
     * 获取最佳预览相机Size参数
     *
     * @return
     */
    private Camera.Size getOptimalSize(List<Camera.Size> sizes, int w, int h) {
        Camera.Size optimalSize = null;
        float targetRadio = h / (float) w;
        float optimalDif = Float.MAX_VALUE; //最匹配的比例
        int optimalMaxDif = Integer.MAX_VALUE;//最优的最大值差距
        for (Camera.Size size : sizes) {
            float newOptimal = size.width / (float) size.height;
            float newDiff = Math.abs(newOptimal - targetRadio);
            if (newDiff < optimalDif) { //更好的尺寸
                optimalDif = newDiff;
                optimalSize = size;
                optimalMaxDif = Math.abs(h - size.width);
            } else if (newDiff == optimalDif) {//更好的尺寸
                int newOptimalMaxDif = Math.abs(h - size.width);
                if (newOptimalMaxDif < optimalMaxDif) {
                    optimalDif = newDiff;
                    optimalSize = size;
                    optimalMaxDif = newOptimalMaxDif;
                }
            }
        }
        return optimalSize;
    }

    private Camera.Parameters localParams;

    private void setParams(Camera.Parameters parameters) {
        localParams = parameters;
        mCamera.setParameters(parameters);
    }

    /**
     * 缩放
     *
     * @param isZoomIn
     */
    public void handleZoom(boolean isZoomIn) {

        Log.e("TAG", "-----handleZoom------?");
        if (mCamera == null)
            return;
        /*try {
            mCamera.lock();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Camera.Parameters params = null;
        params = mCamera.getParameters();
        if (params == null) return;
        if (params.isZoomSupported()) {
            int maxZoom = params.getMaxZoom();
            int zoom = params.getZoom();
            if (isZoomIn && zoom < maxZoom) {
                zoom++;
            } else if (zoom > 0) {
                zoom--;
            }
            params.setZoom(zoom);
            mCamera.setParameters(params);
        } else {
            Lg.i("info", "zoom not supported");
        }
       /* try {
            mCamera.unlock();
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    /**
     * 更换前后置摄像
     */
    public void changeCameraFacing(SurfaceTexture surfaceTexture, int width, int height) {
        if (isSupportFrontCamera) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
            for (int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
                if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) { //现在是后置，变更为前置
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位为前置
                        closeCamera();
                        cameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
                        CameraUtils.setCameraFacing(context, cameraFacing);
                        openCamera(surfaceTexture, width, height);
                        break;
                    }
                } else {//现在是前置， 变更为后置
                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位
                        closeCamera();
                        cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
                        CameraUtils.setCameraFacing(context, cameraFacing);
                        openCamera(surfaceTexture, width, height);
                        break;
                    }
                }
            }
        } else { //不支持摄像机
            Toast.makeText(context, "您的手机不支持前置摄像", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 改变闪光状态
     */
    public void changeCameraFlash(SurfaceTexture surfaceTexture, int width, int height) {
        if (!isSupportFlashCamera) {
            Toast.makeText(context, "您的手机不支闪光", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters != null) {
                int newState = cameraFlash;
                switch (cameraFlash) {
                    case 0: //自动
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        newState = 1;
                        break;
                    case 1://open
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        newState = 2;
                        break;
                    case 2: //close
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                        newState = 0;
                        break;
                }
                cameraFlash = newState;
                CameraUtils.setCameraFlash(context, newState);
                mCamera.setParameters(parameters);
            }
        }
    }

    /**
     * 拍照
     */
    public void takePhoto(Camera.PictureCallback callback) {
        if (mCamera != null) {
            try {
                mCamera.takePicture(null, null, callback);
            } catch (Exception e) {
                Toast.makeText(context, "拍摄失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 开始录制视频
     */
    public void startMediaRecord(String savePath) {
        try {
            //要在实例化MediaRecorder之前就解锁好相机
            mCamera.unlock();
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
                mMediaRecorder.setOrientationHint(90);
            }
            if (isCameraFrontFacing()) {
                mMediaRecorder.setOrientationHint(270);
            }
            //将相机设置给MediaRecorder
            mMediaRecorder.setCamera(mCamera);
            // 设置录制视频源和音频源
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            // 设置录制的视频编码和音频编码
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
            mMediaRecorder.setVideoSize(1920, 1080);
            // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
            // 设置视频文件输出的路径
//            mMediaRecorder.setProfile(mProfile);
            mMediaRecorder.setOutputFile(savePath);
            //暂停住   在外面就可以调用start进行录制了
            mMediaRecorder.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mMediaRecorder.start();
    }

    /**
     * 停止录制
     */
    public void stopMediaRecord() {
        this.cameraType = 0;
        stopRecorder();
        releaseMediaRecorder();
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
                mCamera.lock();
            } catch (Exception e) {
                e.printStackTrace();
                Lg.i("info", e.toString());
            }
        }
    }

    private void stopRecorder() {
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
                Lg.i("info", e.toString());
            }

        }
    }

    public boolean isSupportFrontCamera() {
        return isSupportFrontCamera;
    }

    public boolean isSupportFlashCamera() {
        return isSupportFlashCamera;
    }

    public boolean isCameraFrontFacing() {
        return cameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    /**
     * 设置对焦类型
     *
     * @param cameraType
     */
    public void setCameraType(int cameraType) {
        try {
            this.cameraType = cameraType;
            if (mCamera != null) {//拍摄视频时
                if (cameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    Camera.Parameters parameters = mCamera.getParameters();
                    List<String> focusModes = parameters.getSupportedFocusModes();
                    if (focusModes != null) {
                        if (cameraType == 0) {
                            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                            }
                        } else {
                            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCameraFlash() {
        return cameraFlash;
    }

    /**
     * 对焦
     *
     * @param x
     * @param y
     */
    public void handleFocusMetering(float x, float y) {
        Camera.Parameters params = mCamera.getParameters();
        Camera.Size previewSize = params.getPreviewSize();
        Rect focusRect = calculateTapArea(x, y, 1f, previewSize);
        Rect meteringRect = calculateTapArea(x, y, 1.5f, previewSize);
        mCamera.cancelAutoFocus();

        if (params.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 1000));
            params.setFocusAreas(focusAreas);
        } else {
            Lg.i("info", "focus areas not supported");
        }
        if (params.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<>();
            meteringAreas.add(new Camera.Area(meteringRect, 1000));
            params.setMeteringAreas(meteringAreas);
        } else {
            Lg.i("info", "metering areas not supported");
        }
        final String currentFocusMode = params.getFocusMode();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(params);

        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(currentFocusMode);
                camera.setParameters(params);
            }
        });
    }

    private Rect calculateTapArea(float x, float y, float coefficient, Camera.Size previewSize) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / previewSize.width - 1000);
        int centerY = (int) (y / previewSize.height - 1000);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

}
