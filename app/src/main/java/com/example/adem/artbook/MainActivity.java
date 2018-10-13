package com.example.adem.artbook;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    static ArrayList<Bitmap> artImage;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //Bunu farklı bir metot olarak yazmamız gerek

        MenuInflater menuInflater=getMenuInflater(); //menuyu kullanmak için grekliolan obje
        menuInflater.inflate(R.menu.add_art,menu); //menuyu çıkar


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //menu secilirse ne olacak

        if(item.getItemId()==R.id.add_art)  //menu seçimi kontrolu
        {
            Intent intent=new Intent(getApplicationContext(),Main2Activity.class);
            intent.putExtra("info","new");  //anasayfada save butonu gözükmesin diye
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView=(ListView)findViewById(R.id.listView);

        final ArrayList<String> artName=new ArrayList<String>();
        artImage=new ArrayList<Bitmap>();

        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,artName);//listview ile bağlamak
        listView.setAdapter(arrayAdapter);

        try{

            Main2Activity.database=this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            Main2Activity.database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR,image BLOB)");

            Cursor cursor=Main2Activity.database.rawQuery("SELECT *FROM arts",null);

            int nameIx=cursor.getColumnIndex("name");
            int imageIx=cursor.getColumnIndex("image");

            cursor.moveToFirst();

            while (cursor!=null)
            {

                artName.add(cursor.getString(nameIx));

                byte[] byteArray=cursor.getBlob(imageIx);
                Bitmap image=BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                artImage.add(image);
                System.out.println("Name:" +cursor.getString(nameIx));
                cursor.moveToNext();
                arrayAdapter.notifyDataSetChanged();//data değiştiyse listViewde güncelleme yapar



            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                intent.putExtra("info", "old");
                intent.putExtra("name", artName.get(position));
                intent.putExtra("position", position);

                startActivity(intent);

            }
        });


    }



}
