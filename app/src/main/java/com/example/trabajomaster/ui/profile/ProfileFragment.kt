package com.example.trabajomaster.ui.profile

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.trabajomaster.LoginActivity
import com.example.trabajomaster.R
import com.example.trabajomaster.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        // Intentar meter en una función
        /**val bundle: Bundle? = activity?.intent?.extras
        val username: String? = bundle?.getString("username")
        val email: String? = bundle?.getString("email")*/

        userInfo(root)

        val logoutButton = root.findViewById(R.id.logoutButton) as Button
        logoutButton.setOnClickListener {
            logout()
        }

        return root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)


    }

    private fun userInfo(root: View?){

        val sharedPreferences = activity?.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = sharedPreferences?.getString("email", null)
        val username = sharedPreferences?.getString("username", null)

        val usernameText = root?.findViewById<TextView>(R.id.usernameText)
        val emailText = root?.findViewById<TextView>(R.id.emailText)
        usernameText?.text = username
        emailText?.text = email
    }


    private fun logout(){

        val sharedPreferences = activity?.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.remove("email")
        editor?.apply()

        FirebaseAuth.getInstance().signOut()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
    }

}