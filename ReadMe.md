# 수업명

## 수업 내용

- BroadcastReceiver을 학습

## Code Review

### MainActivity

```Java
public class MainActivity extends AppCompatActivity {
    PermissionUtil pUtil;
    MyReceiver myReceiver;

    PermissionUtil.IPermissionGrant iGrant = new PermissionUtil.IPermissionGrant() {
        @Override
        public void run() {
            myReceiver = new MyReceiver();
        }

        @Override
        public void fail() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        pUtil = new PermissionUtil(100, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS});
        pUtil.check(this, iGrant);

    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        //Activity 를 실행할 때 Intent 를 만들어 StartActivity 를 호출하여 실행
/       //그러면 onCreate() 부터 onResume() 을 통해 호출->  Activity 가 실행
        //실행한 Activity 가 foreground 상태에서 Intent 에 Extra 값을 추가하고 StartActivity 를 호출-> onCraete() 대신에 onNewIntent(Intent intent) 가 호출이 되고 그 다음 onResume() 이 호출이 됨

    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(myReceiver, myReceiver.getIntentFilter());
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);

        super.onDestroy();
    }
}

```

### MyReceiver

```Java

public class MyReceiver extends BroadcastReceiver {
    private IntentFilter intentFilter;

    public MyReceiver(){
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case "android.provider.Telephony.SMS_RECEIVED":{
                Bundle bundle = intent.getExtras();

                Object[] objs = (Object[]) bundle.get("pdus");

                List<SmsMessage> messages = new ArrayList<>();
                for(Object obj : objs){
                    messages.add(SmsMessage.createFromPdu((byte[])obj));
                }

                for(SmsMessage message : messages) Log.d("onReceive", message.getMessageBody());
            }
        }
    }

    public IntentFilter getIntentFilter(){
        return intentFilter;
    }
}
```

### PermissionUtil

```Java
//..상략

 public interface IPermissionGrant{
        void run();
        void fail();
    }

```

## 보충설명

![브로드캐스트리시버](http://en.proft.me/media/android/android_broadcastreceiver.png)


> 4대 컴포넌트 중 하나로써 안드로이드 단말기에서 발생하는 이벤트 중 앱이 알아야 할 상황이 발생하면 방송해주고, 
앱은 수신기를 통해 해당 상황을 감지할 수 있다. 여기서 시스템이 방송한 정보를 수신할 수 있는 수신기가 바로 브로드캐스트 리시버 컴포넌트이다.
- 각종 앱에서 발생하는 방송을 캐치 후 리시버로 처리 할 수 있도록 해줌
- 방송하기 -> 수신하기
ex) 배터리 부족, SMS 문자메시지 등

### 정적 설정

- 한번등록하면 해제x

- 해당 앱이 설치될 떄 자동 등록

- 예제 (출처: http://thereclub.tistory.com/15 [아메리카노 공방])

1.클래스 작성

```Java
public class TestReceiver extends BroadcastReceiver {

@Override
public void onReceive(Context context, Intent intent) {
Toast.makeText(context, "Light On !!", Toast.LENGTH_SHORT).show();
}
}
```
2. manifest에 TestReceiver 등록
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest>

    <application>

        ...

        <receiver android:name=".TestReceiver">
            <intent-filter>
                <action android:name="com.test.action.TEST"/>
                <action android:name="android.intent.action.SCREEN_ON"/>
            </intent-filter>
        </receiver>

    </application>

</manifest>
```
3. 메니페스트에 TestReceiver 등록
```Java
@Override
protected void onCreate(Bundle savedInstanceState) {

super.onCreate(savedInstanceState);
setContentView(R.layout.activity_main);

Intent intent = new Intent();
intent.setAction("test.com.action.TEST");
sendBroadcast(intent);
}
```

### 동적 설정

- 등록과해제가 자유로움

1. 동적리시버 생성 및 등록

```Java
BroadcastReceiver receiver = null;

@Override
protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 인텐트 필터 설정
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction("test.com.action.TEST");

    // 동적리시버 생성
    receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Light On !!", Toast.LENGTH_SHORT).show();
        }
    };
    // 위에서 설정한 인텐트필터+리시버정보로 리시버 등록
    registerReceiver(receiver, intentFilter);

} 
```

2. 동적으로 등록한 리시버 호출

```Java
new Intent();
intent.setAction("test.com.action.TEST");
sendBroadcast(intent);
```




### + 인텐트 플래그를 활용한 리시버 동작제한

- 참고) 플래그는 Intent 클래스에 속하며 인텐트의 메타 데이터로써의 기능을 한다. 이것은 안드로이드 시스템이 액티비티를 어떻게 시작할 지(액티비티가 어떤 task에 속해야할 지 같은) 그리고 시작된 후에 어떻게 처리되어야 할 지(최근 사용 액티비티의 목록에 포함시킬 지 여부와 같은)를 지시하기도 한다.

- FLAG_EXCLUDE_STOPPED_PACKAGES  

앱이 한번이라도 실행됬을때만 리시버가 동작할 수 있도록 해준다.

API 12 이후로는 이 플래그는 기본으로 설정된다.

- FLAG_INCLUDE_STOPPED_PACKAGES

한번도 실해되지 않은 앱이라도 리시버가 동작하게 해준다.

- FLAG_RECEIVER_REGISTERD_ONLY

오직 동적리시버만 방송받을 수 있도록 해준다.

- FLAG_RECEIVER_REPLACE_PENDING

동일한 액션으로 중복해서 방송하는 경우 중복된 방송을 제거해준다.

### + 인텐트 필터

> 인텐트 필터(IntentFilter)는 특정 인텐트를 받을지 말지를 정의하는 역할을 수행하며, 이를 통해 컴포넌트의 특징이 정해짐.
![인텐트 필터](http://cfile23.uf.tistory.com/image/192A74254AD4DFD5103B54)

### 출처

- 출처: http://limkydev.tistory.com/49
- 출처: http://thereclub.tistory.com/15 [아메리카노 공방]
- 출처: http://diyall.tistory.com/786 [모든 것을 DIY 하자]
- 출처: http://androidhuman.com/262 [커니의 안드로이드 이야기]
- 출처: https://kairo96.gitbooks.io/android/content/ch2.8.html
- 출처: http://lectroid.tistory.com/5 [Lectroid]
- 출처: http://en.proft.me/2017/03/10/android-broadcastreceiver-tutorial/

## TODO

- 오류 분석 및 수정 필요

## Retrospect

- 간단한 브로드캐스트 리시버 예제는 이해가 되나, 이 예제는 조금 공부가 필요할 것 같다.(인텐트 필터, 브로드캐스트리시버 심화)


## Output
