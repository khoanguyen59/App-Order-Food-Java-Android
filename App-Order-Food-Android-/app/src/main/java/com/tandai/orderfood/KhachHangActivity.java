package com.tandai.orderfood;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.paperdb.Paper;

public class KhachHangActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView ten, tenTK;
    ListView lvFood;
    ArrayList<Food> arrFood;
    FoodAdapter1 adapter = null;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String userID = user.getUid();
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khachhang);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("MENU");

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Cart = new Intent(KhachHangActivity.this,CartActivity.class);
                startActivity(Cart);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Init paper
        Paper.init(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
        ten = (TextView) headerview.findViewById(R.id.ten);
        tenTK =( TextView) headerview.findViewById(R.id.tenTK);
        LoadData_User();

        lvFood  =   (ListView) findViewById(R.id.lvFood);
        arrFood = new ArrayList<>();
        adapter = new FoodAdapter1(this, R.layout.line_food, arrFood);
        lvFood.setAdapter(adapter);
        LoadData_Food();

        lvFood.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //position là vi tri tren listview
                Food food = arrFood.get(position);
                Intent foodDetail = new Intent(KhachHangActivity.this,FoodDetailActivity.class);
                //gửi FoodId (ten của Food) và id quán đến activity FoodDetail
                foodDetail.putExtra("FoodId",food.getTenMon());
                foodDetail.putExtra("RestaurentID",food.getIDQuan());
                // mở activity  foodDetail
                startActivity(foodDetail);
            }
        });

    }




    private void LoadData_User(){
        mDatabase  = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User uInfo = dataSnapshot.getValue(User.class);
                ten.setText(uInfo.getName());
                tenTK.setText(uInfo.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(eventListener);
    }


    private  void LoadData_Food(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("QuanAn");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    int i= 0;
                    for(DataSnapshot ds1: ds.getChildren()){
                        MonAn mon = ds1.getValue(MonAn.class);
                        arrFood.add(new Food(mon.getTenMon(),mon.getTenQuan(),mon.getLinkAnh(),mon.getIdQuan(),mon.getGiaMon(),mon.getTinhTrang()));
                        adapter.notifyDataSetChanged();
                        ++i;
                        if(i==3) break; // moi quan 3 mon
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(eventListener);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            startActivity(new Intent(KhachHangActivity.this,SearchFoodActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_xemthongtin) {
            startActivity(new Intent(KhachHangActivity.this,InfoPersonActivity.class));
        }
        else if (id == R.id.nav_timkiem) {
            startActivity(new Intent(KhachHangActivity.this, SearchFoodActivity.class));
        }
        else if (id == R.id.nav_donhang) {

        }
        else if (id == R.id.nav_giohang) {
            startActivity(new Intent(KhachHangActivity.this, CartActivity.class));
        }
        else if (id == R.id.nav_doimk) {
            startActivity(new Intent(KhachHangActivity.this,ChangePassActivity.class));
        }
        else if(id == R.id.nav_dangxuat) {

            //delete remember user and password
            Paper.book().destroy();

            // open dialog
            final Dialog dialogLogOut = new Dialog(KhachHangActivity.this);
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
                    startActivity(new Intent(KhachHangActivity.this,WelcomActivity.class));
                }
            });
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
