package com.vansuita.stockexplorer.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.vansuita.stockexplorer.R;
import com.vansuita.stockexplorer.bean.User;
import com.vansuita.stockexplorer.capsule.Credentials;
import com.vansuita.stockexplorer.retro.Retro;
import com.vansuita.stockexplorer.retrofit.HawkCalls;
import com.vansuita.stockexplorer.shared.Prefs;
import com.vansuita.stockexplorer.util.Util;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by jrvansuita on 19/10/17.
 */

public class Login extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        init();
        Util.clipboard(this, "");
    }


    private void init() {
        ButterKnife.bind(this);
        followFinger();

        User user = Prefs.get(Login.this).getUser();

        if (user != null) {
            edLogin.setText(user.getAccess());
            edPass.setText(user.getPass());
            loadUserAndGo(edLogin.getText().toString(), edPass.getText().toString());
        }
    }


    @BindView(R.id.logo)
    ImageView ivLogo;

    @BindView(R.id.input_user)
    EditText edLogin;

    @BindView(R.id.input_pass)
    EditText edPass;

    @OnClick(R.id.enter)
    public void onEnter() {
        String login = edLogin.getText().toString();
        String pass = edPass.getText().toString();

        if (login.isEmpty()){
            edLogin.setError(getString(R.string.user_not));
            return;
        }

        if (pass.isEmpty()){
            edPass.setError(getString(R.string.pass_not));
            return;
        }

        loadUserAndGo(login, pass);
    }

    private void loadUserAndGo(String userId, String pass) {
        HawkCalls.Retro.get(this).create(HawkCalls.class).login(new Credentials(userId, pass)).enqueue(new Retro<ResponseBody>() {
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("s", String.valueOf(t.getMessage()));
                edLogin.setError(getString(R.string.user_incorrect));
            }

            @Override
            public void onResponse(ResponseBody result) {
                try {
                    String response = result.string();

                    if (response == null || response.isEmpty()){
                        edLogin.setError(getString(R.string.login_invalid));
                    }else {
                        Prefs.get(Login.this).setUser(response);
                        go();
                    }

                } catch (IOException e) {
                    Log.i("s", String.valueOf(e.getMessage()));
                    edLogin.setError(getString(R.string.user_incorrect));
                }
            }
        });
    }

    private void go() {
        User user = Prefs.get(Login.this).getUser();

        if (user != null) {
            if (user.isActive()) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                edLogin.setError(getString(R.string.user_not_active));
            }
        }
    }


    private float dX;
    private float dY;

    public void followFinger() {
        ivLogo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:

                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        v.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

}
