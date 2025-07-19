package com.universidad.mindsparkai.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.universidad.mindsparkai.R
import com.universidad.mindsparkai.databinding.FragmentLoginBinding
import com.universidad.mindsparkai.ui.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGoogleSignIn()
        setupListeners()
        setupValidation()
        observeViewModel()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.tvForgotPassword?.setOnClickListener {
            showForgotPasswordDialog()
        }

        binding.tvRegisterLink?.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }

    private fun setupValidation() {
        binding.etEmail.addTextChangedListener { text ->
            viewModel.validateEmail(text.toString())
        }

        binding.etPassword.addTextChangedListener { text ->
            viewModel.validatePassword(text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Authenticated -> {
                    if (state.isNewUser) {
                        // Navegar a configuración inicial
                        findNavController().navigate(R.id.action_login_to_setup)
                    } else {
                        // Navegar al dashboard
                        findNavController().navigate(R.id.action_login_to_dashboard)
                    }
                }
                is AuthViewModel.AuthState.Unauthenticated -> {
                    // Mantener en login
                }
                is AuthViewModel.AuthState.Loading -> {
                    // Mostrar loading
                }
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            updateLoadingState(isLoading)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                showError(it)
                viewModel.clearError()
            }
        }

        viewModel.emailValidation.observe(viewLifecycleOwner) { validation ->
            updateEmailValidation(validation)
        }

        viewModel.passwordValidation.observe(viewLifecycleOwner) { validation ->
            updatePasswordValidation(validation)
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.btnLogin.isEnabled = !isLoading
        binding.btnGoogle.isEnabled = !isLoading

        if (isLoading) {
            binding.btnLogin.text = "Iniciando sesión..."
            binding.progressBar?.visibility = View.VISIBLE
        } else {
            binding.btnLogin.text = getString(R.string.btn_login)
            binding.progressBar?.visibility = View.GONE
        }
    }

    private fun updateEmailValidation(validation: AuthViewModel.ValidationState) {
        if (!validation.isValid && validation.message != null) {
            binding.tilEmail?.error = validation.message
        } else {
            binding.tilEmail?.error = null
        }
    }

    private fun updatePasswordValidation(validation: AuthViewModel.ValidationState) {
        if (!validation.isValid && validation.message != null) {
            binding.tilPassword?.error = validation.message
        } else {
            binding.tilPassword?.error = null
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty()) {
            binding.tilEmail?.error = "Email requerido"
            return
        }

        if (password.isEmpty()) {
            binding.tilPassword?.error = "Contraseña requerida"
            return
        }

        viewModel.loginWithEmail(email, password)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                viewModel.signInWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                showError("Error en Google Sign-In: ${e.message}")
            }
        }
    }

    private fun showForgotPasswordDialog() {
        val emailInput = android.widget.EditText(requireContext()).apply {
            hint = "Ingresa tu email"
            setText(binding.etEmail.text.toString())
        }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Recuperar contraseña")
            .setMessage("Ingresa tu email para recibir instrucciones de recuperación")
            .setView(emailInput)
            .setPositiveButton("Enviar") { _, _ ->
                val email = emailInput.text.toString().trim()
                if (email.isNotEmpty()) {
                    viewModel.sendPasswordReset(email)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}