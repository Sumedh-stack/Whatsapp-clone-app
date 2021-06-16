package com.chatt.android.chatforfun

import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chatt.android.chatforfun.AdapterClasses.ChatsAdapter
import com.chatt.android.chatforfun.Fragments.APIService
import com.chatt.android.chatforfun.ModelClasses.Chat
import com.chatt.android.chatforfun.ModelClasses.Users
import com.chatt.android.chatforfun.Notifications.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_AUTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageChatActivity : AppCompatActivity() {
    var userIdVisit:String=""
    var firebaseuser: FirebaseUser?=null
    var chatsAdapter:ChatsAdapter?=null
    var mChatList:List<Chat>?=null
    var reference:DatabaseReference?=null
    var notify=false
    var apiService: APIService?=null
    lateinit var recycler_view_chats: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val toolbar:androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent= Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }


        apiService= Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
        intent=intent
        userIdVisit=intent?.getStringExtra("visit_id").toString()
        firebaseuser=FirebaseAuth.getInstance().currentUser

        recycler_view_chats=findViewById(R.id.recycler_view_chats)
        var linearLayoutManager=LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd=true
        recycler_view_chats.layoutManager=linearLayoutManager



         reference=FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        reference!!.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
            val user=p0.getValue(Users::class.java)
                username_mchat.text=user!!.getUsername()
                Picasso.get().load(user.getProfile()).into(profile_image_message_chat)
                retrieveMessage(firebaseuser!!.uid,userIdVisit,user.getProfile())
            }



        })
        send_message_btn.setOnClickListener{
            notify=true
            val message=text_message.text.toString()
            if(message==""){
                Toast.makeText(this,"Please write a  Message first..", Toast.LENGTH_SHORT).show()

            }else{
                sendMessagetoUser(firebaseuser!!.uid,userIdVisit,message)
            }
            text_message.setText("")
        }
        attach_image_file_btn.setOnClickListener{
            notify=true
            val intent=Intent()
        intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"),438)

        }
        seenMessage(userIdVisit)
    }

    private fun sendMessagetoUser(senderId: String, receiverId: String?, message: String) {
    val reference=FirebaseDatabase.getInstance().reference
        val messageKey=reference.push().key
        val messageHashMap=HashMap<String,Any?>()
        messageHashMap["sender"]=senderId
        messageHashMap["message"]=message
        messageHashMap["receiver"]=receiverId
        messageHashMap["isseen"]=false
        messageHashMap["url"]="offline"
        messageHashMap["messageId"]=messageKey
        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task->
                if(task.isSuccessful){
                    val chatListreference=FirebaseDatabase.getInstance()
                        .reference.child("ChatList").child(firebaseuser!!.uid)
                        .child(userIdVisit)

                    chatListreference.addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                           if(!p0.exists()){
                               chatListreference.child("id").setValue(userIdVisit)

                           }
                            val chatsListReceiverRef=FirebaseDatabase.getInstance().reference.child("ChatList")
                                .child(userIdVisit).child(firebaseuser!!.uid)
                            chatsListReceiverRef.child("id").setValue(firebaseuser!!.uid)
                        }

                    })



                }
            }

        val userReference=FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseuser!!.uid)
        userReference.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val user=p0.getValue(Users::class.java)
                if(notify){
                    sendNotificationChange(receiverId,user!!.getUsername(),message)
                }
            }




        })
    }
    private fun sendNotificationChange(receiverId: String?, username: String?, message: String) {
val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val query=ref.orderByKey().equalTo(receiverId)
        query.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                for(dataSnapshot in p0.children){
                    val token: Token?=dataSnapshot.getValue(Token::class.java)
                    val data=Data(firebaseuser!!.uid,R.mipmap.ic_launcher,"$username: $message","New Message",userIdVisit)
                    val sender= Sender(data!!,token!!.getToken().toString())
                    apiService!!.sendNotification(sender).enqueue(object: Callback<MyResponse>{
                        override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                            TODO("Not yet implemented")
                        }

                        override fun onResponse(
                            call: Call<MyResponse>,
                            response: Response<MyResponse>
                        ) {
                            if(response.code()==200){
                                if(response.body()!!.success!=1){
                                    Toast.makeText(this@MessageChatActivity,"Failed,Nothing happen",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    })


                }
            }

        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==438&&resultCode==RESULT_OK&&data!=null&&data!!.data!=null){
            val progressbar=ProgressDialog(this)
            progressbar.setMessage("image is uploading")
            progressbar.show()
            val fileUri=data.data
            val storageReference=FirebaseStorage.getInstance().reference.child("Chat Image")
            val ref=FirebaseDatabase.getInstance().reference
            val messageId=ref.push().key
            val filePath=storageReference.child("$messageId.jpj")


                var uploadTask: StorageTask<*>
                uploadTask=filePath.putFile(fileUri!!)
                uploadTask.continueWithTask(Continuation  <UploadTask.TaskSnapshot, Task<Uri>>{ task->

                    if(!task.isSuccessful){
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation filePath.downloadUrl
                }).addOnCompleteListener {task->
                    if(task.isSuccessful){
                        val downloadUrl=task.result
                        val url=downloadUrl.toString()

                        val messageHashMap=HashMap<String,Any?>()
                        messageHashMap["sender"]=firebaseuser!!.uid
                        messageHashMap["message"]="sent you an image. "
                        messageHashMap["receiver"]=userIdVisit
                        messageHashMap["isseen"]=false
                        messageHashMap["url"]=url
                        messageHashMap["messageId"]=messageId
                        ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                            .addOnCompleteListener { task->
                                if(task.isSuccessful){
                                    progressbar.dismiss()
                                    val reference=FirebaseDatabase.getInstance().reference
                                        .child("Users").child(firebaseuser!!.uid)
                                    reference.addValueEventListener(object:ValueEventListener{
                                        override fun onCancelled(error: DatabaseError) {

                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            val user=p0.getValue(Users::class.java)
                                            if(notify){
                                                sendNotificationChange(userIdVisit,user!!.getUsername(),"sent you an image. ")
                                            }
                                        }




                                    })
                                }
                            }

                    }}

        }
    }
    private fun retrieveMessage(senderId: String, receiverId: String?, receiverimageUrl: String?) {
      mChatList=ArrayList()
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                ( mChatList as ArrayList<Chat>).clear()
                for(snapshot in p0.children){
                   val chat=snapshot.getValue(Chat::class.java)
                    if(chat!!.getReceiver().equals(senderId)&&chat.getSender().equals(receiverId)||chat.getReceiver().equals(receiverId)&&chat.getSender().equals(senderId)){
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                    chatsAdapter=ChatsAdapter(this@MessageChatActivity,(mChatList  as ArrayList<Chat>),receiverimageUrl!!)
                     recycler_view_chats.adapter=chatsAdapter
                }
            }

        })
    }
    var seenListener:ValueEventListener?=null
    private fun seenMessage(userId:String){
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener=reference!!.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
               for(dataSnapshot in p0.children){
                   val chat =dataSnapshot.getValue(Chat::class.java)
                   if(chat!!.getReceiver().equals(firebaseuser!!.uid)&&chat!!.getSender().equals(userId)){
                       val hashMap=HashMap<String,Any>()
                       hashMap["isseen"]=true
                       dataSnapshot.ref.updateChildren(hashMap)
                   }
               }
            }

        })

    }

    override fun onPause() {
        super.onPause()
        reference!!.removeEventListener(seenListener!!)
    }
}