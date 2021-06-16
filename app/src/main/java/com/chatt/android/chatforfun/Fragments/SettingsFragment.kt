package com.chatt.android.chatforfun.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.chatt.android.chatforfun.ModelClasses.Users
import com.chatt.android.chatforfun.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*


class SettingsFragment : Fragment() {

    private var userReference: DatabaseReference? = null
    private var firebaseUser:FirebaseUser?= null
    private val Requestcode=438
    private var imageUri:Uri?=null
    private var storageRef:StorageReference?=null
    private var coverChecker:String?=""
    private var socialChecker:String?=""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser=FirebaseAuth.getInstance().currentUser
        userReference=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef=FirebaseStorage.getInstance().reference.child("User Images")
        userReference!!.addValueEventListener(object :ValueEventListener{
    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }

    override fun onDataChange(p0: DataSnapshot) {
if(p0.exists()){
    val user: Users? =p0.getValue(Users::class.java)
    if(context!=null){
        view.username_settings.text=user!!.getUsername()
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.ic_profile).into(view.profile_image_settings)
        Picasso.get().load(user.getCover()).placeholder(R.drawable.ic_profile).into(cover_image_settings)
    }

}
    }

})
        view.profile_image_settings.setOnClickListener {
            pickImage()
        }
        view.cover_image_settings.setOnClickListener {
            coverChecker="cover"
            pickImage()
        }
        view.set_facebook.setOnClickListener {
            socialChecker="facebook"
                  setSocialLink()
        }
        view.set_instagram.setOnClickListener {
            socialChecker="instagram"
            setSocialLink()
        }
        view.set_website.setOnClickListener {
                    socialChecker="website"
                    setSocialLink()
        }


        return view
    }

    private fun setSocialLink() {
        val builder:AlertDialog.Builder=AlertDialog.Builder(requireContext(),R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        if(socialChecker=="website"){
            builder.setTitle("Write UrL")
        }else{
            builder.setTitle("Write username:")
        }
        val editText=EditText(context)
        if(socialChecker=="website"){
            editText.hint="e.g www.google.com"
        }else{
            editText.hint="Sumedh@123"
        }
        builder.setView(editText)
        builder.setPositiveButton("Create",DialogInterface.OnClickListener{
            dialog, which ->
            val str=editText.text.toString()
            if (str==""){
                Toast.makeText(context,"Please write something..",Toast.LENGTH_SHORT).show()
            }else{
                saveSocialLink(str)
            }
        })
        builder.setNegativeButton("Cancel",DialogInterface.OnClickListener{
            dialog,which->
           dialog.cancel()

        })
        builder.show()

    }

    private fun saveSocialLink(str: String) {
        val mapSocial=HashMap<String,Any>()
       

        when(socialChecker) {
            "facebook"->{
                mapSocial["facebook"]="https://m.facebook.com/$str"
              }
            "instagram"->{
                mapSocial["instagram"]="https://m.instagram.com/$str"
            }
            "website"->{
                mapSocial["website"]="https://$str"
            }
        }
        userReference!!.updateChildren(mapSocial).addOnCompleteListener {task->
            if(task.isSuccessful){
                Toast.makeText(context,"updated successfully",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImage() {
      val intent= Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,Requestcode )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Requestcode&&resultCode== Activity.RESULT_OK&&data!!.data!=null){
           imageUri=data.data
            Toast.makeText(context,"your image is uploading",Toast.LENGTH_SHORT).show()
            uploadImage()
        }
    }

    private fun uploadImage() {
      val progressbar=ProgressDialog(context)
        progressbar.setMessage("image is uploading")
        progressbar.show()
        if(imageUri!=null){
            val fileRef=storageRef!!.child(System.currentTimeMillis().toString()+".jpg")
            var uploadTask:StorageTask<*>
            uploadTask=fileRef.putFile(imageUri!!)
            uploadTask.continueWithTask(Continuation  <UploadTask.TaskSnapshot, Task<Uri>>{task->

                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
             return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener {task->
                if(task.isSuccessful){
                    val downloadUrl=task.result
                    val url=downloadUrl.toString()

                if(coverChecker=="cover"){
                     val mapCoverImage=HashMap<String,Any>()
                    mapCoverImage["cover"]=url
                    userReference!!.updateChildren(mapCoverImage)
                    coverChecker=""

                }
               else{
                    val mapProfileImg=HashMap<String,Any>()
                    mapProfileImg["profile"]=url
                    userReference!!.updateChildren(mapProfileImg)
                    coverChecker=""
                }
                    progressbar.dismiss()
            }}

        }
    }
}