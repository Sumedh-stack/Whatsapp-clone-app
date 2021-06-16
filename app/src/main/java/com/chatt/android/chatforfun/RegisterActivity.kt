package com.chatt.android.chatforfun

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    lateinit var  mAuth:FirebaseAuth
    lateinit var refUser:DatabaseReference
    private var firebaseUserId=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar :androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title="Register"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent= Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth= FirebaseAuth.getInstance()
        register_btn.setOnClickListener {
            registeruser()
        }
    }

    private fun registeruser() {
        val username=username_register.text.toString()
        val email=email_register.text.toString()
        val password=password_register.text.toString()
        when {
            username=="" -> {
                Toast.makeText(this,"Please write username ",Toast.LENGTH_SHORT).show()
            }
            email=="" -> {
                Toast.makeText(this,"Please write email ",Toast.LENGTH_SHORT).show()
            }
            password=="" -> {
                Toast.makeText(this,"Please write password",Toast.LENGTH_SHORT).show()
            }
            else->{
               mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->

                   if(task.isSuccessful){

                      firebaseUserId=mAuth.currentUser!!.uid
                       refUser=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserId)


                       val userHashMap=HashMap<String,Any>()
                       userHashMap["uid"]=firebaseUserId
                       userHashMap["username"]=username
                       userHashMap["profile"]="https://firebasestorage.googleapis.com/v0/b/messangerapp-a3dfe.appspot.com/o/profphoto.png?alt=media&token=7ff360b5-6215-4c35-93a5-c754ccd918ef"
                       userHashMap["cover"]="https://firebasestorage.googleapis.com/v0/b/messangerapp-a3dfe.appspot.com/o/imgcover.jpg?alt=media&token=8174f3c9-a1d3-4ebb-830c-4d5596338005"
                       userHashMap["status"]="offline"
                       userHashMap["search"]=username.toLowerCase()
                       userHashMap["facebook"]="https://www.facebook.com/"
                       userHashMap["instagram"]="https://www.instagram.com/"
                       userHashMap["website"]="https://www.google.com/"


                     refUser.updateChildren(userHashMap).addOnCompleteListener {task->
                         if(task.isSuccessful){
                             val intent= Intent(this,MainActivity::class.java)
                             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                             startActivity(intent)
                             finish()
                         }

                     }


                   }else{
                       Toast.makeText(this,"Error Message "+task.exception!!.message.toString(),Toast.LENGTH_SHORT).show()
                   }
               }
            }
        }


    }
}