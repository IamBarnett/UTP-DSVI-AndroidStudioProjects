package com.universidad.mindsparkai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.universidad.mindsparkai.R
import com.universidad.mindsparkai.databinding.FragmentDashboardBinding
import com.universidad.mindsparkai.ui.adapters.RecentActivityAdapter
import com.universidad.mindsparkai.ui.viewmodels.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var recentActivityAdapter: RecentActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        recentActivityAdapter = RecentActivityAdapter { activity ->
            // Handle activity item click
        }

        binding.rvRecentActivity.apply {
            adapter = recentActivityAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }
    }

    private fun setupListeners() {
        binding.cardChatAi.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_chat)
        }

        binding.cardSummary.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_summary)
        }

        binding.cardQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_quiz)
        }

        binding.cardStudyPlan.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_study_plan)
        }

        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_profile)
        }

        binding.ivSearch.setOnClickListener {
            // Implement search functionality
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvGreeting.text = "Buenos días, ${it.name}"
                binding.tvSubjectsCount.text = it.subjects.size.toString()
                binding.tvProgress.text = "${it.averageQuizScore.toInt()}%"
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading indicator if needed
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Show error message if needed
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}