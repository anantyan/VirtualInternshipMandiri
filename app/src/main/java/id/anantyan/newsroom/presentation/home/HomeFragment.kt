package id.anantyan.newsroom.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.anantyan.newsroom.common.UIState
import id.anantyan.newsroom.common.calculateSpanCount
import id.anantyan.newsroom.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var headlineAdapter: HomeHeadlineAdapter
    @Inject
    lateinit var searchAdapter: HomeSearchAdapter
    @Inject
    lateinit var allNewsAdapter: HomeAllNewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(setupOnBackPressed())
        bindObserver()
        bindView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindObserver() {
        setupHeadlineObserver()
        setupAllNewsObserver()
        setupSearchObserver()
    }

    private fun bindView() {
        setupHeadlineAdapter()
        setupSearchAdapter()
        setupAllNewsAdapter()
    }

    private fun setupOnBackPressed() = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.searchView.isShowing) {
                binding.searchView.hide()
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun setupHeadlineObserver() {
        viewModel.getHeadline.onEach { state ->
            when (state) {
                is UIState.Loading -> {
                    binding.progressBarHeadline.isVisible = true
                    binding.nestedScroll.isVisible = false
                    headlineAdapter.submitList(emptyList())
                }
                is UIState.Success -> {
                    binding.progressBarHeadline.isVisible = false
                    binding.nestedScroll.isVisible = true
                    headlineAdapter.submitList(state.data)
                }
                is UIState.Error -> {
                    binding.progressBarHeadline.isVisible = false
                    binding.nestedScroll.isVisible = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun setupAllNewsObserver() {
        viewModel.getAllNews.onEach { state ->
            when (state) {
                is UIState.Loading -> {
                    binding.progressBarAllNews.isVisible = true
                    binding.btnLoadMore.isVisible = false
                    allNewsAdapter.submitList(emptyList())
                }
                is UIState.Success -> {
                    binding.progressBarAllNews.isVisible = false
                    binding.btnLoadMore.isVisible = true
                    allNewsAdapter.submitList(state.data)
                }
                is UIState.Error -> {
                    binding.progressBarAllNews.isVisible = false
                    binding.btnLoadMore.isVisible = false
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun setupSearchObserver() {
        viewModel.getSearchNews.onEach { pagingData ->
            searchAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun setupSearchAdapter() {
        binding.searchBar.isDefaultScrollFlagsEnabled = false
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            viewModel.getSearchNews(binding.searchView.text.toString())
            false
        }

        val loadStateHeader = HomeSearchLoadStateAdapter { searchAdapter.retry() }
        val loadStateFooter = HomeSearchLoadStateAdapter { searchAdapter.retry() }
        val concatAdapter = searchAdapter.withLoadStateHeaderAndFooter(header = loadStateHeader, footer = loadStateFooter)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(requireActivity().windowManager.calculateSpanCount(), RecyclerView.VERTICAL)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = concatAdapter
        binding.recyclerView.isNestedScrollingEnabled = false

        searchAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        searchAdapter.addLoadStateListener { loadState ->
            binding.progressSearch.isVisible = loadState.source.refresh is LoadState.Loading
            binding.recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
        }
        searchAdapter.onClick { _, item ->
            val destination = HomeFragmentDirections.actionHomeFragmentToDetailFragment(item.url)
            findNavController().navigate(destination)
        }
    }

    private fun setupHeadlineAdapter() {
        binding.rvHeadline.setHasFixedSize(true)
        binding.rvHeadline.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvHeadline.itemAnimator = DefaultItemAnimator()
        binding.rvHeadline.adapter = headlineAdapter
        binding.rvHeadline.isNestedScrollingEnabled = true

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvHeadline)

        headlineAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        headlineAdapter.onClick { _, item ->
            val destination = HomeFragmentDirections.actionHomeFragmentToDetailFragment(item.url)
            findNavController().navigate(destination)
        }
    }

    private fun setupAllNewsAdapter() {
        binding.btnLoadMore.setOnClickListener {
            val destination = HomeFragmentDirections.actionHomeFragmentToLoadMoreFragment()
            findNavController().navigate(destination)
        }

        binding.rvAll.setHasFixedSize(true)
        binding.rvAll.layoutManager = StaggeredGridLayoutManager(requireActivity().windowManager.calculateSpanCount(), RecyclerView.VERTICAL)
        binding.rvAll.itemAnimator = DefaultItemAnimator()
        binding.rvAll.adapter = allNewsAdapter
        binding.rvAll.isNestedScrollingEnabled = false

        allNewsAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        allNewsAdapter.onClick { _, item ->
            val destination = HomeFragmentDirections.actionHomeFragmentToDetailFragment(item.url)
            findNavController().navigate(destination)
        }
    }
}