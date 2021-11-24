package com.SE_project.barcode_scanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class GoodsInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodsinfo);
        setTitle("상품 정보");

        Intent intent = getIntent();

        Button btnWrong = findViewById(R.id.btnWrong);
        Button btnNo = findViewById(R.id.btnNo);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = View.inflate(GoodsInfoActivity.this, R.layout.dialog_doadditionalscan, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(GoodsInfoActivity.this);

                dlg.setTitle("추가 검색을 원하십니까?");
                dlg.setView(dialogView);
                dlg.setNeutralButton("취소", null);
                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent i_main = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i_main);
                    }
                });
                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                dlg.show();
            }
        });

        btnWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = View.inflate(GoodsInfoActivity.this, R.layout.dialog_wronggoods, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(GoodsInfoActivity.this);

                dlg.setTitle("기대하신 상품이 아닙니까?");
                dlg.setView(dialogView);
                dlg.setNeutralButton("오류보고", null);
                dlg.setPositiveButton("재촬영", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                dlg.show();
            }
        });

    }
}
