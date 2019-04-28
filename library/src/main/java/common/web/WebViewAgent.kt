package common.web

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.webkit.*
import com.kotlin.library.R
import common.presentation.rxutil.RxInterface
import common.presentation.rxutil.RxUtil
import common.presentation.utils.NetUtils
import java.io.File

/**
 * Created by Ray on 2017/12/29.
 */
class WebViewAgent constructor(val activity: AppCompatActivity): LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){
        mWebView?.onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        mWebView?.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestory(){
        mWebView?.destroy()
    }
    private var mWebView:WebView?=null
    private var uploadMessage: ValueCallback<Uri>? = null
    private var uploadMessageAboveL: ValueCallback<Array<Uri>>? = null
    private var mCurrentPhotoPath: String? = null
    private var mLastPhothPath: String? = null
    private var mThread: Thread? = null

    private val REQUEST_CODE_ALBUM = 0x01
    private val REQUEST_CODE_CAMERA = 0x02
    private val REQUEST_CODE_PERMISSION_CAMERA = 0x03

    @SuppressLint("SetJavaScriptEnabled")
    fun setWebViewSetting(webView: WebView) {
        activity.lifecycle.addObserver(this)
        this.mWebView=webView
        mWebView?.webViewClient = WebViewClient()
        mWebView?.webChromeClient = WebChromeClient()
        val msetting = mWebView?.settings
        msetting?.javaScriptEnabled = true

        val settings = mWebView?.settings
        settings?.javaScriptEnabled = true
        settings?.loadWithOverviewMode = true
        settings?.setAppCacheEnabled(true)
        settings?.domStorageEnabled = true
        settings?.databaseEnabled = true
        if (NetUtils.checkNetWork(mWebView?.context)) {
            settings?.cacheMode = WebSettings.LOAD_DEFAULT
        } else {
            settings?.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }
        settings?.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings?.setSupportZoom(true)
        mWebView?.webChromeClient = object :WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                    callBack?.onWebLoadProgress(newProgress)
            }
            //For Android  >= 5.0
            override  fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: FileChooserParams): Boolean {
                uploadMessageAboveL = filePathCallback
                //调用系统相机或者相册
                RxUtil.askPermission(activity, R.string.photo_need_permission,object : RxInterface.simple{
                    override fun action() {
                        chooseAlbumPic()
                    }
                }, Manifest.permission.CAMERA)
                return true
            }
        }
        mWebView?.webViewClient = object :WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                Log.v("LoveClient1", url)
                return if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url)
                    false
                } else {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        mWebView?.context?.startActivity(intent)
                    }catch (e: ActivityNotFoundException){
                        e.printStackTrace()
                    }
                    true
                }
            }
        }
    }

    var callBack: WebAgentCallBack?=null
    interface WebAgentCallBack{
        fun onWebLoadProgress(progress:Int)
    }
    /**
     * 选择相册照片
     */
    private fun chooseAlbumPic() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        activity.startActivityForResult(Intent.createChooser(i, "Image Chooser"), REQUEST_CODE_ALBUM)
    }

    /**
     * 加载url内容
     */
    fun loadUrl(url:String){
        if(url.isNotEmpty())
            mWebView?.loadUrl(url)
    }

    /**
     * 加载标签内容
     */
    fun initAndLoad(webView: WebView,content: String){
        mWebView=webView
        mWebView?.loadDataWithBaseURL(null,
                getHtmlData(content), "text/html", "utf-8", null)
    }
    /**
     * 加载标签内容
     */
    fun loadDataWithBaseURL(content: String){
        mWebView?.loadDataWithBaseURL(null,
                getHtmlData(content), "text/html", "utf-8", null)
    }

    /**
     * 加载html标签
     *
     * @param bodyHTML
     * @return
     */
    private fun getHtmlData(bodyHTML: String): String {
        val head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto!important;}</style>" +
                "</head>"
        return "<html>$head<body>$bodyHTML</body></html>"
    }

    /**
     * 宿主activity需要照片回调时调用
     */
     fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_ALBUM || requestCode == REQUEST_CODE_CAMERA) {
            if (uploadMessage == null && uploadMessageAboveL == null) {
                return
            }
            //取消拍照或者图片选择时,返回null,否则<input file> 就是没有反应
            if (resultCode != Activity.RESULT_OK) {
                if (uploadMessage != null) {
                    uploadMessage?.onReceiveValue(null)
                    uploadMessage = null
                }
                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL?.onReceiveValue(null)
                    uploadMessageAboveL = null
                }
            }

            //拍照成功和选取照片时
            if (resultCode == Activity.RESULT_OK) {
                var imageUri: Uri? = null

                when (requestCode) {
                    REQUEST_CODE_ALBUM ->
                        if (data != null) {
                            imageUri = data.data
                        }
                    REQUEST_CODE_CAMERA ->
                        if (!TextUtils.isEmpty(mCurrentPhotoPath)) {
                            val file = File(mCurrentPhotoPath)
                            val localUri = Uri.fromFile(file)
                            val localIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri)
                            activity.sendBroadcast(localIntent)
                            imageUri = Uri.fromFile(file)
                            mLastPhothPath = mCurrentPhotoPath
                        }
                }
                //上传文件
                if (uploadMessage != null) {
                    uploadMessage?.onReceiveValue(imageUri)
                    uploadMessage = null
                }
                if (uploadMessageAboveL != null&&imageUri!=null) {
                    uploadMessageAboveL?.onReceiveValue(arrayOf(imageUri))
                    uploadMessageAboveL = null
                }
            }
        }
    }
}
