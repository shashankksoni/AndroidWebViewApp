package com.example.shipglobalv3

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle Android back button inside WebView
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (::webView.isInitialized && webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })

        setContent {
            MaterialTheme {
                WebViewScreen("https://v2.app.shipglobal.in/auth/login")
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun WebViewScreen(url: String) {
        var hasError by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            if (hasError) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Something went wrong.", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        hasError = false
                        webView.reload()
                    }) {
                        Text("Retry")
                    }
                }
            } else {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            settings.useWideViewPort = true
                            settings.loadWithOverviewMode = true

                            isVerticalScrollBarEnabled = true
                            overScrollMode = WebView.OVER_SCROLL_IF_CONTENT_SCROLLS

                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView,
                                    request: WebResourceRequest
                                ): Boolean {
                                    view.loadUrl(request.url.toString())
                                    return true
                                }

                                override fun onReceivedError(
                                    view: WebView,
                                    request: WebResourceRequest,
                                    error: WebResourceError
                                ) {
                                    hasError = true
                                }
                            }

                            // Inject viewport + Wallet Activity panel fix
                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView, progress: Int) {
                                    if (progress >= 80) {
                                        view.evaluateJavascript(
                                            """
                                            (function() {
                                                var m = document.querySelector('meta[name=viewport]');
                                                if (!m) {
                                                    m = document.createElement('meta');
                                                    m.name = 'viewport';
                                                    m.content = 'width=device-width, initial-scale=1.0';
                                                    document.head.appendChild(m);
                                                } else {
                                                    m.content = 'width=device-width, initial-scale=1.0';
                                                }

                                                var style = document.createElement('style');
                                                style.innerHTML = `
                                                    .bg-card {
                                                        width: 100% !important;
                                                        max-width: 100% !important;
                                                        min-width: 100% !important;
                                                        flex: 1 1 100% !important;
                                                    }
                                                `;
                                                document.head.appendChild(style);
                                            })();
                                            """.trimIndent(), null
                                        )
                                    }
                                }
                            }

                            loadUrl(url)
                            webView = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}




//    @SuppressLint("SetJavaScriptEnabled")
//    @Composable
//    fun WebViewScreen(url: String) {
//        var hasError by remember { mutableStateOf(false) }
//
//        Box(modifier = Modifier.fillMaxSize()) {
//            if (hasError) {
//                Column(
//                    modifier = Modifier.align(Alignment.Center),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text("Something went wrong.", color = MaterialTheme.colorScheme.error)
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Button(onClick = {
//                        hasError = false
//                        webView.reload()
//                    }) {
//                        Text("Retry")
//                    }
//                }
//            } else {
//                AndroidView(
//                    factory = { ctx ->
//                        WebView(ctx).apply {
//                            // Core WebView settings
//                            settings.javaScriptEnabled = true
//                            settings.domStorageEnabled = true
//                            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//
//                            // Mobile-friendly scaling
//                            settings.useWideViewPort = true
//                            settings.loadWithOverviewMode = true
//
//                            // Let WebView reflow content for small screens
//                            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING

//
//                            // Start at 100% zoom
//                            setInitialScale(100)
//
//                            // Scrollbars
//                            isVerticalScrollBarEnabled = true
//                            overScrollMode = WebView.OVER_SCROLL_IF_CONTENT_SCROLLS
//
//                            webViewClient = object : WebViewClient() {
//                                override fun onReceivedError(
//                                    view: WebView,
//                                    request: WebResourceRequest,
//                                    error: WebResourceError
//                                ) {
//                                    hasError = true
//                                }
//                            }
//
//                            loadUrl(url)
//                            webView = this
//                        }
//                    },
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//        }
//    }
//}

//    @SuppressLint("SetJavaScriptEnabled")
//    @Composable
//    fun WebViewScreen(url: String) {
//        var hasError by remember { mutableStateOf(false) }
//
//        Box(modifier = Modifier.fillMaxSize()) {
//            if (hasError) {
//                Column(
//                    modifier = Modifier.align(Alignment.Center),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text("Something went wrong.", color = MaterialTheme.colorScheme.error)
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Button(onClick = {
//                        hasError = false
//                        webView.reload()
//                    }) {
//                        Text("Retry")
//                    }
//                }
//            } else {
//                AndroidView(
//                    factory = { ctx ->
//                        WebView(ctx).apply {
//                            // Core settings
//                            settings.javaScriptEnabled = true
//                            settings.domStorageEnabled = true
//                            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//                            settings.loadWithOverviewMode = true
//                            settings.useWideViewPort = true
//
//                            // Pretend to be a mobile Chrome browser
//                            settings.userAgentString =
//                                "Mozilla/5.0 (Linux; Android 10; Mobile) " +
//                                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
//                                        "Chrome/120.0.0.0 Mobile Safari/537.36"
//
//                            // Enable vertical scrolling
//                            isVerticalScrollBarEnabled = true
//                            overScrollMode = WebView.OVER_SCROLL_IF_CONTENT_SCROLLS
//
//                            webViewClient = object : WebViewClient() {
//                                override fun shouldOverrideUrlLoading(
//                                    view: WebView,
//                                    request: WebResourceRequest
//                                ): Boolean {
//                                    view.loadUrl(request.url.toString())
//                                    return true
//                                }
//                                override fun onReceivedError(
//                                    view: WebView,
//                                    request: WebResourceRequest,
//                                    error: WebResourceError
//                                ) {
//                                    hasError = true
//                                }
//                            }
//
//                            var injected = false
//                            webChromeClient = object : WebChromeClient() {
//                                override fun onProgressChanged(view: WebView, progress: Int) {
//                                    if (!injected && progress >= 80) {
//                                        injected = true
//                                        view.evaluateJavascript(
//                                            """
//                                            (function() {
//                                              var m = document.querySelector('meta[name="viewport"]');
//                                              if (!m) {
//                                                m = document.createElement('meta');
//                                                m.name = 'viewport';
//                                                m.content = 'width=device-width, initial-scale=1.0';
//                                                document.head.appendChild(m);
//                                              } else {
//                                                m.content = 'width=device-width, initial-scale=1.0';
//                                              }
//                                            })();
//                                            """.trimIndent(), null
//                                        )
//                                    }
//                                }
//                            }
//
//                            loadUrl(url)
//                            webView = this
//                        }
//                    },
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//        }
//    }
//}

//    @Composable
//    fun WebViewScreen(url: String) {
//        val context = LocalContext.current
//        var hasError by remember { mutableStateOf(false) }
//
//        Box(modifier = Modifier.fillMaxSize()) {
//            if (hasError) {
//                // Error screen with a proper retry Button
//                Column(
//                    modifier = Modifier.align(Alignment.Center),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "Something went wrong.",
//                        color = MaterialTheme.colorScheme.error
//                    )
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Button(onClick = {
//                        hasError = false
//                        webView.reload()
//                    }) {
//                        Text("Retry")
//                    }
//                }
//            } else {
//                AndroidView(
//                    factory = {
//                        WebView(context).apply {
//                            settings.javaScriptEnabled = true
//                            settings.domStorageEnabled = true
//                            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//                            settings.loadWithOverviewMode = true
//                            settings.useWideViewPort = true
//                            isVerticalScrollBarEnabled = true
//                            overScrollMode = WebView.OVER_SCROLL_IF_CONTENT_SCROLLS
//
//                            webViewClient = object : WebViewClient() {
//                                override fun onPageFinished(view: WebView?, url: String?) {
//                                    // Inject a viewport meta tag so mobile layout scales correctly
//                                    view?.evaluateJavascript(
//                                        """
//                                        (function() {
//                                          var m = document.querySelector('meta[name="viewport"]');
//                                          if (!m) {
//                                            m = document.createElement('meta');
//                                            m.name = 'viewport';
//                                            m.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0';
//                                            document.head.appendChild(m);
//                                          } else {
//                                            m.setAttribute('content','width=device-width, initial-scale=1.0, maximum-scale=1.0');
//                                          }
//                                        })();
//                                        """.trimIndent(), null
//                                    )
//                                }
//
//                                override fun onReceivedError(
//                                    view: WebView,
//                                    request: WebResourceRequest,
//                                    error: WebResourceError
//                                ) {
//                                    hasError = true
//                                }
//                            }
//
//                            loadUrl(url)
//                            webView = this
//                        }
//                    },
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//        }
//    }
//}

//    @Composable
//    fun WebViewScreen(url: String) {
//        AndroidView(factory = { context ->
//            WebView(context).apply {
//                settings.javaScriptEnabled = true
//                settings.domStorageEnabled = true // Required for login forms sometimes
//                webViewClient = object : WebViewClient() {
//                    override fun onPageFinished(view: WebView?, url: String?) {
//                        super.onPageFinished(view, url)
//                        println("✅ Page finished loading: $url")
//                    }
//
//                    override fun onReceivedError(
//                        view: WebView,
//                        errorCode: Int,
//                        description: String?,
//                        failingUrl: String?
//                    ) {
//                        super.onReceivedError(view, errorCode, description, failingUrl)
//                        println("❌ Error loading page: $description at $failingUrl")
//                    }
//                }
//                webView = this
//                loadUrl(url)
//            }
//        })
//    }
//}

//    @Composable
//    fun WebViewScreen(url: String) {
//        AndroidView(factory = { context ->
//            WebView(context).apply {
//                webViewClient = WebViewClient()
//                settings.javaScriptEnabled = true  // Required for login pages and modern JS-based UIs
//                webView = this
//                loadUrl(url)
//            }
//        })
//    }
//}
