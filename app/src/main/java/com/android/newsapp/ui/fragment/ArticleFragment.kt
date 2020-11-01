package com.android.newsapp.ui.fragment

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.android.newsapp.R
import com.android.newsapp.ui.HomeActivity
import com.android.newsapp.ui.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article){

    lateinit var viewModel: HomeViewModel
    val args:ArticleFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as HomeActivity).viewModel
        val article = args.article
        webView.apply {
            webChromeClient = WebChromeClient()
            loadUrl(article.url)
        }

        fab.setOnClickListener(View.OnClickListener {
            viewModel.upsert(article)
            Snackbar.make(view,"Article saved successfully",Snackbar.LENGTH_SHORT).show()
        })

    }

}