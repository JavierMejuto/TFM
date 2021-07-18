package com.example.trabajomaster

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var backLoginButton: Button
    private lateinit var emailText: EditText
    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var repeatPasswordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerButton = findViewById(R.id.registerButton)
        backLoginButton = findViewById(R.id.backLoginButton)
        emailText = findViewById(R.id.emailEditText)
        usernameText = findViewById(R.id.inputUsername)
        passwordText = findViewById(R.id.inputPassword)
        repeatPasswordText = findViewById(R.id.inputRepeatPassword)

        register()
        toLogin()
    }

    private fun toLogin(){
        backLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun register(){

        // Guardar datos en base de datos o Shared Preferences

        registerButton.setOnClickListener {
            if (emailText.text.isEmpty() || usernameText.text.isEmpty()
                || passwordText.text.isEmpty() || repeatPasswordText.text.isEmpty()) {
                Toast.makeText(this, "There are empty fields", Toast.LENGTH_SHORT).show()

            } else if (passwordText.text.toString() != repeatPasswordText.text.toString()){
                Toast.makeText(this, "Passwords are not the same", Toast.LENGTH_SHORT).show()

            } else if (passwordText.text.length <= 5){
                Toast.makeText(this, "Password too weak", Toast.LENGTH_SHORT).show()

            } else if (!isEmailValid(emailText.text)){
                Toast.makeText(this, "This email address is not correct", Toast.LENGTH_SHORT).show()

            } else {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(emailText.text.toString(), passwordText.text.toString())
                    .addOnCompleteListener{
                        if (it.isSuccessful){
                            startApp(usernameText.text.toString(), it.result?.user?.email ?: "")
                        } else {
                            showAlert()
                        }
                    }
            }
        }
    }

    private fun showAlert(){
        val  builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun isEmailValid(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun startApp(username: String, email: String){
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("username", username)
            putExtra("email", email)
        }
        startActivity(mainIntent)
    }

}