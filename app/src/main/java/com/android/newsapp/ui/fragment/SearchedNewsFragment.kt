package com.android.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.newsapp.R
import com.android.newsapp.ui.HomeActivity
import com.android.newsapp.ui.HomeViewModel
import com.android.newsapp.ui.Resource
import com.android.newsapp.ui.adapter.NewsAdapter
import com.android.newsapp.utils.Utils
import kotlinx.android.synthetic.main.fragment_breaking_news.*
import kotlinx.android.synthetic.main.fragment_breaking_news.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchedNewsFragment : Fragment(R.layout.fragment_search_news){

    lateinit var viewModel: HomeViewModel
    lateinit var newsAdapter: NewsAdapter
    private  val TAG = "SearchedNewsFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as HomeActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {

            val bundle = Bundle().apply {
                putSerializable("article",it)
            }

            findNavController().navigate(
                R.id.action_searchedNewsFragment_to_articleFragment,
                bundle
            )
        }

         var job : Job? = null
        etSearch.addTextChangedListener{ editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(500)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewModel.getSearchNews(editable.toString())
                    }
                }
            }

        }



        viewModel.searchLiveData.observe(viewLifecycleOwner, Observer {response ->
            when(response){

                is Resource.Success -> {
                    hideProgress()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                        val totalPages = it.totalResults / Utils.TOTAL_PAGES +2
                        isLastPage = viewModel.page == totalPages
                    }

                }

                is Resource.Error -> {
                    hideProgress()

                    response.message?.let {
                        Toast.makeText(activity,it, Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    showProgress()
                }


            }

        })

    }

    fun hideProgress(){
        paginationProgressBar.visibility = View.GONE
        isLoading = false
    }


    fun showProgress(){
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }


    var isLoading:Boolean = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Utils.TOTAL_PAGES
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                viewModel.getSearchNews(etSearch.text.toString())
                isScrolling = false
            }else{

                rvSearchNews.setPadding(0,0,0,0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }


    private fun setupRecyclerView() {

        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = newsAdapter
            addOnScrollListener(scrollListener)
        }
    }

}