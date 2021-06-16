package com.chatt.android.chatforfun

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {
    lateinit var  mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar :androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent= Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth= FirebaseAuth.getInstance()
        login_btn.setOnClickListener {
            loginuser()
        }
    }

    private fun loginuser() {

        val email=email_login.text.toString()
        val password=password_login.text.toString()
        when {

            email=="" -> {
                Toast.makeText(this,"Please write email ", Toast.LENGTH_SHORT).show()
            }
            password=="" -> {
                Toast.makeText(this,"Please write password", Toast.LENGTH_SHORT).show()
            }
            else->{
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val intent= Intent(this,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)//Even if pressing back key we will not come back
                        startActivity(intent)
                        finish()

                    }else{
                        Toast.makeText(this,"Error Message "+task.exception!!.message.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
                }
        }
    }
}