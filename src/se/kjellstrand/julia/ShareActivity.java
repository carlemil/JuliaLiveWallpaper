package se.kjellstrand.julia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ShareActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.pref_share_text));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

        finish();
    }
}
