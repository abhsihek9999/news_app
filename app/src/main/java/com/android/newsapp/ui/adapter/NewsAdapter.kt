package com.android.newsapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.newsapp.R
import com.android.newsapp.data.db.entity.Article
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_article_preview.view.*

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    private  val TAG = "NewsAdapter"
    private val differenceCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {

            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
          return oldItem == newItem
        }

    }

     val differ = AsyncListDiffer(this,differenceCallback)

    class ArticleViewHolder(itemVIew: View):RecyclerView.ViewHolder(itemVIew) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {

        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
       return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        val article = differ.currentList[position]

        holder.itemView.apply {

            Glide.with(this)
                .load(article.urlToImage)
                .into(ivArticleImage)

            tvPublishedAt.text = article.publishedAt
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvSource.text = article.source.name

            setOnClickListener {
                onItemClickListener?.let { it(article) }
            }
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listner : (Article) -> Unit ){
        Log.d(TAG, "setOnItemClickListener: ")
        onItemClickListener = listner
    }

}