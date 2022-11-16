package com.example.facebooklogin.ui.signinfragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.facebooklogin.Notification
import com.example.facebooklogin.databinding.FragmentSignInBinding
import com.facebook.FacebookSdk
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignInFragment : Fragment() {

    companion object {
        fun newInstance() = SignInFragment()
    }

    private lateinit var binding: FragmentSignInBinding
    private lateinit var viewModel: SignInViewModel

    private lateinit var auth: FirebaseAuth
    private val REQ_SIGN_IN = 2  // Can be any integer unique to the Activity
    private var showOneTapUI = true

    var mSignInClient: GoogleSignInClient? = null
    private val TAG: String = "SignInFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FacebookSdk.setAutoInitEnabled(true)
        binding = FragmentSignInBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(this)[SignInViewModel::class.java]

        // Initialize Firebase Auth
        auth = Firebase.auth


        // Initialize sign in options
        // the client-id is copied form
        // google-services.json file
        // Initialize sign in options
        // the client-id is copied form
        // google-services.json file
        val googleSignInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("355263672218-0lfmvq4risqcf5j39c816ekceqkdl5kk.apps.googleusercontent.com")
            .requestEmail()
            .build()


        // Initialize sign in client
        mSignInClient = GoogleSignIn.getClient(
            this.requireContext(), googleSignInOptions
        );


        binding.signInButton.setOnClickListener {
            signIn()
        }

        binding.logOutBtn.setOnClickListener {
            auth.signOut()
            binding.logOutBtn.visibility = View.GONE
            binding.signInButton.visibility = View.VISIBLE
            binding.userName.text = "displayName"
        }
        
        binding.crashBtn.setOnClickListener { 
            throw java.lang.RuntimeException("Crash Test")
        }

        binding.notifyBtn.setOnClickListener {
            Notification().pushNotification(this.requireContext())
        }


        return binding.root
    }


    private fun signIn() {
        val signInIntent = mSignInClient!!.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    handleSignInResult(task)
                }
            }
        }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Toast.makeText(this.requireContext(), account.displayName, Toast.LENGTH_LONG).show()
            // Signed in successfully, show authenticated UI.

            if (account != null) {
                //                val personName: String = account.displayName!!
                //                val personGivenName: String = account.givenName!!
                //                val personFamilyName: String = account.familyName!!
                //                val personEmail: String = account.email!!
                //                val personId: String = account.id!!
                //                val personPhoto: Uri = account.photoUrl!!

                val token = account.idToken
                if (token != null) {
                    firebaseAuthWithGoogle(token)
                }
            } else {
                Toast.makeText(this.requireContext(), "Access Denied", Toast.LENGTH_SHORT).show()

            }

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    if (user != null) {
                        binding.userName.text = user.displayName
                        binding.logOutBtn.visibility = View.VISIBLE
                        binding.signInButton.visibility = View.GONE
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.userName.text = currentUser.displayName
            binding.logOutBtn.visibility = View.VISIBLE
            binding.signInButton.visibility = View.GONE
        }
    }
}