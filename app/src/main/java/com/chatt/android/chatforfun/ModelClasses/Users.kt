package com.chatt.android.chatforfun.ModelClasses

class Users {
    private var uid=""
    private var username=""
    private var profile=""
    private var cover=""
    private var status=""
    private var search=""
    private var facebook=""
    private var instagram=""
    private var website=""

    constructor()
    constructor(uid: String, username: String, profile: String, cover: String, status: String, search: String, facebook: String, instagram: String, website: String) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.cover = cover
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
    }
    fun getUID():String?{
       return uid
   }
    fun setUID(uid:String){
        this.uid=uid
    }
    fun getUsername():String?{
        return username
    }
    fun setUsername(username:String){
        this.username=username
    }
    fun getProfile():String?{
        return profile
    }
    fun setProfile(profile:String){
        this.profile=profile
    }
    fun getCover():String?{
        return cover
    }
    fun setCover(cover:String){
        this.cover=cover
    }
    fun getStatus():String?{
        return status
    }
    fun setStatus(status:String){
        this.status=status
    }

    fun getSearch():String?{
        return search
    }
    fun setSearch(uid:String){
        this.search=search
    }
    fun getFacebook():String?{
        return facebook
    }
    fun setFacebook(facebook:String){
        this.facebook=facebook
    }
    fun getInstagram():String?{
        return instagram
    }
    fun setInstagram(facebook:String){
        this.instagram=instagram
    }
    fun getWebsite():String?{
        return website
    }
    fun setWebsite(website:String){
        this.website=website
    }



}