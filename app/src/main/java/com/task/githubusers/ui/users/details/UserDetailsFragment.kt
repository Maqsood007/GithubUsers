package com.task.githubusers.ui.users.details

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.task.githubusers.R
import com.task.githubusers.databinding.FragmentUserDetailsBinding
import com.task.githubusers.utils.AppConstants.DEFAULT_URL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailsFragment : Fragment() {

    private lateinit var binding: FragmentUserDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                binding.root.findViewById<WebView>(R.id.webViewer).settings.forceDark =
                    WebSettings.FORCE_DARK_ON
            }
        }

        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.webViewer.webViewClient = webVieClient
        binding.webViewer.settings.javaScriptEnabled = true

        binding.webViewer.loadUrl(
            arguments?.getString(URL_TO_BROWSE)
                ?: DEFAULT_URL
        )

        // Intercept the device back button
        binding.webViewer.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (binding.webViewer.canGoBack()) {
                    binding.webViewer.goBack()
                    true
                } else false
            } else {
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.webViewer.stopLoading()
    }

    private val webVieClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.progressBar.let {
                it.visibility = View.VISIBLE
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.progressBar.let {
                it.visibility = View.GONE
            }
        }
    }

    companion object {
        const val URL_TO_BROWSE = "url_to_browse"
        const val TOP_TITLE = "title"
    }
}
