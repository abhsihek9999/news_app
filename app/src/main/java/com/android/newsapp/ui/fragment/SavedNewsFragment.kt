package com.android.newsapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.newsapp.R
import com.android.newsapp.ui.HomeActivity
import com.android.newsapp.ui.HomeViewModel
import com.android.newsapp.ui.adapter.NewsAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*
import kotlinx.android.synthetic.main.fragment_search_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news){

    lateinit var viewModel: HomeViewModel
    lateinit var newsAdapter : NewsAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as HomeActivity).viewModel
        setupRecyclerView()
        newsAdapter.setOnItemClickListener {

            val bundle = Bundle().apply {
                putSerializable("article",it)
            }

            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        val onItemSwipedCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
              return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList.get(position)

                viewModel.deleteArticles(article)

                Snackbar.make(view,"Item deleted successfully",Snackbar.LENGTH_SHORT).apply {
                    setAction("UNDO"){
                        viewModel.upsert(article)
                    }
                    show()
                }

            }


        }

        ItemTouchHelper(onItemSwipedCallBack).apply {
            attachToRecyclerView(rvSavedNews)
        }

        viewModel.getSavedArticles().observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList(it)
        })

    }

    private fun setupRecyclerView() {

        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = newsAdapter
        }
    }


}