package com.chatt.android.chatforfun.ModelClasses

class Chat {
    private var sender=""
    private var message=""
    private var receiver=""
    private var isseen=false
    private var url=""
    private var messageId=""


    constructor()
    constructor(
        sender: String,
        message: String,
        receiver: String,
        isseen: Boolean,
        url: String,
        messageId: String
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isseen = isseen
        this.url = url
        this.messageId = messageId
    }
    fun getSender():String?{
        return sender
    }
    fun setSender(sender:String){
        this.sender=sender
    }
    fun getMessage():String?{
        return message
    }
    fun setMessage(message:String){
        this.message=message
    }
    fun getReceiver():String?{
        return receiver
    }
    fun setReceiver(receiver:String){
        this.receiver=receiver
    }
    fun IsSeen():Boolean?{
        return isseen
    }
    fun setIsSeen(isseen:Boolean){
        this.isseen=isseen!!
    }
    fun getUrl():String?{
        return url
    }
    fun setUrl(url:String){
        this.url=url!!
    }

    fun getMessageId():String?{
        return messageId
    }
    fun setMessageId(messageId:String){
        this.messageId=messageId
    }

}