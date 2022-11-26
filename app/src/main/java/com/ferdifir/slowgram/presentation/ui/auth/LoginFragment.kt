package com.ferdifir.slowgram.presentation.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import com.ferdifir.slowgram.R
import com.ferdifir.slowgram.databinding.FragmentLoginBinding
import com.ferdifir.slowgram.presentation.ui.main.MainActivity
import com.ferdifir.slowgram.presentation.viewmodel.ViewModelFactory
import com.ferdifir.slowgram.utils.Const.EXTRA_TOKEN
import com.ferdifir.slowgram.utils.Result
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setLoginButtonEnable() {
        val emailState = binding.edLoginEmail.text != null && binding.edLoginEmail.text.toString().isNotEmpty()
        val passwdState = binding.edLoginEmail.text != null && binding.edLoginPassword.text.toString().isNotEmpty()
        binding.btnLogin.isEnabled = emailState && passwdState
    }

    private fun setAnimation() {
        val email = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(email, password)
        }

        AnimatorSet().apply {
            playSequentially(together, button)
            start()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAnimation()
        setLoginButtonEnable()
        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setLoginButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })
        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setLoginButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })
        setActions()
    }

    private fun setActions() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = binding.edLoginEmail.text.toString()
                val password = binding.edLoginPassword.text.toString()
                handleLogin(email, password)
            }
            btnRegister.setOnClickListener {
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.auth_container, RegisterFragment(), RegisterFragment::class.java.simpleName)
                    addSharedElement(binding.edLoginEmail, "email")
                    addSharedElement(binding.edLoginPassword, "password")
                    addSharedElement(binding.btnLogin, "button")
                    commit()
                }
            }
        }
    }

    private fun handleLogin(email: String, password: String) {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: AuthViewModel by viewModels { factory }
        viewModel.userLogin(email, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when(result) {
                    is Result.Success -> {
                        binding.progressCircular.visibility = View.GONE
                        Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.putExtra(EXTRA_TOKEN, result.data.loginResult.token)
                        startActivity(intent)
                    }
                    is Result.Error -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.login_error_message),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is Result.Loading -> {

                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
