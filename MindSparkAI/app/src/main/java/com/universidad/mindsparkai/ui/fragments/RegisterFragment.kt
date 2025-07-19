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
import com.universidad.mindsparkai.databinding.FragmentRegisterBinding
import com.universidad.mindsparkai.ui.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
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
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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

        binding.btnRegister.setOnClickListener {
            attemptRegister()
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }

        binding.tvLoginLink?.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }

    private fun setupValidation() {
        binding.etName.addTextChangedListener { text ->
            viewModel.validateName(text.toString())
        }

        binding.etEmail.addTextChangedListener { text ->
            viewModel.validateEmail(text.toString())
        }

        binding.etPassword.addTextChangedListener { text ->
            viewModel.validatePassword(text.toString())
        }

        binding.etConfirmPassword?.addTextChangedListener { text ->
            validatePasswordConfirmation(text.toString())
        }
    }

    private fun validatePasswordConfirmation(confirmPassword: String) {
        val password = binding.etPassword.text.toString()
        if (confirmPassword.isNotEmpty() && password != confirmPassword) {
            binding.tilConfirmPassword?.error = getString(R.string.error_passwords_not_match)
        } else {
            binding.tilConfirmPassword?.error = null
        }
    }

    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Authenticated -> {
                    if (state.isNewUser) {
                        // Navegar a configuración inicial
                        showWelcomeMessage()
                        findNavController().navigate(R.id.action_register_to_dashboard)
                    } else {
                        // Usuario ya existente con Google
                        findNavController().navigate(R.id.action_register_to_dashboard)
                    }
                }
                is AuthViewModel.AuthState.Unauthenticated -> {
                    // Mantener en registro
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

        viewModel.nameValidation.observe(viewLifecycleOwner) { validation ->
            updateNameValidation(validation)
        }

        viewModel.emailValidation.observe(viewLifecycleOwner) { validation ->
            updateEmailValidation(validation)
        }

        viewModel.passwordValidation.observe(viewLifecycleOwner) { validation ->
            updatePasswordValidation(validation)
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.btnRegister.isEnabled = !isLoading
        binding.btnGoogle.isEnabled = !isLoading

        if (isLoading) {
            binding.btnRegister.text = "Creando cuenta..."
            binding.progressBar?.visibility = View.VISIBLE
        } else {
            binding.btnRegister.text = getString(R.string.btn_register)
            binding.progressBar?.visibility = View.GONE
        }
    }

    private fun updateNameValidation(validation: AuthViewModel.ValidationState) {
        if (!validation.isValid && validation.message != null) {
            binding.tilName?.error = validation.message
        } else {
            binding.tilName?.error = null
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

        // Mostrar indicador de fortaleza
        binding.tvPasswordStrength?.text = validation.message
        binding.tvPasswordStrength?.visibility = if (validation.message != null) View.VISIBLE else View.GONE
    }

    private fun attemptRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword?.text.toString() ?: password
        val university = binding.etUniversity?.text.toString().trim() ?: ""
        val career = binding.etCareer?.text.toString().trim() ?: ""

        // Validaciones
        if (name.isEmpty()) {
            binding.tilName?.error = "Nombre requerido"
            return
        }

        if (email.isEmpty()) {
            binding.tilEmail?.error = "Email requerido"
            return
        }

        if (password.isEmpty()) {
            binding.tilPassword?.error = "Contraseña requerida"
            return
        }

        if (password != confirmPassword) {
            binding.tilConfirmPassword?.error = getString(R.string.error_passwords_not_match)
            return
        }

        viewModel.registerWithEmail(email, password, name, university, career)
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

    private fun showWelcomeMessage() {
        Toast.makeText(
            requireContext(),
            "¡Bienvenido a MindSpark AI! 🎉",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}