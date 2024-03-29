package ro.ase.proiect_draft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText email, passw;
    private Button login;
    private TextView newHere;
    private CheckBox rememberMe;
    SharedPreferences shp;
    SharedPreferences.Editor editor;
    private FirebaseAuth authFireBase;
    private DataSnapshot dataSnapshot = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.btnLogIn);
        newHere = findViewById(R.id.newHere);
        email = findViewById(R.id.email3);
        passw = findViewById(R.id.password3);
        rememberMe = findViewById(R.id.chbRemember);
        authFireBase = FirebaseAuth.getInstance();

        //Autentificare utilizator

        //Cand aplicatia se deschide va cauta fisierul sharedpreferances
        SharedPreferences shp = getSharedPreferences( " checkbox", MODE_PRIVATE);
        String checkbox = shp.getString("remember", "");

        if(checkbox.equals("true")){

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }else if(checkbox.equals("false")){

            Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show();
        }



        login.setOnClickListener(new View.OnClickListener() {
            //Preluare date existente din firebase
            @Override
            public void onClick(View v) {
                loginUserAccount();
            }
        });

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if( compoundButton.isChecked()){

                    SharedPreferences shp= getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = shp.edit();
                    editor.putString("rememberMe", "true");
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Checked", Toast.LENGTH_SHORT).show();

                } else if( !compoundButton.isChecked()){

                    SharedPreferences shp= getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = shp.edit();
                    editor.putString("rememberMe", "false");
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        newHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUserAccount() {

        String emailEt = email.getText().toString().trim();
        String passwordEt = passw.getText().toString().trim();

        //Validare email, parola
        if (TextUtils.isEmpty(emailEt)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter email!!",
                    Toast.LENGTH_LONG)
                    .show();
        }

        if (TextUtils.isEmpty(passwordEt)) {
            Toast.makeText(getApplicationContext(),
                    "Please enter password!!",
                    Toast.LENGTH_LONG)
                    .show();
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailEt).matches()){
            Toast.makeText(this, "Provide a valid email",
                    Toast.LENGTH_SHORT).show();
        }

        if(passwordEt.length() < 6)
            Toast.makeText(this, "Password should be at least 6 characters!",
                    Toast.LENGTH_SHORT).show();

        authFireBase.signInWithEmailAndPassword(emailEt, passwordEt)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>(){

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                         }else{

                            Toast.makeText(LoginActivity.this,
                                    "Failed to login! Please check your credentials!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
