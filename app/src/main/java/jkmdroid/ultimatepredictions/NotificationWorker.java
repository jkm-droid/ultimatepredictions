package jkmdroid.ultimatepredictions;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

/**
 * Created by jkm-droid on 05/04/2021.
 */

public class NotificationWorker extends Worker{
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    public Result doWork() {
        show_notification("New tips have just been added...check them out");
        return Result.success();
    }
    
    public void show_notification(String notificationMessage){
        NotificationManager notificationManager = (NotificationManager)getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("TopTier Odds", "TopTier Odds", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "toptier odds")
                .setContentTitle("New tips added")
                .setContentText(notificationMessage)
                .setVibrate(new long[]{1000})
                .setSmallIcon(R.mipmap.ic_launcher);

        notificationManager.notify(1, notification.build());
    }
}
