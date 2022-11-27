package com.example.aesencryptiontool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private EditText message;
    private EditText key;
    private EditText encryptedMessage;
    private Button encryptButton;
    private RadioGroup modesRadioGroup;
    private String aesMode = "AES/CBC/ISO10126Padding";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.background));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//
        }
        setContentView(R.layout.activity_main);

        message = findViewById(R.id.messageId);
        key = findViewById(R.id.keyId);
        encryptedMessage = findViewById(R.id.encryptedMessageId);
        encryptButton = findViewById(R.id.encryptButtonId);
        modesRadioGroup = findViewById(R.id.modes_rGroup);


        modesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedRadioButton) {
                modeChanger(checkedRadioButton);
            }
        });

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((message.getText().toString().equals(null) || message.getText().toString().equals("")) || (key.getText().toString().equals(null) || key.getText().toString().equals(""))) {
                    Toast.makeText(getApplicationContext(), "Message or secret can't be empty", Toast.LENGTH_LONG).show();
                } else {
                    encryptMessage();
                }
            }
        });
    }

    private void encryptMessage() {
        try {

            byte[] plaintext = message.getText().toString().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key.getText().toString().getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(aesMode);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] ciphertext = cipher.doFinal(plaintext);
            encryptedMessage.setText(Base64.encodeToString(ciphertext, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modeChanger(int checkedRadioButton) {
        encryptedMessage.setText("");

        switch (checkedRadioButton) {
            case R.id.cbc_mode:
                aesMode = "AES/CBC/ISO10126Padding";
                break;
            case R.id.ecb_mode:
                aesMode = "AES/ECB/PKCS5Padding";
                break;
        }
    }

    public void clearInputs() {
        message.setText("");
        key.setText("");
        encryptedMessage.setText("");
    }

}