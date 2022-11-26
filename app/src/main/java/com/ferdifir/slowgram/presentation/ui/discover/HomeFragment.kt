package com.ferdifir.slowgram.presentation.ui.discover

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ferdifir.slowgram.R
import com.ferdifir.slowgram.data.local.entity.StoryEntity
import com.ferdifir.slowgram.databinding.FragmentHomeBinding
import com.ferdifir.slowgram.presentation.adapter.LoadingStateAdapter
import com.ferdifir.slowgram.presentation.adapter.StoryAdapter
import com.ferdifir.slowgram.presentation.ui.auth.AuthActivity
import com.ferdifir.slowgram.presentation.ui.main.MainActivity
import com.ferdifir.slowgram.presentation.viewmodel.ViewModelFactory
import com.ferdifir.slowgram.utils.Const
import com.ferdifir.slowgram.utils.Helper.animateVisibility
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

@OptIn(ExperimentalPagingApi::class)
@RequiresApi(Build.VERSION_CODES.M)
class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.swipeRefresh.isRefreshing = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        val factory = ViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        val token = (activity as MainActivity).intent.getStringExtra(Const.EXTRA_TOKEN)
        setAdapter()
        if (token != null) {
            getAllStory(token)
            setSwipeRefreshLayout(token)
        }
    }

    private fun setAdapter() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        storyAdapter = StoryAdapter()

        storyAdapter.addLoadStateListener { loadState ->
            if ((loadState.source.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        storyAdapter.itemCount < 1) ||
                loadState.source.refresh is LoadState.Error) {
                binding.apply {
                    tvNotFoundError.animateVisibility(true)
                    ivNotFoundError.animateVisibility(true)
                    rvStories.animateVisibility(false)
                }
            } else {
                binding.apply {
                    tvNotFoundError.animateVisibility(false)
                    ivNotFoundError.animateVisibility(false)
                    rvStories.animateVisibility(true)
                }
            }
            binding.swipeRefresh.isRefreshing = loadState.source.refresh is LoadState.Loading
        }
        try {
            recyclerView = binding.rvStories
            recyclerView.apply {
                adapter = storyAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        storyAdapter.retry()
                    }
                )
                layoutManager = linearLayoutManager
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun setSwipeRefreshLayout(token: String) {
        binding.swipeRefresh.setOnRefreshListener {
            getAllStory(token)
        }
    }

    private fun getAllStory(token: String) {
        viewModel.getStories(token).observe(viewLifecycleOwner) { result ->
            updateRecyclerViewData(result)
        }
    }

    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing = true
        storyAdapter.refresh()
        Timer().schedule(2000) {
            binding.swipeRefresh.isRefreshing = false
            binding.rvStories.smoothScrollToPosition(0)
        }
    }

    private fun updateRecyclerViewData(stories: PagingData<StoryEntity>) {
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        storyAdapter.submitData(lifecycle, stories)
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_main_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.swipeRefresh -> {
                onRefresh()
            }
            R.id.logout -> {
                lifecycleScope.launch {
                    viewModel.userLogout()
                }
                val intent = Intent(activity, AuthActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
        return true
    }

}