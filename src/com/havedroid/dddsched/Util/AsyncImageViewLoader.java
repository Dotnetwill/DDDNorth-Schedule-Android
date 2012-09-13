package com.havedroid.dddsched.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import com.havedroid.dddsched.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncImageViewLoader extends AsyncTask<ImageView, Object, Bitmap> {
    private ImageView imageView;
    private String downloadedUrl;
    private final Context context;

    public AsyncImageViewLoader(Context context) {

        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(ImageView... imageViews) {
        imageView = imageViews[0];
        downloadedUrl = (String) imageView.getTag();
        String fileName = String.valueOf(downloadedUrl.hashCode());

        File imageThumb = new File(getFileCacheLocation(), fileName + ".jpg");
        if (imageThumb.exists()) {
            Log.v(Constants.LOG_TAG, "Cache hit, using file: " + imageThumb.toString());
            return BitmapFactory.decodeFile(imageThumb.toString());
        } else {
            try {
                Log.v(Constants.LOG_TAG, "Cache miss downloading: " + downloadedUrl);
                URL newurl = new URL(downloadedUrl);
                Bitmap image = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                if(image != null){
                    Log.v(Constants.LOG_TAG, "Image downloaded");
                    FileOutputStream cachedFile = new FileOutputStream(imageThumb);
                    image.compress(Bitmap.CompressFormat.JPEG, 90, cachedFile);
                    Log.v(Constants.LOG_TAG, "Image saved to " + imageThumb.getName());
                    return image;
                }else{
                    Log.e(Constants.LOG_TAG, "Failed to create Image from stream of URL: " + downloadedUrl);
                }
            } catch (MalformedURLException e) {
                Log.e(Constants.LOG_TAG, "Invalid URL: " + downloadedUrl);
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Unable to download image [" + downloadedUrl +"] error: " + e.toString());
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Bitmap image) {
        if (image != null && imageView.getTag() == downloadedUrl) {
            Log.v(Constants.LOG_TAG, "Setting imageview with image");
            imageView.setImageBitmap(image);
        }else if(image != null){
            Log.v(Constants.LOG_TAG, "Image view url changed from [" + downloadedUrl + "] to [" + imageView.getTag() + "]");
        }
    }

    private File getFileCacheLocation() {
        File cacheDir;

            Log.v(Constants.LOG_TAG, "using local cache");
            cacheDir = context.getCacheDir();



        return cacheDir;
    }
}
