package com.example.trabajomaster

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var googleButton: ImageButton
    private lateinit var registerButton: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginLayout: View
    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TrabajoMaster)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.loginButton)
        googleButton = findViewById(R.id.googleButton)
        registerButton = findViewById(R.id.toRegisterButton)
        email = findViewById(R.id.emailEditTextLogin)
        password = findViewById(R.id.passwordEditText)

        login()
        session()
        toRegister()
    }

    override fun onStart() {
        super.onStart()
        loginLayout = findViewById(R.id.loginLayout)
        loginLayout.visibility = View.VISIBLE
    }

    private fun session(){
        val sharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)
        //val username = sharedPreferences.getString("username", null)
        if (email != null){
            //visibility del layout poner invisible
            loginLayout = findViewById(R.id.loginLayout)
            loginLayout.visibility = View.INVISIBLE
            startApp(email)
        }
    }


    private fun toRegister(){
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(){

        loginButton.setOnClickListener {

            if (email.text.isEmpty() || password.text.isEmpty()) {
                Toast.makeText(this, "There are empty fields", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener{
                        if (it.isSuccessful){
                            startApp(it.result?.user?.email ?: "")
                        } else {
                            showAlert()
                        }
                    }
            }
        }

        googleButton.setOnClickListener {

            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

    }

    private fun startApp(email: String){

        val sharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.apply()

        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
    }

    private fun showAlert(){
        val  builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN){

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener{
                            if (it.isSuccessful){
                                val email = account.email
                                val sharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("username$email", account.givenName)
                                editor.apply()

                                startApp(email ?: "")
                            } else {
                                showAlert()
                            }
                        }
                }
            } catch (e: ApiException){
                showAlert()
            }
        }
    }

}