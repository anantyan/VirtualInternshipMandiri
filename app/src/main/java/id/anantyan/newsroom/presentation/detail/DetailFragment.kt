package id.anantyan.newsroom.presentation.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import id.anantyan.newsroom.databinding.FragmentDetailBinding

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    private fun bindView() {
        binding.webView.webViewClient = WebViewClient()
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.loadUrl(args.url ?: "")
        binding.webView.webChromeClient = chromeProgressBar

        binding.btnBack.setOnClickListener {
            when {
                binding.webView.canGoBack() -> binding.webView.goBack()
                else -> findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when {
                binding.webView.canGoBack() -> binding.webView.goBack()
                else -> findNavController().navigateUp()
            }
        }
    }

    private val chromeProgressBar = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (newProgress == 100) {
                binding.progressBarHeadline.isVisible = false
            }
            if (newProgress < 100) {
                binding.progressBarHeadline.isVisible = true
                binding.progressBarHeadline.progress = newProgress
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.webView.stopLoading()
        binding.webView.destroy()
        _binding = null
    }
}