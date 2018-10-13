package com.example.adem.artbook;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    static SQLiteDatabase database;
    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

    imageView=(ImageView)findViewById(R.id.imageView);
    editText=(EditText)findViewById(R.id.editText);
        Button button=(Button)findViewById(R.id.button);

        Intent intent=getIntent();  //yeni eski karşılaştırması
        String info=intent.getStringExtra("info");

        if (info.equalsIgnoreCase("new")) //Stringlerde kontrol
        {
            Bitmap background=BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.background);
            imageView.setImageBitmap(background);
            button.setVisibility(View.VISIBLE);
            editText.setText(" ");
        }
        else {
            String name=intent.getStringExtra("info");  //old yani var olan şeyleri görüntülemek için
            editText.setText(name);
            int position=intent.getIntExtra("position",0);
            imageView.setImageBitmap(MainActivity.artImage.get(position));

            button.setVisibility((View.INVISIBLE));
        }






    }
    public void select(View view)
    {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED)  //izin verilmedi ise
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);

        }
        else //izin varsa
        {
            Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //gorsel seçmek için izin sağlama işlemleri
            startActivityForResult(intent,1); //data almak için forresult diyoruz
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    { //izin vermeyip daha sonra verdiyse
        if(requestCode==2)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //gorsel seçmek için izin sağlama işlemleri
                startActivityForResult(intent,1); //data almak için forresult diyoruz

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }






    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {//bunu yukarıda 1 diye belirledik. resim seçme koşulları için

            Uri image = data.getData();

            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                imageView.setImageBitmap(selectedImage);//seçilen resimi alıp koyuyoruz
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



        super.onActivityResult(requestCode, resultCode, data);
    }

    public void save(View view)
    {
        String artName=editText.getText().toString();

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);  //ziplemek
       byte[] byteArray=outputStream.toByteArray();

       try
       {

           database = this.openOrCreateDatabase("Arts", MODE_PRIVATE, null);
           database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR, image BLOB)");//int değilse var char değilse BLOB


           String sqlString = "INSERT INTO arts (name, image) VALUES (?, ?)";//veri ekleme böyle
           SQLiteStatement statement = database.compileStatement(sqlString);
           statement.bindString(1,artName);
           statement.bindBlob(2,byteArray);
           statement.execute();



       }
       catch (Exception e)
       {
           e.printStackTrace();
       }

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);  //saveleyip ana ekrana gitsin
        startActivity(intent);

    }



}
