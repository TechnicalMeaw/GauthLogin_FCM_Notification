package com.example.facebooklogin.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.facebooklogin.ui.signinfragment.SignInFragment
import com.example.facebooklogin.databinding.ActivityMainBinding
import com.example.facebooklogin.fcm.NotificationService
import com.facebook.FacebookSdk
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.setAutoInitEnabled(true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if (supportFragmentManager.backStackEntryCount == 0){
//
//        }

        // Setting Up Notification Token
        NotificationService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)



        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && !TextUtils.isEmpty(task.result)) {
                        val token: String = task.result!!
                        NotificationService.token = token
                        Log.d("Notification_Token", token)
//                        mainActivityViewModel.setNotificationToken(token, this)
                    }
                }
            }


        supportFragmentManager.beginTransaction().add(binding.fragmentContainer.id,
            SignInFragment.newInstance()
        ).commit()
    }
}