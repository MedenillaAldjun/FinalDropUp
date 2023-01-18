package com.group.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    private EditText code1, code2, code3, code4, code5, code6;
    private String verification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        TextView textMobile = findViewById(R.id.textMobile);
        textMobile.setText(String.format(
                "+63-%s", getIntent().getStringExtra("Mobile")
        ));

        code1 = findViewById(R.id.inputcode1);
        code2 = findViewById(R.id.inputcode2);
        code3 = findViewById(R.id.inputcode3);
        code4 = findViewById(R.id.inputcode4);
        code5 = findViewById(R.id.inputcode5);
        code6 = findViewById(R.id.inputcode6);

        setupOTPInputs();

        final ProgressBar otp_pb = findViewById(R.id.otp_pb);
        final Button btn_verify = findViewById(R.id.btn_resend_otp);

        verification = getIntent().getStringExtra("verification");

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(code1.getText().toString().trim().isEmpty()
                || code2.getText().toString().trim().isEmpty()
                || code3.getText().toString().trim().isEmpty()
                || code4.getText().toString().trim().isEmpty()
                || code5.getText().toString().trim().isEmpty()
                || code6.getText().toString().trim().isEmpty()){
                    Toast.makeText(VerifyOTP.this, "Please enter a valid code", Toast.LENGTH_SHORT).show();
                }
                String code =
                        code1.getText().toString() +
                                code2.getText().toString() +
                                    code3.getText().toString() +
                                        code4.getText().toString() +
                                            code5.getText().toString() +
                                                code6.getText().toString();

                if(verification != null){
                    otp_pb.setVisibility(View.VISIBLE);
                    btn_verify.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verification,
                            code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            otp_pb.setVisibility(View.VISIBLE);
                            btn_verify.setVisibility(View.INVISIBLE);
                            if(task.isSuccessful()){
                                Intent intent = new Intent(getApplicationContext(), SetUp.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else{
                                Toast.makeText(VerifyOTP.this, "Verification code invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        
        findViewById(R.id.text_resend_otp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+63" + getIntent().getStringExtra("moble"),
                        60,
                        TimeUnit.SECONDS,
                        VerifyOTP.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newverification, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(newverification, forceResendingToken);
                                verification = newverification;
                                Toast.makeText(VerifyOTP.this, "OTP Sent", Toast.LENGTH_SHORT).show();

                            }
                        }
                );
            }
        });
    }
    private void setupOTPInputs(){
        code1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    code2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        code2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    code3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        code3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    code4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        code4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    code5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        code5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    code6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}