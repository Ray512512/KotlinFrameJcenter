package common.view.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kotlin.library.R;
import com.luck.picture.lib.tools.PictureFileUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import common.presentation.utils.FileUtil;
import common.presentation.utils.Lg;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 获取相册
     */
    public static final int REQUEST_PHOTO = 1;
    /**
     * 获取视频
     */
    public static final int REQUEST_VIDEO = 2;
    /**
     * 最小录制时间
     */
    private static final int MIN_RECORD_TIME = 3 * 1000;
    /**
     * 最长录制时间
     */
    private static final int MAX_RECORD_TIME = 10 * 1000;
    /**
     * 刷新进度的间隔时间
     */
    private static final int PLUSH_PROGRESS = 100;

    private Context mContext;
    /**
     * TextureView
     */
    private TextureView mTextureView;
    /**
     * 带手势识别
     */
    private CameraView mCameraView;
    /**
     * 录制按钮
     */
    private CameraProgressBar mProgressbar;
    /**
     * 顶部像机设置
     */
    private RelativeLayout rl_camera;

    /**
     * 底部设置
     */
    private RelativeLayout rl_bottome;
    /**
     * 关闭,选择,前后置
     */
    private TextView iv_close, iv_choice;
    private ImageView iv_facing;
    private ImageView iv_back;//返回界面
    /**
     * 闪光
     */
    private TextView tv_flash;
    /**
     * camera manager
     */
    private CameraManager cameraManager;
    /**
     * player manager
     */
    private MediaPlayerManager playerManager;
    /**
     * true代表视频录制,否则拍照
     */
    private boolean isSupportRecord;
    /**
     * 视频录制地址
     */
    private String recorderPath;

    /**
     * 拍照的图片地址
     */
    private String photoPath;

    /**
     * 录制视频的时间,毫秒
     */
    private int recordSecond;
    /**
     * 获取照片订阅, 进度订阅
     */
    private Subscription takePhotoSubscription, progressSubscription;
    /**
     * 是否正在录制
     */
    private boolean isRecording;

    /**
     * 是否为点了拍摄状态(没有拍照预览的状态)
     */
    private boolean isPhotoTakingState;
    private TextView tvSmallScreen;
    private TextView tvBigScreen;
    private String type;//1从首页过去的，2从认证界面过去的

    public static void lanuchForPhoto(Activity context) {
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivityForResult(intent, REQUEST_PHOTO);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        type=getIntent().getStringExtra("type");
        Lg.i("info","--------------type="+type);
        initView();
        initDatas();
    }

    private void initView() {
        mTextureView = (TextureView) findViewById(R.id.mTextureView);
        mCameraView = (CameraView) findViewById(R.id.mCameraView);
        mProgressbar = (CameraProgressBar) findViewById(R.id.mProgressbar);
        rl_camera = (RelativeLayout) findViewById(R.id.rl_camera);
        rl_bottome = (RelativeLayout) findViewById(R.id.rl_bottom);
        tvBigScreen=findViewById(R.id.tv_bigScreen);
        tvSmallScreen=findViewById(R.id.tv_smallScreen);
        tvBigScreen.setOnClickListener(this);
        tvSmallScreen.setOnClickListener(this);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        iv_close = (TextView) findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        iv_choice = (TextView) findViewById(R.id.iv_choice);
        iv_choice.setOnClickListener(this);
        iv_facing = (ImageView) findViewById(R.id.iv_facing);
        iv_facing.setOnClickListener(this);
        tv_flash = (TextView) findViewById(R.id.tv_flash);
        tv_flash.setOnClickListener(this);
    }

    protected void initDatas() {
        cameraManager = CameraManager.getInstance(getApplication());
        playerManager = MediaPlayerManager.getInstance(getApplication());
        cameraManager.setCameraType(isSupportRecord ? 1 : 0);

        setCameraFlashState();
        rl_camera.setVisibility(cameraManager.isSupportFlashCamera()
                || cameraManager.isSupportFrontCamera() ? View.VISIBLE : View.GONE);

        final int max = MAX_RECORD_TIME / PLUSH_PROGRESS;
        mProgressbar.setMaxProgress(max);

        mProgressbar.setOnProgressTouchListener(new CameraProgressBar.OnProgressTouchListener() {
            @Override
            public void onClick(CameraProgressBar progressBar) {
                  Toast.makeText(CameraActivity.this, "请长按拍摄", Toast.LENGTH_SHORT).show();
//                cameraManager.takePhoto(callback);
            }

            @Override
            public void onLongClick(CameraProgressBar progressBar) {
                isSupportRecord = true;
                cameraManager.setCameraType(1);
                rl_camera.setVisibility(View.GONE);
                recorderPath = FileUtils.getUploadVideoFile(mContext);
                cameraManager.startMediaRecord(recorderPath);
                isRecording = true;
                progressSubscription = Observable.interval(100, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).take(max).subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        mCameraView.setFoucs(true);
                        stopRecorder(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        mProgressbar.setProgress(mProgressbar.getProgress() + 1);
                    }
                });
            }

            @Override
            public void onZoom(boolean zoom) {
                cameraManager.handleZoom(zoom);
            }

            @Override
            public void onLongClickUp(CameraProgressBar progressBar) {
                mCameraView.setFoucs(true);
                //  isSupportRecord = false;
                cameraManager.setCameraType(0);
                stopRecorder(true);
                if (progressSubscription != null) {
                    progressSubscription.unsubscribe();
                }
            }

            @Override
            public void onPointerDown(float rawX, float rawY) {
                if (mTextureView != null) {

                    mCameraView.setFoucsPoint(new PointF(rawX, rawY));
                }
            }
        });

        mCameraView.setOnViewTouchListener(new CameraView.OnViewTouchListener() {
            @Override
            public void handleFocus(float x, float y) {
                Log.e("TAG", "===========handleFocus===?");
                cameraManager.handleFocusMetering(x, y);
            }

            @Override
            public void handleZoom(boolean zoom) {
                Log.e("TAG", "===========handleZoom===?");
                cameraManager.handleZoom(zoom);
            }
        });
    }

    /**
     * 设置闪光状态
     */
    private void setCameraFlashState() {
        int flashState = cameraManager.getCameraFlash();
        switch (flashState) {
            case 0: //自动
                tv_flash.setSelected(true);
                tv_flash.setText("自动");
                break;
            case 1://open
                tv_flash.setSelected(true);
                tv_flash.setText("开启");
                break;
            case 2: //close
                tv_flash.setSelected(false);
                tv_flash.setText("关闭");
                break;
            default:
        }
    }

    /**
     * 是否显示录制按钮
     *
     * @param isShow
     */
    private void setTakeButtonShow(boolean isShow) {
        if (isShow) {
            mProgressbar.setVisibility(View.VISIBLE);
            rl_camera.setVisibility(cameraManager.isSupportFlashCamera()
                    || cameraManager.isSupportFrontCamera() ? View.VISIBLE : View.GONE);
        } else {
            mProgressbar.setVisibility(View.GONE);
            rl_camera.setVisibility(View.GONE);
        }
    }

    /**
     * 停止拍摄
     */
    private void stopRecorder(boolean play) {
        isRecording = false;
        cameraManager.stopMediaRecord();
        recordSecond = mProgressbar.getProgress() * PLUSH_PROGRESS;//录制多少毫秒
        mProgressbar.reset();
        if (recordSecond < MIN_RECORD_TIME) {//小于最小录制时间作废
            Toast.makeText(CameraActivity.this, "视频录制时间需大于3秒钟", Toast.LENGTH_SHORT).show();
            if (recorderPath != null) {
                FileUtils.delteFiles(new File(recorderPath));
                recorderPath = null;
                recordSecond = 0;
            }
            setTakeButtonShow(true);
        } else if (play && mTextureView != null && mTextureView.isAvailable()) {
            setTakeButtonShow(false);
            mProgressbar.setVisibility(View.GONE);
            iv_choice.setVisibility(View.VISIBLE);
            cameraManager.closeCamera();
            playerManager.playMedia(new Surface(mTextureView.getSurfaceTexture()), recorderPath);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTextureView.isAvailable()) {
            if (recorderPath != null) {//优先播放视频
                iv_choice.setVisibility(View.VISIBLE);
                setTakeButtonShow(false);
                playerManager.playMedia(new Surface(mTextureView.getSurfaceTexture()), recorderPath);
            } else {
                iv_choice.setVisibility(View.GONE);
                setTakeButtonShow(true);
                cameraManager.openCamera(mTextureView.getSurfaceTexture(),
                        mTextureView.getWidth(), mTextureView.getHeight());
            }
        } else {
            mTextureView.setSurfaceTextureListener(listener);
        }
    }

    @Override
    protected void onPause() {
        if (progressSubscription != null) {
            progressSubscription.unsubscribe();
        }
        if (takePhotoSubscription != null) {
            takePhotoSubscription.unsubscribe();
        }
        if (isRecording) {
            stopRecorder(false);
        }
        cameraManager.closeCamera();
        playerManager.stopMedia();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mCameraView.removeOnZoomListener();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_close) {
            mCameraView.setFoucs(false);
            if (recorderPath != null) {//有拍摄好的正在播放,重新拍摄
                FileUtils.delteFiles(new File(recorderPath));
                recorderPath = null;
                recordSecond = 0;
                playerManager.stopMedia();
                setTakeButtonShow(true);
                iv_choice.setVisibility(View.GONE);
                cameraManager.openCamera(mTextureView.getSurfaceTexture(), mTextureView.getWidth(), mTextureView.getHeight());
            } else if (isPhotoTakingState) {
                isPhotoTakingState = false;
                iv_choice.setVisibility(View.GONE);
                setTakeButtonShow(true);
                cameraManager.restartPreview();
            } else {
                // finish();
            }

        } else if (i == R.id.iv_choice) {
            Lg.i("info", "------------------isSupportRecord---" + isSupportRecord);
            //  Toast.makeText(this,"选勾",Toast.LENGTH_SHORT).show();
            if (isSupportRecord) {//rue代表视频录制
                dealResult();
//                        UIControler.intentActivity(this,ReleaseGrowthRecordActivity.class,bundle,true);
            } else {
                if (type.contains("one")) {//首页发布出生记录
                    Bundle bundle = new Bundle();
                    bundle.putString("fileName", photoPath);
//                        UIControler.intentActivity(this,ReleaseGrowthRecordActivity.class,bundle,true);
                }
            }

        } else if (i == R.id.tv_flash) {
            cameraManager.changeCameraFlash(mTextureView.getSurfaceTexture(),
                    mTextureView.getWidth(), mTextureView.getHeight());
            setCameraFlashState();

        } else if (i == R.id.iv_facing) {
            cameraManager.changeCameraFacing(mTextureView.getSurfaceTexture(),
                    mTextureView.getWidth(), mTextureView.getHeight());

        } else if (i == R.id.iv_back) {
            finish();

        } else if (i == R.id.tv_bigScreen) {
            getAndroiodMatchScreen();

        } else if (i == R.id.tv_smallScreen) {
            getAndroiodSmallScreen();

        } else {
        }
    }

    private void dealResult(){
        String coverPath= PictureFileUtils.bitmap2File(PictureFileUtils.getVideoThumb2(recorderPath),
                FileUtil.getCacheFilePath(System.currentTimeMillis()+".jpg"));
        Intent intent = new Intent();
        intent.putExtra("videoPath", recorderPath);
        intent.putExtra("coverPath",coverPath);
        intent.putExtra("duration",recordSecond);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void getAndroiodSmallScreen() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        float width = dm.widthPixels;         // 屏幕宽度（像素）
        float height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        Log.d("h_bl", "------1:1的小屏-屏幕宽度（dp）：" + screenWidth);
        Log.d("h_bl", "-----1:1的小屏-屏幕高度（dp）：" + screenHeight);

        RelativeLayout.LayoutParams Params = (RelativeLayout.LayoutParams) mTextureView.getLayoutParams();
        Params.height = dip2px(screenWidth+60);
        Params.width = dip2px(screenWidth);
        mTextureView.setLayoutParams(Params);
        cameraManager.get(true,dip2px(screenWidth),  dip2px(screenWidth));
        cameraManager.initCameraParameters(0,dip2px(screenWidth),  dip2px(screenWidth + 60));
    }

    public void getAndroiodMatchScreen() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        float width = dm.widthPixels;         // 屏幕宽度（像素）
        float height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        Log.d("h_bl", "-------全屏幕宽度（dp）：" + screenWidth);
        Log.d("h_bl", "------全屏幕高度（dp）：" + screenHeight);

        RelativeLayout.LayoutParams Params = (RelativeLayout.LayoutParams) mTextureView.getLayoutParams();
        Params.height = dip2px(screenHeight);
        Params.width = dip2px(screenWidth);
        mTextureView.setLayoutParams(Params);
        cameraManager.get(true,dip2px(screenWidth), dip2px(screenHeight));
      //  cameraManager.initCameraParameters(0, dip2px(screenWidth), dip2px(screenHeight));
    }

    public int dip2px(float dpValue) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * camera回调监听
     */
    private TextureView.SurfaceTextureListener listener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            if (recorderPath != null) {
                iv_choice.setVisibility(View.VISIBLE);
                setTakeButtonShow(false);
                playerManager.playMedia(new Surface(texture), recorderPath);
            } else {
                setTakeButtonShow(true);
                iv_choice.setVisibility(View.GONE);
                cameraManager.openCamera(texture, width, height);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };


    //拍照的回调
    private Camera.PictureCallback callback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            setTakeButtonShow(false);
            takePhotoSubscription = Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    isSupportRecord = false;
                    if (!subscriber.isUnsubscribed()) {
                        photoPath = FileUtils.getUploadPhotoFile(mContext);
                        isPhotoTakingState = FileUtils.savePhoto(photoPath, data, cameraManager.isCameraFrontFacing());
                        subscriber.onNext(isPhotoTakingState);
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Boolean>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Boolean aBoolean) {
                    mCameraView.setFoucs(true);
                    if (aBoolean != null && aBoolean) {
                        iv_choice.setVisibility(View.VISIBLE);
                    } else {
                        setTakeButtonShow(true);
                    }
                }
            });
        }
    };

}
