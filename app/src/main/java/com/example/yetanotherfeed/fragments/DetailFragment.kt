package com.example.yetanotherfeed.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.yetanotherfeed.database.getDatabase
import com.example.yetanotherfeed.databinding.FragmentDetailBinding
import com.example.yetanotherfeed.network.HtmlJSInterface
import com.example.yetanotherfeed.viewmodels.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*
import java.util.*
import java.io.File.separator




/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment(), Observer {

    private lateinit var viewModel: DetailViewModel
    private lateinit var progressBar: ProgressBar

    @SuppressLint("JavascriptInterface")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDetailBinding.inflate(inflater)
        val arguments = DetailFragmentArgs.fromBundle(arguments)

        val application = requireNotNull(this.activity).application
        val database = getDatabase(application)
        viewModel = ViewModelProviders.of(
            this, DetailViewModel
                .Factory(arguments.link, database.itemDao)
        )
            .get(DetailViewModel::class.java)

        binding.lifecycleOwner = this

        progressBar = binding.progressBar
        progressBar.max = 100

        binding.webView.apply {
            settings.javaScriptEnabled = true
            loadUrl(arguments.link)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {

                    super.onPageFinished(view, url)
                    progressBar.visibility = View.GONE
                    binding.webView.visibility = View.VISIBLE
//                    view.loadUrl(
//                        "javascript:(function() { "
//                                + "window.HTMLOUT.setHtml('<html>'+"
//
//                                + "document.getElementsByTagName('html')[0].innerHTML+'</html>');})();"
//                    )
                    viewModel.setCachedState()
                    webView?.saveWebArchive(arguments.link + ".xml")

                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

                    progressBar.visibility = View.VISIBLE
                    view.loadUrl(url)
                    return true

                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    viewModel.onError()
                    super.onReceivedError(view, request, error)
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    progressBar.progress = newProgress
                }
            }
        }

        val htmlJSInterface = HtmlJSInterface()
        binding.webView.addJavascriptInterface(htmlJSInterface, "HTMLOUT")
        htmlJSInterface.addObserver(this)

        return binding.root
    }

    override fun update(observable: Observable, observation: Any) {

        // Got full page source.
        if (observable is HtmlJSInterface) {
            observable.html = observation as String
        }
    }


}
