package id.anantyan.newsroom.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import id.anantyan.newsroom.databinding.ListItemLostOrFailedBinding

class HomeSearchLoadStateAdapter(
    private val onClick: () -> Unit
) : LoadStateAdapter<RecyclerView.ViewHolder>() {
    inner class ViewHolder(private val binding: ListItemLostOrFailedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.txtRetry.isVisible = loadState is LoadState.Error
            binding.btnRetry.isVisible = loadState is LoadState.Error
            /*binding.txtRetry.text = (loadState as? LoadState.Error)?.error?.message.toString()*/
            binding.btnRetry.setOnClickListener {
                onClick.invoke()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, loadState: LoadState) {
        holder as ViewHolder
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): RecyclerView.ViewHolder {
        return ViewHolder(
            ListItemLostOrFailedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}