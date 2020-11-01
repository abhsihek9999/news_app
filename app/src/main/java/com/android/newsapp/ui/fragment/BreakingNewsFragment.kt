package com.android.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.newsapp.R
import com.android.newsapp.ui.HomeActivity
import com.android.newsapp.ui.HomeViewModel
import com.android.newsapp.ui.Resource
import com.android.newsapp.ui.adapter.NewsAdapter
import com.android.newsapp.utils.Utils.Companion.TOTAL_PAGES
import kotlinx.android.synthetic.main.fragment_breaking_news.*

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news){

    lateinit var viewModel: HomeViewModel

    lateinit var newsAdapter: NewsAdapter
    private  val TAG = "BreakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as HomeActivity).viewModel
        setupRecyclerView()


        viewModel.newsLiveData.observe(viewLifecycleOwner, Observer {response ->

            when(response){

                is Resource.Success -> {
                    hideProgress()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages = it.totalResults / TOTAL_PAGES +2
                        isLastPage = viewModel.page == totalPages
                    }

                }

                is Resource.Error -> {
                    hideProgress()

                    response.message?.let {
                        Toast.makeText(activity,it,Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Loading -> {
                    showProgress()
                }


            }

        })

        newsAdapter.setOnItemClickListener {

            val bundle = Bundle().apply {
                putSerializable("article",it)
            }

            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }


    }

    fun hideProgress(){
       paginationProgressBar.visibility = GONE
        isLoading = false
    }


    fun showProgress(){
        paginationProgressBar.visibility = VISIBLE
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
            val isTotalMoreThanVisible = totalItemCount >= TOTAL_PAGES
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                viewModel.getBreakingNews("US")
                isScrolling = false
            }else{

                rvBreakingNews.setPadding(0,0,0,0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }


    private fun setupRecyclerView(){

        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = newsAdapter
            addOnScrollListener(scrollListener)
        }

    }
}