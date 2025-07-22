package com.universidad.mindsparkai.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.universidad.mindsparkai.R
import com.universidad.mindsparkai.databinding.FragmentProfileBinding
import com.universidad.mindsparkai.ui.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        loadUserData()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Agregar listener para logout (asumiendo que existe btn_logout en el layout)
        try {
            val logoutButton = binding.root.findViewById<View>(R.id.btn_logout)
            logoutButton?.setOnClickListener {
                showLogoutConfirmation()
            }
        } catch (e: Exception) {
            // Si no existe el botón en el layout, lo agregamos programáticamente
            addLogoutButton()
        }
    }

    private fun addLogoutButton() {
        // Crear botón de logout programáticamente si no existe en el layout
        val logoutButton = android.widget.Button(requireContext()).apply {
            text = "Cerrar Sesión"
            setBackgroundColor(resources.getColor(R.color.accent_red, null))
            setTextColor(resources.getColor(R.color.white, null))
            setPadding(32, 24, 32, 24)
            setOnClickListener { showLogoutConfirmation() }
        }

        // Buscar el LinearLayout principal y agregar el botón
        val scrollView = binding.root.findViewById<android.widget.ScrollView>(R.id.content_scroll)
            ?: binding.root.findViewById<android.widget.ScrollView>(R.layout.fragment_profile)

        scrollView?.let { scroll ->
            val linearLayout = scroll.getChildAt(0) as? android.widget.LinearLayout
            linearLayout?.addView(logoutButton)
        }
    }

    private fun loadUserData() {
        authViewModel.user.value?.let { user ->
            binding.tvUserName.text = user.name
            binding.tvUserCareer.text = "${user.career} • ${user.university}"
            binding.tvStudyHours.text = "${user.studyHours}h"
            binding.tvAiQueries.text = user.aiQueries.toString()
            binding.tvAvgQuiz.text = "${user.averageQuizScore.toInt()}%"
        } ?: run {
            // Datos por defecto si no hay usuario
            binding.tvUserName.text = "Usuario"
            binding.tvUserCareer.text = "Bienvenido a MindSpark AI"
            binding.tvStudyHours.text = "0h"
            binding.tvAiQueries.text = "0"
            binding.tvAvgQuiz.text = "0%"
        }
    }

    private fun observeViewModel() {
        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Unauthenticated -> {
                    // Usuario deslogueado, navegar a welcome
                    findNavController().navigate(R.id.welcomeFragment)
                }
                else -> {
                    // Usuario autenticado, actualizar datos
                    loadUserData()
                }
            }
        }

        authViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let { loadUserData() }
        }
    }

    private fun showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí, cerrar sesión") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancelar", null)
            .setIcon(R.drawable.ic_profile)
            .show()
    }

    private fun performLogout() {
        try {
            authViewModel.signOut()

            // Mostrar mensaje de confirmación
            android.widget.Toast.makeText(
                requireContext(),
                "Sesión cerrada exitosamente",
                android.widget.Toast.LENGTH_SHORT
            ).show()

            // Navegar a welcome y limpiar el stack de navegación
            findNavController().navigate(
                R.id.welcomeFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build()
            )
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                requireContext(),
                "Error al cerrar sesión",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}