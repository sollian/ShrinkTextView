package demo.sollian.com.shrinktextview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.sollian.shrinktext.ShrinkTextView;

public class MainActivity extends Activity {
    private ShrinkTextView vText;

    private String text = "1\n2\n3\n\n\n\n4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vText = findViewById(R.id.text);
        vText.setText(text);
    }

    public void click(View view) {
        vText.toggle();
    }
}
