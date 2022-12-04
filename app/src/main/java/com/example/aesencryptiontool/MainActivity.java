package com.example.aesencryptiontool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private EditText message;
    private EditText key;
    private TextView encryptedMessage;
    private Button encryptButton;
    private RadioGroup modesRadioGroup;
    private RadioGroup paddingsRadioGroup;
    private String aesMode = "AES/CBC/";
    private String aesPadding = "ISO10126Padding";
    private RadioButton ISO10126P;
    private RadioButton PKCS5P;
    private RadioButton noPadding;
    private ImageView copyToClipboardButton;

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
        paddingsRadioGroup = findViewById(R.id.paddings_rGroup);
        ISO10126P = findViewById(R.id.iso10126P);
        PKCS5P = findViewById(R.id.pkcs5p);
        noPadding = findViewById(R.id.noP);
        copyToClipboardButton = findViewById(R.id.copyToClipboardButton);
        noPadding.setVisibility(View.GONE);
        ISO10126P.setChecked(true);
        encryptedMessage.setTextIsSelectable(true);
        copyToClipboardButton.setVisibility(View.GONE);
        modesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedRadioButton) {
                modeChanger(checkedRadioButton);
            }
        });
        paddingsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedPaddingRadioB) {
                paddingsChanger(checkedPaddingRadioB);
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
        copyToClipboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyEncryptedMessageToClipBoard();
            }
        });
    }

    private void encryptMessage() {
        try {

            byte[] plaintext = message.getText().toString().getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(key.getText().toString().getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(aesMode + aesPadding);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] ciphertext = cipher.doFinal(plaintext);
            encryptedMessage.setText(Base64.encodeToString(ciphertext, Base64.DEFAULT));
            copyToClipboardButton.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void modeChanger(int checkedRadioButton) {
        encryptedMessage.setText("");
        copyToClipboardButton.setVisibility(View.GONE);
        paddingsVisibility(checkedRadioButton);
        switch (checkedRadioButton) {
            case R.id.cbc_mode:
                aesMode = "AES/CBC/";
                break;
            case R.id.ecb_mode:
                aesMode = "AES/ECB/";
                break;
            case R.id.cfb_mode:
                aesMode = "AES/CFB/";
                break;
            case R.id.ctr_mode:
                aesMode = "AES/CTR/";
                break;
            case R.id.cts_mode:
                aesMode = "AES/CTS/";
                break;
            case R.id.gcm_mode:
                aesMode = "AES/GCM/";
                break;
            case R.id.ofb_mode:
                aesMode = "AES/OFB/";
                break;
        }
    }

    private void paddingsChanger(int checkedRadioPaddingButton) {
        encryptedMessage.setText("");
        copyToClipboardButton.setVisibility(View.GONE);
        switch (checkedRadioPaddingButton) {
            case R.id.iso10126P:
                aesPadding = "ISO10126Padding";
                break;
            case R.id.noP:
                aesPadding = "NoPadding";
                break;
            case R.id.pkcs5p:
                aesPadding = "PKCS5Padding";
                break;
        }

    }

    private void paddingsVisibility(int aesModeId) {
        if (aesModeId == R.id.gcm_mode) {
            ISO10126P.setVisibility(View.GONE);
            PKCS5P.setVisibility(View.GONE);
            noPadding.setVisibility(View.VISIBLE);
            noPadding.setChecked(true);
            aesPadding = "NoPadding";
        }
        else if(aesModeId == R.id.cts_mode || aesModeId == R.id.ecb_mode || aesModeId == R.id.cbc_mode){
            ISO10126P.setVisibility(View.VISIBLE);
            PKCS5P.setVisibility(View.VISIBLE);
            noPadding.setVisibility(View.GONE);
            ISO10126P.setChecked(true);
            aesPadding = "ISO10126Padding";
        }
        else {
            ISO10126P.setVisibility(View.VISIBLE);
            PKCS5P.setVisibility(View.VISIBLE);
            noPadding.setVisibility(View.VISIBLE);
            noPadding.setChecked(true);
            aesPadding = "NoPadding";
        }


    }

    private void copyEncryptedMessageToClipBoard(){
        ClipboardManager clipboard = (ClipboardManager)getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Encrypted message", encryptedMessage.getText());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

}