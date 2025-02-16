package hbv601g.learningsquare.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import hbv601g.learningsquare.R
import hbv601g.learningsquare.databinding.FragmentLoginBinding
import hbv601g.learningsquare.ui.login.LoginViewModel
import hbv601g.learningsquare.ui.login.LoginViewModelFactory

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        binding.loginButton.setOnClickListener {
            val username = binding.inputUsername.text.toString()
            val password = binding.inputPassword.text.toString()

            loginViewModel.login(username, password)
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner) { loginResult ->
            loginResult?.let {
                if (it.error != null) {
                    Toast.makeText(context, getString(it.error), Toast.LENGTH_SHORT).show()
                }

                if (it.success != null) {
                    Toast.makeText(context, "Welcome ${it.success.displayName}", Toast.LENGTH_LONG).show()

                    // After login success, navigate to the next screen
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
