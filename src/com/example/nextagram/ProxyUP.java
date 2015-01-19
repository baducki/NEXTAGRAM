package com.example.nextagram;

import java.io.File;
import java.io.FileNotFoundException;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ProxyUP {
	private static AsyncHttpClient client = new AsyncHttpClient(); 
	
	public static void uploadArticle(Article article, String filePath, 
			AsyncHttpResponseHandler responseHandler) { 
		RequestParams params = new RequestParams();
		params.put("title", article.getTitle());
		params.put("writer", article.getWriter());
		params.put("id", article.getId());
		params.put("content",article.getContent());
		params.put("writeDate",article.getWriteDate());
		params.put("imgName",article.getImgName());
		
		try {
			params.put("uploadedfile", new File( filePath ));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		client.post("http://10.73.39.188:5009/upload", params, responseHandler); 
	}
}

/* public class ProxyUP {
	
	public static String SERVER_URL = "http://10.73.43.33:5009";
	private RestAdapter restAdapter;
	private NextagramService service;
      
    public void uploadArticle(Article article, TypedFile imageFile) {
    	
		restAdapter = new RestAdapter.Builder().setEndpoint(SERVER_URL).build();

		service = restAdapter.create(NextagramService.class);
    	
		service.updateArticle(
				article.getTitle(),
				article.getWriter(),
				article.getId(),
				article.getContent(),
				article.getWriteDate(),
				article.getImgName(),
				imageFile,
				new Callback<DefaultResponse>(){

					@Override
					public void failure(RetrofitError error) {
						Log.i("test", ""+error);
						
					}

					@Override
					public void success(DefaultResponse uploadResponse, Response response) {
						if (uploadResponse.getStatus() == 200) {
							Log.i("test","uploadSuccess");
						} else  {
							Log.i("test","uploadResponseError");
						}
					}

				});
	}
} */