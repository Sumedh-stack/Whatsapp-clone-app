package com.chatt.android.chatforfun

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chatt.android.chatforfun.ModelClasses.Users
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_visit_profile.*

class VisitProfileActivity : AppCompatActivity() {
    private var userVisitId:String=""
    private var user:Users?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_profile)
        userVisitId=intent.getStringExtra("visit_id")
        val ref=FirebaseDatabase.getInstance().reference.child("Users").child(userVisitId)

        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
               if(p0.exists()){
                    user=p0.getValue(Users::class.java)
                   username_display.text=user!!.getUsername()
                   Picasso.get().load(user!!.getProfile()).into(profile_display)
                   Picasso.get().load(user!!.getCover()).into(cover_display)
               }
            }

        })
        facebook_display.setOnClickListener{
            val uri= Uri.parse(user!!.getFacebook())
            val intent= Intent(Intent.ACTION_VIEW,uri )
            startActivity(intent)
        }
        instagram_display.setOnClickListener{
            val uri= Uri.parse(user!!.getInstagram())
            val intent= Intent(Intent.ACTION_VIEW,uri )
            startActivity(intent)
        }
        website_display.setOnClickListener{
            val uri= Uri.parse(user!!.getWebsite())
            val intent= Intent(Intent.ACTION_VIEW,uri )
            startActivity(intent)
        }
        send_msg_btn.setOnClickListener{
            val intent =Intent(this,MessageChatActivity::class.java)
            intent.putExtra("visit_id",user!!.getUID())
             startActivity(intent)
        }
    }
}