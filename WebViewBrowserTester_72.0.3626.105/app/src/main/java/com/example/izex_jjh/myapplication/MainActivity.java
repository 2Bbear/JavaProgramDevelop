package com.example.izex_jjh.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private long pageStartTime = 0;
    private WebView wv;
    private ImageButton goBtn;
    private EditText et;

    //

    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wv = (WebView) findViewById(R.id.wv);
        goBtn = (ImageButton) findViewById(R.id.go);
        et = (EditText) findViewById(R.id.et);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        wv.setWebChromeClient(new WebChromeClient());

        //WebView세팅 작업
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true); //자바스크립트 허용
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); //캐시가 사용되는 방식을 재정의 합니다.
        /*
        * LOAD_NO_CACHE : 캐시를 사용않습니다. 네트워크에서 로드하게 됩니다.
        * LOAD_NORMAL : 이 상수는 APK 17이상에서 사용하지 않습니다.
        * LOAD_DEFAULT :  기본 캐시 사용 모드
        * LOAD_CACHE_ONLY : 캐시를 사용하여 네트워크를 하지 않습니다.
        * LOAD_CACHE_ELSE_NETWORK : 만료된 경우에도 캐시 된 리소스를 사용할 수 있을 때 사용하게 됩니다.
        * */
        settings.setAppCacheEnabled(false); //응용 프로그램에서 캐시 API를 사용해야 하는지 여부를 설정합니다.
        settings.setDomStorageEnabled(true); // DOM저장소 API를 사용하지 여부를 설정합니다.
        /*
        * DOM저장소란
        * XML Document -> XML Parser -> DOM -> Application
        * Dom인터페이스를 데이터 액세스 함수들에 대한 랩퍼로 구현함으로써 데이터 저장소를 DOM을 이용하여 액세스 할 수 있다.
        * */
        settings.setSupportZoom(true); // 화면 확대 축소 컨트롤 및 제스처를 사용하여 WebView에서 확대 축소를 지원해야 하는지 여부를 결정합니다.
        settings.setBuiltInZoomControls(true); //WebView가 내장 Zomm 매커니즘을 사용해야 하는지 여부를 결정합니다.
        //빌드 버전이 젤리빈 이상일경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);//파일 구성표 URL의 컨텍스트에서 실행중인 JavaScript가 모든 출처의 콘텐츠에 액세스 할 수 있어야하는지 결정합니다.
        }

        //WebView에 WebViewClient를 연결합니다. WebViewClient를 생성함과 동시에 onPageStarted메소드를 재정의하여 페이지가 새로 만들어지면 에디트 텍스트에 url 값을 넣을 수 있도록합니다.
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                et.setText(url);
            }
        });

        //WebView를 실행시켜 http에 접근합니다.
        wv.loadUrl("http://192.168.100.190:11014/login/login.do");

        // setup events
        goBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    goBtn.setColorFilter(getResources().getColor(android.R.color.holo_blue_dark));
                    return false;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    goBtn.setColorFilter(null);
                    return false;
                }
                return false;
            }
        });

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoadUrl(true);
            }
        });

        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    handleLoadUrl(false);
                }
            }
        });
    }

    //뒤로가기 버튼을 누를경우 히스토리에 저장되어 있는 url로 회귀합니다.
    @Override
    public void onBackPressed() {
        if (wv.canGoBack() == true) {
            wv.goBack();
        } else {
            MainActivity.super.onBackPressed(); // 만약 돌아갈 url이 없다면 뒤로가기 버튼을 무시합니다.
        }
    }

    /*
    load하는 url을 관리하는 메소드 입니다.
    * */
    private void handleLoadUrl(boolean forceReload) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);

        String url = et.getText().toString(); //에디트 텍스트에서 문자열을 읽어들입니다.
        //문자열 시작이 http가 아닐 경우 문자열 앞에 http관련 문자열을 붙여줍니다.
        if (url.startsWith("http://")) {
        } else if (url.startsWith("https://")) {
        } else {
            url = String.format("http://%s", url);
        }

        //ur????
        if (!url.equals(wv.getUrl()) || forceReload) {
            wv.loadUrl(url);
        }
    }
}
