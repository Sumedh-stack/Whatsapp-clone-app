package com.chatt.android.chatforfun.Fragments;

import com.chatt.android.chatforfun.Notifications.MyResponse;
import com.chatt.android.chatforfun.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAsCXEihQ:APA91bHsWfAm7ZgAlEs-Pul-WFmLEDsQE78shFVfsP_mZT1zaqKGUE2V-3UkqInhzRTOmwbmHeK5T-bCmiXR46P2BBaTTTkrxY9Gr2KXCI2BmJv6KC9IfirkDDLK4iovsYUHO7JSpQjV"
    })
    @POST("fcm/send")
        Call<MyResponse> sendNotification(@Body Sender body);

}
