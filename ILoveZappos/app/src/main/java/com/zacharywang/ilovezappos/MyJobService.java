package com.zacharywang.ilovezappos;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyJobService extends JobService {
    SharedPreferences sharedPrefs;
    String userString;
    Double userDouble;

    @Override
    public boolean onStartJob(JobParameters job) {
        // Do some work here

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        userString = sharedPrefs.getString("example_text", "-1");
        userDouble = Double.parseDouble(userString);

        // check if you are connected or not
        if(isConnected()){
            Log.d("MyJobService", "You are connected");
        }
        else{
            Log.d("MyJobService", "You are NOT connected");
        }

        // call AsynTask to perform network operation on separate thread
        new HttpAsyncTask().execute("https://www.bitstamp.net/api/v2/ticker_hour/btcusd/");

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {

        return false; // Answers the question: "Should this job be retried?"
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            // Empty strings for tests
            String msg = "";
            String msg2 = "";
            String msg3 = "";
            String msg4 = "";

            Double msgDouble = 0.0;

            try {

                JSONObject json = new JSONObject(result);
                msg = json.getString("last");
                msgDouble = Double.parseDouble(msg);
            } catch (JSONException e) {
                Log.e("ILoveZappos", "unexpected JSON exception", e);
            }

            if (msgDouble < userDouble) {
                addNotification();
            }

            Log.d("msg", "Received: " + msg);

        }
    }

    @TargetApi(26)
    private void addNotification() {

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;


        if (currentApiVersion == 26) {


            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// The id of the channel.
            String CHANNEL_ID = "my_channel_01";
// The user-visible name of the channel.
            CharSequence name = "channel_name";
// The user-visible description of the channel.
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
// Configure the notification channel.
            mChannel.setDescription(description);
//            mChannel.enableLights(true);
// Sets the notification light color for notifications posted to this
// channel, if the device supports this feature.
//            mChannel.setLightColor(Color.RED);
//            mChannel.enableVibration(true);
//            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);


            PendingIntent contentIntent =
                    PendingIntent
                            .getActivity(this, 0, new Intent(this, MainActivity.class), 0);


            Notification notification = new Notification.Builder(MyJobService.this)
                    .setContentIntent(contentIntent)
                    .setContentTitle("ILoveZappos Message")
                    .setContentText("The current bitcoin price is falling down!")
                    .setSmallIcon(R.drawable.ic_menu_send)
                    .setChannelId(CHANNEL_ID)
                    .build();

// mNotificationId is a unique integer your app uses to identify the
// notification. For example, to cancel the notification, you can pass its ID
// number to NotificationManager.cancel().
            mNotificationManager.notify(378, notification);
        }



//        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= 16)
        {
            Context context = MyJobService.this;

            Intent notificationIntent = new Intent(context, MainActivity.class);
//            PendingIntent contentIntent = PendingIntent.getService(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            // This line of yours should contain the activity that you want to launch.
// You are currently just passing empty new Intent()
            PendingIntent contentIntent =
                    PendingIntent
                            .getActivity(this, 0, new Intent(this, MainActivity.class), 0);

            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Resources res = context.getResources();
            Notification.Builder builder = new Notification.Builder(context);

            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_action_movie)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_action_movie))
                    .setTicker("ticker string!")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle("ILoveZappos Message")
                    .setContentText("The current bitcoin price is falling down!");
            Notification n = builder.build();

            nm.notify(379, n);
        }




    }
}
