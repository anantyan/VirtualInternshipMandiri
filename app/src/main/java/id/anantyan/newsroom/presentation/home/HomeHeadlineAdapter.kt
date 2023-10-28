package id.anantyan.newsroom.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import coil.load
import coil.size.ViewSizeResolver
import id.anantyan.newsroom.R
import id.anantyan.newsroom.common.toCustomDate
import id.anantyan.newsroom.data.remote.model.ArticlesItem
import id.anantyan.newsroom.databinding.ListItemHeadlineBinding

class HomeHeadlineAdapter : ListAdapter<ArticlesItem, HomeHeadlineAdapter.ArticlesItemViewHolder>(ArticlesItemComparator) {

    private var _onClick: ((position: Int, item: ArticlesItem) -> Unit)? = null

    private object ArticlesItemComparator : DiffUtil.ItemCallback<ArticlesItem>() {
        override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesItemViewHolder {
        return ArticlesItemViewHolder(
            ListItemHeadlineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticlesItemViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }

    inner class ArticlesItemViewHolder(private val binding: ListItemHeadlineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                _onClick?.let {
                    it(bindingAdapterPosition, getItem(bindingAdapterPosition))
                }
            }
        }

        fun bindItem(item: ArticlesItem) {
            binding.imgHeadline.load(item.urlToImage) {
                crossfade(true)
                placeholder(R.drawable.round_image_24)
                error(R.drawable.round_hide_image_24)
                size(ViewSizeResolver(binding.imgHeadline))
            }
            binding.txtTitle.text = item.title
            binding.txtAuthor.text = item.author ?: "-"
            binding.txtPublished.text = item.publishedAt?.toCustomDate() ?: "-"
        }
    }

    fun onClick(listener: (position: Int, item: ArticlesItem) -> Unit) {
        _onClick = listener
    }
}