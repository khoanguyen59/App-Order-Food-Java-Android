package com.tandai.orderfood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class ThemMonActivity extends AppCompatActivity {
    private Button themMon,folder;
    private EditText tenMon,giaMon;
    private ImageView image;
    private int REQUEST_CODE_FOLDER = 123;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private String link ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_them_mon);
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://databaseorderfood.appspot.com");
        AnhXa();
        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE_FOLDER);
            }
        });

        themMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String ten  =   tenMon.getText().toString().trim();
                final String stringGia = giaMon.getText().toString().trim();
                final long gia    =   Long.parseLong(stringGia);
                if(ten.isEmpty()|| stringGia.isEmpty()){
                    Toast.makeText(ThemMonActivity.this, "Vui lòng nhập đầy đủ thông tin ", Toast.LENGTH_SHORT).show();
                }
                else{
                    Calendar calendar = Calendar.getInstance();
                    final String tenhinh="image"+calendar.getTimeInMillis();
                    final StorageReference mountainsRef = storageRef.child(tenhinh+".png");
                    image.setDrawingCacheEnabled(true);
                    image.buildDrawingCache();

                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    final UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // get downloadUrl
                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    link = mountainsRef.getDownloadUrl().toString();
                                    // Continue with the task to get the download URL
                                    return mountainsRef.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        link = task.getResult().toString();
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        String IDQuan = user.getUid();
                                        MonAn Mon= new MonAn(user.getDisplayName(),IDQuan,ten,link,gia,1);
                                        DatabaseReference refData = FirebaseDatabase.getInstance().getReference();
                                        refData.child("QuanAn").child(IDQuan).child(ten).setValue(Mon);
                                        Toast.makeText(ThemMonActivity.this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ThemMonActivity.this,QuanAnActivity.class));

                                    } else {
                                        Toast.makeText(ThemMonActivity.this, "fail", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });


                }

            }
        });


    }


    // chọn ảnh từ file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                image.setImageBitmap(bitmap);
            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void AnhXa(){
        themMon =(Button) findViewById(R.id.btnThemMonLayoutThemMon);
        folder  =(Button) findViewById(R.id.btnfolder);
        tenMon  =(EditText) findViewById(R.id.edtTenMon);
        giaMon  =(EditText) findViewById(R.id.edtGiaMon);
        image   = (ImageView) findViewById(R.id.ivImage);
    }



}
