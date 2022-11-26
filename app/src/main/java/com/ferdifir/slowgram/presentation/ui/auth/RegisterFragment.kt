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
import com.ferdifir.slowgram.databinding.FragmentRegisterBinding
import com.ferdifir.slowgram.presentation.ui.main.MainActivity
import com.ferdifir.slowgram.presentation.viewmodel.ViewModelFactory
import com.ferdifir.slowgram.utils.Const
import com.ferdifir.slowgram.utils.Result

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
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
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setAnimation()
        setRegisterButtonEnable()
        binding.edRegisterName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setRegisterButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })
        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setRegisterButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })
        binding.edRegisterPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setRegisterButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })
        return binding.root
    }

    private fun setRegisterButtonEnable() {
        val nameState = binding.edRegisterName.text != null && binding.edRegisterName.text.toString().isNotEmpty()
        val emailState = binding.edRegisterEmail.text != null && binding.edRegisterEmail.text.toString().isNotEmpty()
        val passwdState = binding.edRegisterPassword.text != null && binding.edRegisterPassword.text.toString().isNotEmpty()
        binding.btnRegister.isEnabled = nameState && emailState && passwdState
    }

    private fun setAnimation() {
        val name = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(name, email, password)
        }

        AnimatorSet().apply {
            playSequentially(together, button)
            start()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActions()
    }

    private fun setActions() {
        binding.apply {
            btnRegister.setOnClickListener {
                val name = binding.edRegisterName.text.toString()
                val email = binding.edRegisterEmail.text.toString()
                val password = binding.edRegisterPassword.text.toString()
                handleRegister(name, email, password)
            }
            btnLogin.setOnClickListener {
                backToLoginPage()
            }
        }
    }

    private fun backToLoginPage() {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.auth_container, LoginFragment(), LoginFragment::class.java.simpleName)
            addSharedElement(binding.edRegisterEmail, "email")
            addSharedElement(binding.edRegisterPassword, "password")
            addSharedElement(binding.btnRegister, "button")
            commit()
        }
    }

    private fun handleRegister(name: String, email: String, password: String) {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: AuthViewModel by viewModels { factory }
        viewModel.userRegister(name, email, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when(result) {
                    is Result.Success -> {
                        viewModel.userLogin(email, password).observe(viewLifecycleOwner) { resultLogin ->
                            when(resultLogin) {
                                is Result.Success -> {
                                    Toast.makeText(context, getString(R.string.toast_login_success), Toast.LENGTH_SHORT).show()
                                    val intent = Intent(activity, MainActivity::class.java)
                                    intent.putExtra(Const.EXTRA_TOKEN, resultLogin.data.loginResult.token)
                                    startActivity(intent)
                                }
                                is Result.Error -> {
                                    Toast.makeText(context, "Please login manually", Toast.LENGTH_SHORT).show()
                                    backToLoginPage()
                                }
                                is Result.Loading -> {

                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
