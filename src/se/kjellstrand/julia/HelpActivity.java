
package se.kjellstrand.julia;

import android.app.Activity;
import android.os.Bundle;

public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        //
        // auto-started on install and reachable from settings page.
        // - Mention pinch zoom and swipe and settings reached from apps.

    }
}
