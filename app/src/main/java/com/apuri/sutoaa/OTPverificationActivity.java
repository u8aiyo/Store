package com.apuri.sutoaa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
//////////////////////////////
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
/*import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;*/

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

//import es.dmoral.toasty.Toasty;
////////////////////////////////////

public class OTPverificationActivity extends AppCompatActivity {

    private TextView mobileNo;
    private EditText otp;
    private Button verifyBtn;
    private String userNo;

    ///////////////// MY NEW
    private Spinner spinner;
    private EditText editText;
    //private FirebaseAuth firebaseAuth;
    private String verificationId;
    ///////////////// MY NEW

    String OrderID = getIntent().getStringExtra("OrderID");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        mobileNo = findViewById(R.id.phone_no);
        otp = findViewById(R.id.otp);
        verifyBtn = findViewById(R.id.verify_btn);

        //userNo = getIntent().getStringExtra("mobileNo");
        //mobileNo.setText("Verification code has been sent to +20 " + userNo);

        /*userNo = FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_ADDRESSES").get().toString();*/
        /*editText = findViewById(R.id.editTextPhone);
        userNo = editText.getText().toString();*/

        /*Random random = new Random();
        final int OTP_no = random.nextInt(999999 - 111111) + 111111;
        String SMS_API = "https://www.fast2sms.com/dev/bulk";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                verifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(otp.getText().toString().equals(String.valueOf(OTP_no))){

                            Map<String, Object> updateStatus=new HashMap<>();
                            updateStatus.put("Order Status","Ordered");
                            final String order_id=getIntent().getStringExtra("order_id");

                            FirebaseFirestore.getInstance().collection("ORDERS").document(order_id) //OrderID
                                    .update(updateStatus)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Map<String, Object> userOrder = new HashMap<>();
                                                userOrder.put("order_id", order_id);
                                                userOrder.put("time", FieldValue.serverTimestamp());
                                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_id)
                                                        .set(userOrder)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    //DeliveryActivity.ordered = true;  //my code
                                                                    //DeliveryActivity.codOrderConfirmed = true;
                                                                    finish();
                                                                }else {
                                                                    Toast.makeText(OTPverificationActivity.this, "Failed to update user orders list!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }else {
                                                Toast.makeText(OTPverificationActivity.this, "Order Cancelled!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                        }else {
                            Toast.makeText(OTPverificationActivity.this, "OTP incorrect!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(OTPverificationActivity.this,"Failed to send OTP verification code!",Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("authorization","B5WzCKYbZuvV7tdGwga2MDs431Oj8xIlciqA9USFQeonfkh6mROPywVDBz6qv2muHjaT1Afibx9QIsRX");

                return headers;

            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<String, String>();
                body.put("sender_id","FSTSMS");
                body.put("language","english");
                body.put("route","qt");
                body.put("numbers",userNo);
                body.put("message","23115");
                body.put("variables","{#BB#}");
                body.put("variables_values", String.valueOf(OTP_no));
                return body;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue=Volley.newRequestQueue(OTPverificationActivity.this);
        requestQueue.add(stringRequest);*/

        ///////////////// MY NEW
        /*firebaseAuth = FirebaseAuth.getInstance();
        spinner = findViewById(R.id.spinnerCountries);
        spinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));
        editText = findViewById(R.id.editTextPhone);

        findViewById(R.id.verify_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
                String number = editText.getText().toString().trim();
                if (number.isEmpty() || number.length() < 10) {
                    editText.setError("Valid number is required");
                    editText.requestFocus();
                    return;
                }

                String phonenumber = "+" + code + number;


                if ((code.isEmpty() || code.length() < 6)){

                    editText.setError("Enter code...");
                    editText.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });*/
        ///////////////// MY NEW
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }*/
    }

    private void verifyCode(String code) {
        //PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        //signInWithCredential(credential);
    }

    /*private void signInWithCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(OTPverificationActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);

                        } else {
                            Toast.makeText(OTPverificationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }*/

    private void sendVerificationCode(String number){

        /*PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );*/
    }

    /*private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                //progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OTPverificationActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();

        }
    };*/

}