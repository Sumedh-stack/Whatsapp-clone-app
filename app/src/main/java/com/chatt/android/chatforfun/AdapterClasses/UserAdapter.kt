package com.chatt.android.chatforfun.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.chatt.android.chatforfun.MessageChatActivity
import com.chatt.android.chatforfun.ModelClasses.Chat
import com.chatt.android.chatforfun.ModelClasses.Users
import com.chatt.android.chatforfun.R
import com.chatt.android.chatforfun.VisitProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.zip.CheckedOutputStream

class UserAdapter(mContext: Context, mUsers:List<Users>,isChatBox:Boolean):
    RecyclerView.Adapter<UserAdapter.ViewHolder?>() {
    private val mContext:Context
    private val mUsers:List<Users>
   private var isChatBox:Boolean
    var lstmsg:String?=null
    init{
        this.mContext=mContext
        this.mUsers=mUsers
        this.isChatBox=isChatBox
    }
    override fun onCreateViewHolder(viewGroup :ViewGroup, viewType: Int): UserAdapter.ViewHolder {
val view:View=LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout,viewGroup,false)
        return UserAdapter.ViewHolder(view)
    }
    override fun getItemCount(): Int {
      return mUsers.size

    }
    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, i: Int) {
        val user: Users? =mUsers[i]
        holder.userNameTxt.text=user!!.getUsername()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.ic_profile).into(holder.profileImageView)


        if(isChatBox){
            retrievelastMessage(user.getUID(),holder.lastMessageTxt)
        }else{
            holder.lastMessageTxt.visibility=View.GONE
        }
        if(isChatBox){
            if(user.getStatus()=="online"){
                holder.onlineImageView.visibility =View.VISIBLE
                holder.offlineImageView.visibility =View.GONE
            }else{
                holder.onlineImageView.visibility =View.GONE
                holder.offlineImageView.visibility =View.VISIBLE
            }
        }
        else{
            holder.onlineImageView.visibility =View.GONE
            holder.offlineImageView.visibility =View.GONE
        }
        holder.itemView.setOnClickListener {
            val options=arrayOf<CharSequence>("SendMessage","Visit Profile")
            val builder=AlertDialog.Builder(mContext)
            builder.setTitle("What do you want?")
            builder.setItems(options,DialogInterface.OnClickListener{dialog,position->
                if(position==0){
                    val intent =Intent(mContext,MessageChatActivity::class.java)
                    intent.putExtra("visit_id",user.getUID())
                    mContext.startActivity(intent)
                }
                if(position==1){
                    val intent =Intent(mContext,VisitProfileActivity::class.java)
                    intent.putExtra("visit_id",user.getUID())
                    mContext.startActivity(intent)
                }
            })
            builder.show()
        }
    }

    private fun retrievelastMessage(chatUserId: String?, lastMessageTxt: TextView) {
        lstmsg="defaultMsg"
        val firebaseUser=FirebaseAuth.getInstance().currentUser
        val reference=FirebaseDatabase.getInstance().reference.child("Chats")
        reference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
              for(dataSnapshot in p0.children){
                  val chat:Chat?=dataSnapshot.getValue(Chat::class.java)
                  if(firebaseUser!=null&&chat!=null){
                      if(chat.getReceiver()==firebaseUser!!.uid&&chat.getSender()==chatUserId||chat.getReceiver()==chatUserId&&chat.getSender()==firebaseUser!!.uid){
                          lstmsg=chat.getMessage()
                      }
                  }
              }
                when(lstmsg){
                    "defaultMsg"->lastMessageTxt.text="No Message"
                    "sent you an image."->lastMessageTxt.text="image sent."
                    else->{
                        lastMessageTxt.text=lstmsg
                    }
                }
                lstmsg="defaultMsg"
            }

        })
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var userNameTxt:TextView
        var profileImageView:CircleImageView
        var onlineImageView:CircleImageView
        var offlineImageView:CircleImageView
        var lastMessageTxt:TextView
        init{
            userNameTxt=itemView.findViewById(R.id.username)
            profileImageView=itemView.findViewById(R.id.profile_image)
            onlineImageView=itemView.findViewById(R.id.image_online)
            offlineImageView=itemView.findViewById(R.id.image_offline)
            lastMessageTxt=itemView.findViewById(R.id.message_last)
        }
    }

}