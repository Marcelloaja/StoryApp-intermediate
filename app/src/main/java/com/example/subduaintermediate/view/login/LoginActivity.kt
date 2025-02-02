package com.example.subduaintermediate.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.subduaintermediate.call.ResultCall
import com.example.subduaintermediate.data.preference.UserModel
import com.example.subduaintermediate.databinding.ActivityLoginBinding
import com.example.subduaintermediate.view.ViewModelFactory
import com.example.subduaintermediate.view.main.MainActivity
import com.example.subduaintermediate.view.signup.PasswordCustomField
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var passwordCustomField: PasswordCustomField
    private lateinit var emailCustomField: EmailCustomField
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupAction()
        playAnimation()

        emailCustomField = binding.emailEditText
        passwordCustomField = binding.passwordEditText
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.login(email, password).observe(this) { user ->
                when (user) {
                    is ResultCall.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        AlertDialog.Builder(this).apply {
                            setTitle("Success")
                            setMessage("Successfully Login")
                            setPositiveButton("Go") { _, _ ->
                                saveSession(
                                    UserModel(
                                        user.data.loginResult.token,
                                        user.data.loginResult.name,
                                        user.data.loginResult.userId,
                                        true
                                    )
                                )
                            }
                            Log.e("login", user.data.loginResult.token)
                            create()
                            show()
                        }
                    }

                    is ResultCall.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ResultCall.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        val error = user.error
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }

    private fun saveSession(session: UserModel) {
        lifecycleScope.launch {
            viewModel.saveSession(session)
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            ViewModelFactory.clearInstance()
            startActivity(intent)
        }
    }

}