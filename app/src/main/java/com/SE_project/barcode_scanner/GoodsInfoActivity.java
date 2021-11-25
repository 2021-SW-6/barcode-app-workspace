package com.SE_project.barcode_scanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class GoodsInfoActivity extends AppCompatActivity {
    TextView tv_barcode_number; //인식한 내용 출력되는 것 확인
    TextView tv_product_name;   //상품명 나중에 정보읽어와서 넣어주어야하는부분
    TextView tv_lowest_price;   //최저가 나중에 정보읽어와서 넣어주어야하는부분
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodsinfo);
        setTitle("상품 정보");

        Intent intent = getIntent();
        String barcodeNumber = intent.getStringExtra("barcodeNumber");
        tv_barcode_number = findViewById(R.id.barcodeNumber);
        tv_barcode_number.setText(barcodeNumber);

        Button btnWrong = findViewById(R.id.btnWrong);

        btnWrong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = View.inflate(GoodsInfoActivity.this, R.layout.dialog_wronggoods, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(GoodsInfoActivity.this);

                dlg.setTitle("기대하신 상품이 아닙니까?");
                dlg.setView(dialogView);
                dlg.setNeutralButton("오류보고", null);
                dlg.show();
            }
        });

    }
}
