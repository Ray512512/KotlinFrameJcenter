package common.web

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.kotlin.library.R
import common.presentation.kotlinx.extensions.onClick
import common.presentation.kotlinx.extensions.unSafeLazy
import kotlinx.android.synthetic.main.activity_web.*


class WebActivity :  AppCompatActivity(), WebViewAgent.WebAgentCallBack {
   /* override fun injectDependencies() {
    }

    override fun initPresenter()=emptyPresenter*/
    companion object {
        private val EXTRA_DATA = "extra_data"
        private val EXTRA_URL = "extra_url"
        private val EXTRA_TITLE = "extra_title"

        const  val TEXT_URL = "https://www.baidu.com/"
        const val TITLE_ABOUT_US = "关于我们"

        fun start(context: Context, extraURL: String, extraTitle: String): Intent {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(EXTRA_URL, extraURL)
            intent.putExtra(EXTRA_TITLE, extraTitle)
            context.startActivity(intent)
            return intent
        }
    }

    private var mUrl: String? = null
    private var mTitle: String? = null
    private  val webAgent: WebViewAgent by unSafeLazy {
        WebViewAgent(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_web)
        super.onCreate(savedInstanceState)
        mUrl = intent.getStringExtra(EXTRA_URL)
        mTitle = intent.getStringExtra(EXTRA_TITLE)

        webAgent.setWebViewSetting(webView)
        webAgent.loadUrl(mUrl!!)
        webAgent.callBack=this

        findViewById<TextView>(R.id.tv_title).text = mTitle
        findViewById<ImageView>(R.id.img_back).onClick {
            goBack()
        }
    }

    override fun onWebLoadProgress(newProgress: Int) {
        progressbar.progress = newProgress
        if (newProgress == 100) {
            progressbar.visibility = View.GONE
        } else {
            progressbar.visibility = View.VISIBLE
        }
    }

    private fun refresh() {
        webView.reload()
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    goBack()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun goBack(){
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
    }
}
