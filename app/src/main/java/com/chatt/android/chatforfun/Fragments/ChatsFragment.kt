package com.chatt.android.chatforfun.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chatt.android.chatforfun.AdapterClasses.UserAdapter
import com.chatt.android.chatforfun.ModelClasses.ChatList
import com.chatt.android.chatforfun.ModelClasses.Users
import com.chatt.android.chatforfun.Notifications.Token
import com.chatt.android.chatforfun.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal


class ChatsFragment : Fragment() {
    private var userAdapter: UserAdapter? = null
    private var mUsers: List<Users>? = null
    private var usersChatList: List<ChatList>? = null
    lateinit var recycler_view_chatList: RecyclerView
    private var firebaseUser:FirebaseUser?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view= inflater.inflate(R.layout.fragment_chats, container, false)
    recycler_view_chatList=view.findViewById(R.id.recycler_view_chatList)
        recycler_view_chatList.setHasFixedSize(true)
        recycler_view_chatList.layoutManager=LinearLayoutManager(context)
        firebaseUser=FirebaseAuth.getInstance().currentUser
        usersChatList=ArrayList()
        val ref=FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                (usersChatList as ArrayList).clear()
                       for (dataSnapshot in p0.children){
                           val chatList=dataSnapshot.getValue(ChatList::class.java)
                           (usersChatList as ArrayList).add(chatList!!)
                       }
                retrieveChatList()
            }


        })

        updateToken(FirebaseInstanceId.getInstance().token)
     return view
    }

    private fun updateToken(token: String?) {
val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1= Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retrieveChatList(){
mUsers=ArrayList()
    val ref=FirebaseDatabase.getInstance().reference.child("Users")
    ref!!.addValueEventListener(object :ValueEventListener{
        override fun onCancelled(error: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            (mUsers as ArrayList).clear()
            for(dataSnapshot in p0.children){
                val user=dataSnapshot.getValue(Users::class.java)
                for(eachChatList in usersChatList!!){
                    if(user!!.getUID().equals(eachChatList.getId()))
                    (mUsers as ArrayList).add(user!!)
                }
                userAdapter= UserAdapter(context!!,(mUsers as ArrayList<Users>),true)
                recycler_view_chatList.adapter=userAdapter
            }
        }


    })
}
}