package com.oss.diaring.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.oss.diaring.R
import com.oss.diaring.data.sharedpreference.SharedPrefManagerImpl
import com.oss.diaring.databinding.ActivityLoginBinding
import com.oss.diaring.presentation.base.BaseActivity
import com.oss.diaring.presentation.main.MainActivity
import com.oss.diaring.presentation.signup.SignUpActivity
import com.oss.diaring.util.Constants

class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private var auth: FirebaseAuth? = null

    private lateinit var nickname: String
    private lateinit var sharedPreferences: SharedPrefManagerImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        sharedPreferences = SharedPrefManagerImpl(this)

        getIntentExtra()

        bindViews()
    }

    private fun getIntentExtra() {
        nickname = intent.getStringExtra("user_nickname").toString()
    }

    private fun bindViews() {
        // 회원가입 버튼 -> singup액티비티로 이동
        binding.btnSignupButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // 로그인 버튼
        binding.btnLoginButton.setOnClickListener {
            val userId = binding.etUsername.text.trim().toString()
            val userPassword = binding.etPassword.text.trim().toString()

            signIn(userId, userPassword)
        }
    }

    //     로그아웃하지 않을 시 자동 로그인 , 회원가입시 바로 로그인 됨
    public override fun onStart() {
        super.onStart()
        val uid = sharedPreferences.getUserId(Constants.FIREBASE_UID)

        if (uid != "") {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    // 로그인
    private fun signIn(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this, "로그인에 성공 하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        moveMainPage(email, nickname, auth?.currentUser)
                    } else {
                        Toast.makeText(
                            this, "로그인에 실패 하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    // 유저정보 넘겨주고 메인 액티비티 호출, 최초로그인
    private fun moveMainPage(email: String, nickname: String, user: FirebaseUser?) {
        if (user != null) {
            sharedPreferences.setEmail(Constants.USER_EMAIL, email)
            sharedPreferences.setNickName(Constants.USER_NICKNAME, nickname)
            sharedPreferences.setUserId(Constants.FIREBASE_UID, user.uid)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}