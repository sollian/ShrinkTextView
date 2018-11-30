package demo.sollian.com.shrinktextview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sollian.shrinktext.ShrinkTextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        ShrinkTextView v = findViewById(R.id.text);
        v.toggle();
    }
}
