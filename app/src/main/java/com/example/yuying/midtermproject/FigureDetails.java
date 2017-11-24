package com.example.yuying.midtermproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;

/**
 * Created by Yuying on 2017/11/20.
 *人物详情界面
 */

public class FigureDetails extends AppCompatActivity {
    /*  上传pic  */
    private static final String TAG = "FigureDetails";
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static File  AppPath;
    protected static Uri tempUri;
    private ImageView pic_iv;
    private String imagePath;
    private Figure figure;


    Boolean isEdit = false;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.figure_details);
        Context context=this;
        AppPath=context.getFilesDir();
        pic_iv=(ImageView)findViewById(R.id.figure_pic);
        pic_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) showChoosePicDialog();
            }
        });


        final FigureRepo repo=new FigureRepo(this);
        final Intent intent=getIntent();
        figure=(Figure)intent.getSerializableExtra("figure");
        final int position =intent.getIntExtra("position",0);


        final EditText name_tv=(EditText)findViewById(R.id.figure_name);
        final EditText life_tv = (EditText)findViewById(R.id.figure_life);
        final EditText gender_tv = (EditText)findViewById(R.id.figure_gender);
        final EditText origin_tv = (EditText)findViewById(R.id.figure_origin);
        final EditText maincountry_tv = (EditText)findViewById(R.id.figure_maincountry);
        pic_iv=(ImageView)findViewById(R.id.figure_pic);
        name_tv.setText(figure.getName());
        origin_tv.setText(figure.getOrigin());
        gender_tv.setText(figure.getGender());
        life_tv.setText(figure.getLife());
        maincountry_tv.setText(figure.getMainCountry());


        if(figure.getPicPath()==null)
            pic_iv.setImageResource(figure.getPic());
        else {
            tempUri=Uri.fromFile(new File(figure.getPicPath()));
            pic_iv.setImageURI(tempUri);
            Toast.makeText(FigureDetails.this,"修改过图片", Toast.LENGTH_SHORT).show();
        }

        // 实现更新功能
        final ImageButton saveButton = (ImageButton)findViewById(R.id.save);
        final ImageButton backButton = (ImageButton) findViewById(R.id.back);
        final TextView editInfo = (TextView)findViewById(R.id.editInfo);
        // 初始化图标
        if(!isEdit) {
           saveButton.setVisibility(View.INVISIBLE);
            editInfo.setVisibility(View.INVISIBLE);
        }else {
            saveButton.setVisibility(View.VISIBLE);
            editInfo.setVisibility(View.VISIBLE);
        }


        final ImageButton editButton = (ImageButton)findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = true;
                saveButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.INVISIBLE);
                editInfo.setVisibility(View.VISIBLE);
                name_tv.setFocusableInTouchMode(true);
                name_tv.requestFocus();
                life_tv.setFocusable(true);
                life_tv.setFocusableInTouchMode(true);
                gender_tv.setFocusableInTouchMode(true);
                gender_tv.setFocusable(true);
                origin_tv.setFocusable(true);
                origin_tv.setFocusableInTouchMode(true);
                maincountry_tv.setFocusable(true);
                maincountry_tv.setSelection(figure.getMainCountry().length());
                maincountry_tv.setFocusableInTouchMode(true);
                maincountry_tv.requestFocus();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = false;
                saveButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.VISIBLE);
                editInfo.setVisibility(View.INVISIBLE);
                figure.setName(name_tv.getText().toString());
                figure.setLife(life_tv.getText().toString());
                figure.setGender(gender_tv.getText().toString());
                figure.setOrigin(origin_tv.getText().toString());
                figure.setMainCountry(maincountry_tv.getText().toString());
                name_tv.setFocusableInTouchMode(false);
                name_tv.setFocusable(false);
                life_tv.setFocusable(false);
                life_tv.setFocusableInTouchMode(false);
                gender_tv.setFocusableInTouchMode(false);
                gender_tv.setFocusable(false);
                origin_tv.setFocusable(false);
                origin_tv.setFocusableInTouchMode(false);
                maincountry_tv.setFocusable(false);
                maincountry_tv.setFocusableInTouchMode(false);
                repo.update(figure);
            }
        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent();
                intent.putExtra("position",position);
                setResult(1,intent);
                finish();
            }
        });




//        //删除
//        repo.delete(figure.getID());
//        //更新

        //repo.insert(figure)



    }


    /* 上传pic */
    public void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置头像");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        takePicture();
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void takePicture() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= 23) {
            // 需要申请动态权限
            int check = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (check != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), "image.jpg");

        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= 24) {
            openCameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            tempUri = FileProvider.getUriForFile(FigureDetails.this, "com.yuying.midtermproject.fileProvider", file);
        } else {
            tempUri = Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), "image.jpg"));
        }
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Log.d(TAG,"setImageToView:"+photo);
           // photo = ImageUtils.toRoundBitmap(photo); // 这个时候的图片已经被处理成圆形的了
            pic_iv.setImageBitmap(photo);
            uploadPic(photo);
        }
    }

    private void uploadPic(Bitmap bitmap) {
        // 上传至服务器
        // ... 可以在这里把Bitmap转换成file，然后得到file的url，做文件上传操作
        // 注意这里得到的图片已经是圆形图片了
        // bitmap是没有做个圆形处理的，但已经被裁剪了
       imagePath = ImageUtils.savePhoto(bitmap, AppPath.getAbsolutePath(), String
                .valueOf(System.currentTimeMillis()));
        Log.e("imagePath", imagePath+"");
        if(imagePath != null){
            // 拿着imagePath上传了
            // ...
            figure.setPicPath(imagePath);
            Toast.makeText(FigureDetails.this,"修改路径成功", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"imagePath:"+imagePath);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            // 没有获取 到权限，从新请求，或者关闭app
            Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show();
        }
    }
}

