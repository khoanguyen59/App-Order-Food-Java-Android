package com.tandai.orderfood;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoPersonActivity extends AppCompatActivity {
    private Button doiMK;
    private ImageView logout, home;
    private TextView ten, tenTK, diachi, sdt;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_info_person);
        AnhXa();
        LoadData();
        doiMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoPersonActivity.this,ChangePassActivity.class));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DangXuat();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfoPersonActivity.this, KhachHangActivity.class));
            }
        });

    }

    private void AnhXa(){
        doiMK = (Button) findViewById(R.id.btndoipassKhachhang);
        ten     = (TextView) findViewById(R.id.tvtenKhachHang);
        tenTK     = (TextView) findViewById(R.id.tvtentaikhoan);
        diachi     = (TextView) findViewById(R.id.tvdiachikhachhang);
        sdt     = (TextView) findViewById(R.id.tvsdtkhachhang);
        logout    =(ImageView) findViewById(R.id.ivlogout);
        home    =(ImageView) findViewById(R.id.ivhome);
    }

    private void LoadData(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User uInfo = dataSnapshot.getValue(User.class);
                ten.setText("Tên khách hàng: "+ uInfo.getName());
                tenTK.setText("Tên tài khoản: "+uInfo.getEmail());
                diachi.setText("Địa chỉ: "+uInfo.getAddress());
                sdt.setText("Số điện thoại: "+uInfo.getPhone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(eventListener);
    }

    private void DangXuat(){
        final Dialog dialogLogOut = new Dialog(InfoPersonActivity.this);
        dialogLogOut.setContentView(R.layout.dialog_dang_xuat);
        dialogLogOut.show();
        Button khong=(Button) dialogLogOut.findViewById(R.id.btnKhongDialogDangXuat);
        Button thoat=(Button) dialogLogOut.findViewById((R.id.btnDialogDangXuat));
        khong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLogOut.cancel();
            }
        });
        thoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLogOut.cancel();
                startActivity(new Intent(InfoPersonActivity.this,WelcomActivity.class));
            }
        });
    }
}
