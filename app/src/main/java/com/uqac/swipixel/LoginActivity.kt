package com.uqac.swipixel


import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var tvRegister: TextView
    private lateinit var etLoginEmail: EditText
    private lateinit var etLoginPassword: EditText
    private lateinit var etLoginConfirmPassword: EditText
    private var isPasswordVisible: Boolean = false

    private lateinit var btSignIn: SignInButton
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        } else {
            // No user is signed in
        }

        tvRegister = findViewById(R.id.tv_register)

        tvRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        val btnLogin = findViewById<Button>(R.id.btn_login)
        etLoginEmail = findViewById(R.id.et_login_email)
        etLoginPassword = findViewById(R.id.et_login_password)
        etLoginConfirmPassword = findViewById(R.id.et_login_confirm_password)

        val passwordToggle = etLoginPassword
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordToggle.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                passwordToggle.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
        }


        btnLogin.setOnClickListener {
            when {
                TextUtils.isEmpty(etLoginEmail.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(etLoginPassword.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                etLoginPassword.text.toString() != etLoginConfirmPassword.text.toString() -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Passwords do not match",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val email: String = etLoginEmail.text.toString().trim { it <= ' ' }
                    val password: String = etLoginPassword.text.toString().trim { it <= ' ' }

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                Toast.makeText(
                                    this@LoginActivity,
                                    "You are logged successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent =
                                    Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra(
                                    "user_id",
                                    FirebaseAuth.getInstance().currentUser!!.uid
                                )
                                intent.putExtra("email_id", email)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }

        btSignIn = findViewById(R.id.googleSignInBtn)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1011379569359-s3fqcampg8gddln38rdptq0pjujderni.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        btSignIn.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        googleSignInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        firebaseAuthWithGoogle(account!!.idToken!!)
                    } catch (e: ApiException) {
                        Log.w(TAG, "Google sign in failed", e)
                    }
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}




