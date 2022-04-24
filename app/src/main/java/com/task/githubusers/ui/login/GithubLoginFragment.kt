package com.task.githubusers.ui.login

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.task.githubusers.R
import com.task.githubusers.databinding.FragmentUserDetailsBinding
import com.task.githubusers.utils.github.GithubAuthentication
import com.task.githubusers.utils.github.githubAuthURLFull
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GithubLoginFragment : Fragment() {

    private lateinit var binding: FragmentUserDetailsBinding
    private val githubLoginViewModel by activityViewModels<GithubLoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserDetailsBinding.inflate(inflater, container, false)

        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
            githubAuthURLFull
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

        setStateListener()
    }

    private fun setStateListener() {
        githubLoginViewModel.loginStateLiveData.observe(viewLifecycleOwner) {
            onStateChanged(it)
        }
    }

    private fun onStateChanged(loginViewState: LoginViewState) = when (loginViewState) {
        is LoginViewState.StateDataLoaded -> {
            accessCodeReceived()
        }
        is LoginViewState.StateError -> Unit
        else -> Unit
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.webViewer.stopLoading()
    }

    private fun accessCodeReceived() {
        findNavController().apply {
            githubLoginViewModel.loginStateLiveData.value = LoginViewState.StateIdle
            popBackStack()
        }
    }

    // Check web_view url for access token code or error
    private fun handleUrl(url: String) {
        val uri = Uri.parse(url)
        if (url.contains("code")) {
            val githubCode = uri.getQueryParameter("code") ?: ""
            githubLoginViewModel.getAccessToken(githubCode)
        }
    }

    private val webVieClient = object : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            binding.progressBar.visibility = View.VISIBLE
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            binding.progressBar.visibility = View.GONE
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            if (request!!.url.toString().startsWith(GithubAuthentication.REDIRECT_URI)) {
                handleUrl(request.url.toString())
                return true
            }
            return false
        }
    }
}
