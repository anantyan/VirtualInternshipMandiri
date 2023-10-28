package id.anantyan.newsroom.presentation.load_more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import id.anantyan.newsroom.R
import id.anantyan.newsroom.common.calculateSpanCount
import id.anantyan.newsroom.databinding.FragmentLoadMoreBinding
import id.anantyan.newsroom.presentation.home.HomeFragmentDirections
import id.anantyan.newsroom.presentation.home.HomeSearchLoadStateAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LoadMoreFragment : BottomSheetDialogFragment() {

    private val viewModel: LoadMoreViewModel by viewModels()
    private var _binding: FragmentLoadMoreBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var loadMoreAdapter: LoadMoreAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObserver()
        bindView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindView() {
        setupLoadMoreAdapter()
    }

    private fun bindObserver() {
        setupLoadMoreObserver()
    }

    private fun setupLoadMoreAdapter() {
        val loadStateHeader = LoadMoreLoadStateAdapter { loadMoreAdapter.retry() }
        val loadStateFooter = LoadMoreLoadStateAdapter { loadMoreAdapter.retry() }
        val concatAdapter = loadMoreAdapter.withLoadStateHeaderAndFooter(header = loadStateHeader, footer = loadStateFooter)

        binding.rvLoadMore.setHasFixedSize(true)
        binding.rvLoadMore.layoutManager = StaggeredGridLayoutManager(requireActivity().windowManager.calculateSpanCount(), RecyclerView.VERTICAL)
        binding.rvLoadMore.itemAnimator = DefaultItemAnimator()
        binding.rvLoadMore.adapter = concatAdapter

        loadMoreAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        loadMoreAdapter.addLoadStateListener { loadState ->
            binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
            binding.rvLoadMore.isVisible = loadState.source.refresh is LoadState.NotLoading
        }
        loadMoreAdapter.onClick { _, item ->
            val destination = LoadMoreFragmentDirections.actionLoadMoreFragmentToDetailFragment(item.url)
            findNavController().navigate(destination)
        }
    }

    private fun setupLoadMoreObserver() {
        viewModel.getLoadMore.onEach {
            loadMoreAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }
}