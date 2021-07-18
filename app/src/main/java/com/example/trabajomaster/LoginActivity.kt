package com.example.trabajomaster

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_TrabajoMaster)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.toRegisterButton)
        email = findViewById(R.id.emailEditTextLogin)
        password = findViewById(R.id.passwordEditText)

        login()
        toRegister()
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
    }

    private fun startApp(email: String){
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
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

}