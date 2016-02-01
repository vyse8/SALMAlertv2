
package matthew_2.salmalert;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MainActivity extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        showToast(messageEvent.getPath());
    }

    private void showToast(String message) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int icon = R.drawable.staffoflife;
        System.out.println(message);
        if(message.equals("Emergency Alert! Check patient immediately!"))
        {
            icon = R.drawable.staffoflife;
        }
        else if(message.equals("Food has been requested."))
        {
            icon = R.drawable.sandwich;
        }
        Context context = getApplicationContext();
        Notification notification = new Notification.Builder(context)
                .setContentTitle("SALM-Alert")
                .setContentText(message)
                .setSmallIcon(icon)
                .build();

        int SERVER_DATA_RECEIVED = 1;
        notificationManager.notify(SERVER_DATA_RECEIVED, notification);
    }
}