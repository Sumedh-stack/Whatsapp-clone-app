package com.chatt.android.chatforfun

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {
    var firebaseuser:FirebaseUser?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        firebaseuser=FirebaseAuth.getInstance().currentUser
        register_welcome_btn.setOnClickListener {
            val intent= Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()


        }
        login_welcome_btn.setOnClickListener {

            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
        if(firebaseuser!=null){
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}