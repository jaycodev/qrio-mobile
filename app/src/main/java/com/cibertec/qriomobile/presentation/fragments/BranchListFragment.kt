package com.cibertec.qriomobile.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.cibertec.qriomobile.databinding.FragmentBranchListBinding
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.data.model.BranchDto
import com.cibertec.qriomobile.data.repository.BranchRepository
import com.cibertec.qriomobile.data.remote.NetworkResult
import com.cibertec.qriomobile.presentation.adapters.BranchAdapter

class BranchListFragment : Fragment() {

    private var _binding: FragmentBranchListBinding? = null
    private val binding get() = _binding!!
    
    private val args: BranchListFragmentArgs by navArgs()
    private val branchRepository by lazy { BranchRepository(RetrofitClient.api) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBranchListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        binding.rvBranches.layoutManager = LinearLayoutManager(requireContext())
        
        loadBranches(args.restaurantId)
    }

    private fun loadBranches(restaurantId: Long) {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            when (val result = branchRepository.getBranchesByRestaurant(restaurantId)) {
                is NetworkResult.Success -> {
                    val branches = result.data
                    binding.rvBranches.adapter = BranchAdapter(branches) { branch ->
                        // Si el ID viene nulo (poco probable en DB real), usamos 0L como fallback
                        val bId = branch.id ?: 0L
                        val action = BranchListFragmentDirections
                            .actionBranchListFragmentToCatalogFragment(
                                branchId = bId,
                                tableNumber = 0
                            )
                        findNavController().navigate(action)
                    }
                }
                is NetworkResult.Error -> {
                    Toast.makeText(context, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "No se pudieron cargar sucursales", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
