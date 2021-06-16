package com.chatt.android.chatforfun.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chatt.android.chatforfun.AdapterClasses.UserAdapter
import com.chatt.android.chatforfun.ModelClasses.Users
import com.chatt.android.chatforfun.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*


class SearchFragment : Fragment() {

    private var userAdapter:UserAdapter ? = null
    private var mUsers: List<Users>? = null
    private var recyclerView: RecyclerView? = null
    private var searchEditText:EditText?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

         val view=inflater.inflate(R.layout.fragment_search, container, false)
        searchEditText=view.findViewById(R.id.searchUsersEt)
        mUsers=ArrayList()
        retrieveAllUsers()
        recyclerView=view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager=LinearLayoutManager(context)
        searchEditText!!.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(cs: Editable?) {

            }
            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(cs.toString().toLowerCase())
            }
            override fun beforeTextChanged(cs: CharSequence?, start: Int, count: Int, after: Int) {

            }



        })
        return view
    }

    private fun retrieveAllUsers() {
        var firebaseUserId=FirebaseAuth.getInstance().currentUser!!.uid
        val refUser= FirebaseDatabase.getInstance().reference.child("Users")
refUser.addValueEventListener(object :ValueEventListener{
    override fun onCancelled(error: DatabaseError) {

    }

    override fun onDataChange(p0: DataSnapshot) {

        (mUsers as ArrayList<Users>).clear()
        if(searchEditText!!.text.toString()==""){
            for(snapshot in p0.children){
                val user: Users? =snapshot.getValue(Users::class.java)
                if(!(user!!.getUID().equals(firebaseUserId))){
                    (mUsers as ArrayList<Users>).add(user)
                }
            }
            userAdapter= UserAdapter(context!!,mUsers!!,false)
            recyclerView!!.adapter=userAdapter
        }

    }

})
    }
private fun searchForUsers(str:String){
    var firebaseUserId=FirebaseAuth.getInstance().currentUser!!.uid
    val queryUsers= FirebaseDatabase.getInstance().reference.child("Users")
        .orderByChild("search").startAt(str).endAt(str + "\uf8ff")
    queryUsers.addValueEventListener(object :ValueEventListener {
        override fun onCancelled(error: DatabaseError) {

        }

        override fun onDataChange(p0: DataSnapshot) {
            (mUsers as ArrayList<Users>).clear()
            for (snapshot in p0.children) {
                val user: Users? = snapshot.getValue(Users::class.java)
                if (!(user!!.getUID().equals(firebaseUserId))) {
                    (mUsers as ArrayList<Users>).add(user)
                }
            }
            userAdapter= UserAdapter(context!!,mUsers!!,false)
            recyclerView!!.adapter=userAdapter
        }
    })
}
}