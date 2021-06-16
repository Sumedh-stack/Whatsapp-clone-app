package com.chatt.android.chatforfun

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.chatt.android.chatforfun.Fragments.ChatsFragment
import com.chatt.android.chatforfun.Fragments.SearchFragment
import com.chatt.android.chatforfun.Fragments.SettingsFragment
import com.chatt.android.chatforfun.ModelClasses.Chat
import com.chatt.android.chatforfun.ModelClasses.ChatList
import com.chatt.android.chatforfun.ModelClasses.Users
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
         var firebaseuser:FirebaseUser?=null
         var refUser:DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar_main))

        val toolbar :androidx.appcompat.widget.Toolbar=findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

        val tabLayout:TabLayout=findViewById(R.id.tab_layout)
        val viewPager:ViewPager=findViewById(R.id.view_pager)

val ref=FirebaseDatabase.getInstance().reference.child("Chats")
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val viewPagerAdapter=ViewPagerAdapter(supportFragmentManager)
                var countUnreadMessages=0
                for(dataSnapshot in p0.children){
                    val chat=dataSnapshot.getValue(Chat::class.java)
                    if(chat!!.getReceiver().equals(firebaseuser!!.uid)&& chat.IsSeen()==false){
                        countUnreadMessages+=1
                    }
                }
                if(countUnreadMessages==0){
                    viewPagerAdapter.addFragment(ChatsFragment(),"Chats")

                }
                else{
                    viewPagerAdapter.addFragment(ChatsFragment(),"($countUnreadMessages) Chats")
                }
                       viewPagerAdapter.addFragment(SearchFragment(),"Search")
      viewPagerAdapter.addFragment(SettingsFragment(),"Settings")
                        viewPager.adapter=viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
            }

        })


        firebaseuser=FirebaseAuth.getInstance().currentUser
        refUser=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseuser!!.uid)
       refUser!!.addValueEventListener(object:ValueEventListener{
           override fun onCancelled(error: DatabaseError) {
               TODO("Not yet implemented")
           }

           override fun onDataChange(p0: DataSnapshot) {
              if(p0.exists()){
                  val user: Users? =p0.getValue(Users::class.java)
                  user_name.text=user!!.getUsername()
                  Picasso.get().load(user.getProfile()).placeholder(R.drawable.ic_profile).into(profile_image)
             }
           }

       })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

         when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent= Intent(this,WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                return true
            }

        }
        return false
    }
    private fun updateStatus(status:String){
        val ref=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseuser!!.uid)
        val hashMap=HashMap<String,Any>()
        hashMap["status"]=status
        ref!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")

    }

    override fun onPause() {
        super.onPause()
        updateStatus("offline")
    }
    internal class ViewPagerAdapter(fragmentManager:FragmentManager):FragmentPagerAdapter(fragmentManager){
        private val fragments:ArrayList<Fragment>
        private val titles:ArrayList<String>
        init{
            fragments=ArrayList<Fragment>()
            titles=ArrayList<String>()

        }
        override fun getItem(position: Int): Fragment {
                 return fragments[position]
        }

        override fun getCount(): Int {
              return fragments.size
        }
        fun addFragment(fragment:Fragment,title:String){
            fragments.add(fragment)  
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }

    }
}