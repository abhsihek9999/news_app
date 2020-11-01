package com.android.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.newsapp.R
import com.android.newsapp.data.NewsAppRepository
import com.android.newsapp.data.db.NewsDatabase
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    lateinit var viewModel:HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val repository = NewsAppRepository(NewsDatabase(this))

        val viedModelFactory = HomeViewModelFactory(application,repository)

        viewModel = ViewModelProvider(this,viedModelFactory).get(HomeViewModel::class.java)

        bottomNavigationView.setupWithNavController(navigation.findNavController())
    }
}
