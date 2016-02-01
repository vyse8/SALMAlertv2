
package matthew_2.salmalert;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.DelayedConfirmationView;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class AddHeartRateFragment extends Fragment implements SensorEventListener {

    private static final float FALL_THRESHOLD = 1.1f;
    private static final int FALL_WAIT_TIME_MS = 250;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int mSensorType;
    private long mFallTime = 0;

    private static final String ARG_ID = "com.matthew2.salmAlert.fragment.med.ID";

    @Override
    public void onSensorChanged(SensorEvent event) {
        System.out.println(
                "z = " + Float.toString(event.values[2]) + "\n"
        );
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectFall(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void detectFall(SensorEvent event) {
        long now = System.currentTimeMillis();

        if((now - mFallTime) > FALL_WAIT_TIME_MS) {
            mFallTime = now;

            float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

            float gForce = FloatMath.sqrt(gX * gX + gY * gY + gZ * gZ);

            if(gForce > FALL_THRESHOLD) {
                System.out.println("Fall detected");
                sendToast();
            }

        }
    }

    public static final class Builder {

        private final Bundle mArgs = new Bundle();

        private Builder() {
        }

        public static Builder create(long id) {
            final Builder builder = new Builder();
            builder.mArgs.putLong(ARG_ID, id);
            return builder;
        }

        public AddHeartRateFragment build() {
            final AddHeartRateFragment fragment = new AddHeartRateFragment();
            fragment.setArguments(mArgs);
            return fragment;
        }
    }

    private TextView mAddHeartRatesTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_heartrate, container, false);
        mAddHeartRatesTextView = (TextView) view.findViewById(R.id.add_heartrate_label);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if(args != null) {
            mSensorType = args.getInt("sensorType");
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.google.android.apps.fitness");
                startActivity(launchIntent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void sendToast() {
        if (MainWearActivity.nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MainWearActivity.client.blockingConnect(MainWearActivity.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(MainWearActivity.client, MainWearActivity.nodeId, "The patient has fallen!", null);
                    MainWearActivity.client.disconnect();
                }
            }).start();
        }
    }
}
