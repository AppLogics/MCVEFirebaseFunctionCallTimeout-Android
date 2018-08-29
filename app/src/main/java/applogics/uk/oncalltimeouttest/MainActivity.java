package applogics.uk.oncalltimeouttest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FirebaseAuth fbAuth = FirebaseAuth.getInstance();
        final FirebaseFunctions fbFunctions = FirebaseFunctions.getInstance("europe-west1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button bCallFunction = findViewById(R.id.call_function);
        final TextView tv_login_status = findViewById(R.id.tv_login_status);
        final TextView tv_info = findViewById(R.id.tv_info);
        final EditText et_milli_seconds = findViewById(R.id.et_milli_seconds);
        fbAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                tv_login_status.setText("Logged in");
                bCallFunction.setEnabled(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tv_login_status.setText(e.getMessage());
            }
        });
        bCallFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bCallFunction.setEnabled(false);
                tv_info.setText("Calling Endpoint...");
                String sMilliSeconds = et_milli_seconds.getText().toString();
                Integer iMilliSeconds = new Integer(sMilliSeconds);
                Map<String, Object> data = new HashMap<>();
                data.put("timeout", iMilliSeconds);
                fbFunctions.getHttpsCallable("endpoint").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        bCallFunction.setEnabled(true);
                        return (String) task.getResult().getData();
                    }
                }).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String result) {
                        bCallFunction.setEnabled(true);
                        tv_info.setText(result);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        bCallFunction.setEnabled(true);
                        tv_info.setText(e.getMessage());
                    }
                });
            }
        });
    }
}
