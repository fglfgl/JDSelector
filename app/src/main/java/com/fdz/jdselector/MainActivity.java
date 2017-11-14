package com.fdz.jdselector;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText etDeep;
    private SelectorViewModel selectorViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        etDeep = findViewById(R.id.et_deep);
        selectorViewModel = new SelectorViewModel(this);
        findViewById(R.id.bt_show).setOnClickListener(v -> {
            if (etDeep.getText().length() > 0 && Integer.valueOf(etDeep.getText().toString()) > 0) {
                selectorViewModel.showSelector(Integer.valueOf(etDeep.getText().toString()));
            }
        });
    }
}
