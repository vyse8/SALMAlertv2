
package matthew_2.salmalert;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class AddEmergencyAlertFragment extends Fragment {


    private static final String ARG_ID = "com.matthew2.salmAlert.fragment.med.ID";

    public static final class Builder {

        private final Bundle mArgs = new Bundle();

        private Builder() {
        }

        public static Builder create(long id) {
            final Builder builder = new Builder();
            builder.mArgs.putLong(ARG_ID, id);
            return builder;
        }

        public AddEmergencyAlertFragment build() {
            final AddEmergencyAlertFragment fragment = new AddEmergencyAlertFragment();
            fragment.setArguments(mArgs);
            return fragment;
        }
    }

    private TextView mAddEmergencyAlertTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_emergencyalert, container, false);
        mAddEmergencyAlertTextView = (TextView) view.findViewById(R.id.add_emergencyalert_label);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToast();
            }
        });
    }

    private void sendToast() {
        if (MainWearActivity.nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MainWearActivity.client.blockingConnect(MainWearActivity.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(MainWearActivity.client, MainWearActivity.nodeId, "Emergency Alert! Check patient immediately!", null);
                    MainWearActivity.client.disconnect();
                }
            }).start();
        }
    }
}
