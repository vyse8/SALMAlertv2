
package matthew_2.salmalert;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.DelayedConfirmationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

/**
 * Fragment that allows to add some notes to a book.
 * <p/>
 * Created by Simone Casagranda on 25/01/15.
 */
public class AddHeartRateFragment extends Fragment implements DelayedConfirmationView.DelayedConfirmationListener {

    /**
     * Tag used for logging.
     */
    private static final String TAG_LOG = AddHeartRateFragment.class.getSimpleName();

    /**
     * Arguments params.
     */
    private static final String ARG_ID = "com.alchemiasoft.books.fragment.book.ID";

    /**
     * Code use to request the free-form speech
     */
    private static final int REQUEST_NOTES_CODE = 23;

    /**
     * Timeout delay for confirmation.
     */
    private static final long DELAY_TIMEOUT = 2000L;

    /**
     * Allows to build BookInfoCardFragment in an extensible way.
     */
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

    private DelayedConfirmationView mConfirmationView;
    private TextView mAddHeartRatesTextView;

    private boolean mIsAnimating = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_heartrate, container, false);
        mConfirmationView = (DelayedConfirmationView) view.findViewById(R.id.confirm_notes);
        mAddHeartRatesTextView = (TextView) view.findViewById(R.id.add_heartrate_label);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // The entire "screen" is listening for the click
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsAnimating) {
                    mConfirmationView.setImageResource(R.drawable.ic_action_notes);
                    mIsAnimating = false;
                    return;
                }
                final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.google.android.apps.fitness");
                startActivity(launchIntent);
                //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.heartRate_title));
                //startActivityForResult(intent, REQUEST_NOTES_CODE);
            }
        });
        mConfirmationView.setTotalTimeMs(DELAY_TIMEOUT);
        // Registering the listener triggered by the DelayedConfirmationView
        mConfirmationView.setListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_NOTES_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    final List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    final String voiceNotes = results.get(0);
                    mAddHeartRatesTextView.setText(voiceNotes);
                    mIsAnimating = true;
                    mConfirmationView.setImageResource(R.drawable.ic_full_cancel);
                    mConfirmationView.start();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onTimerFinished(View view) {
        final Activity activity = getActivity();
        if (activity == null) {
            Log.e(TAG_LOG, "Fragment not attached anymore to the activity (Skipping action).");
            return;
        }
        // Updating the book state
        //final long bookId = getArguments().getLong(ARG_ID);
        //final String notes = mAddHeartRatesTextView.getText().toString();
        // Scheduling the job in on the BookService
        //BookService.Invoker.get(activity).bookId(bookId).notes(notes).invoke();
        // We want to close the wear app
        activity.finish();
    }

    @Override
    public void onTimerSelected(View view) {
        mAddHeartRatesTextView.setText(R.string.heartRate_title);
        mConfirmationView.reset();
    }
}
