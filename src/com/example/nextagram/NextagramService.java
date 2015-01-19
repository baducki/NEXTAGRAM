package com.example.nextagram;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

import com.google.gson.JsonArray;

public interface NextagramService {
	 @GET("/loadData")
	 JsonArray getJsonData();
	 
	 @Multipart
	 @Headers("Cache-Control: no-cache; Connection: Keep-Alive")
	 @POST("/upload")
	 void updateArticle(
			 @Part("title") String title,
			 @Part("writer") String writer,
			 @Part("id") String id,
			 @Part("content") String content,
			 @Part("writeDate") String writeDate,
			 @Part("imgName") String imgName,
			 @Part("uploadedfile") TypedFile photo,
			 Callback<DefaultResponse> cb);
	 
}	