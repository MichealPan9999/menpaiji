package cn.ktc.meeting.serverapi;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.ktc.jkf.serverapi.app.api.LoginApi;
import cn.ktc.jkf.utils.EnvironmentInfo;
import cn.ktc.jkf.utils.persistence.FilePersistUtil;
import cn.ktc.jkf.utils.persistence.IFilePersist;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private Gson gson = new GsonBuilder()
            //.setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private static final String JSON_LoginResult = "{\n" +
            "    \"code\": 0,\n" +
            "    \"message\": \"message\",\n" +
            "    \"uid\": 123,\n" +
            "    \"username\":\"username\",\n" +
            "    \"token\": \"xxxx\",\n" +
            "    \"expired\": 100025,\n" +
            "    \"userinfo\": {\n" +
            "        \"uid\"=234,\n" +
            "        \"username\": \"xxx\"\n" +
            "    }\n" +
            "}";

    private List<String> testList = new ArrayList<>();

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Log.i("AAA", "TEST MODE START ++++++++++." + SystemClock.uptimeMillis());
        String ktcDeviceId = EnvironmentInfo.getKtcDeviceId();
        if (TextUtils.isEmpty(ktcDeviceId)) {
            EnvironmentInfo.setDeviceId(EnvironmentInfo.newUUID(""));
        }
        Log.i("AAA", "deviceId=" + EnvironmentInfo.getKtcDeviceId());
/*
        LoginResult loginResult = JsonHelper.fromJson(JSON_LoginResult, LoginResult.class);
        assertEquals(0, loginResult.getCode());
        assertEquals("message", loginResult.getMessage());
        assertEquals(123, loginResult.getUid());
        assertEquals("username", loginResult.getUserName());
        assertEquals(100025, loginResult.getExpired());

        assertEquals(234, loginResult.getUserInfo().getUid());
        assertEquals("xxx", loginResult.getUserInfo().getUserName());
        //assertEquals("cn.ktc.meeting.serverapi.test", appContext.getPackageName());
*/
        //
        Log.i("AAA", "Start Horion Result test ....");
/*
        testList.add("HorionWeb");
        HorionTestApi horionTestApi = new HorionTestApi();
        horionTestApi.login("5bf37c53ba9aa", horionTestApiIRequestApiCallback);
*/

        //Rx调用logout
        if (false) {
            testList.add("UserName+Pwd Login");
            LoginApi loginApi = new LoginApi("http://127.0.0.1:8080");

        }
//        //获取组织架构（部门）
//        if (false) {
//            testList.add("Company+groups");
//            CompanyApi api = new CompanyApi("http://127.0.0.1:8080");
//            api.getApi().groups2(0, -1)
//                    .subscribeOn(Schedulers.newThread())
//                    .subscribe(result -> {
//                        //请求成功
//                        Log.i("AAA", "groups successful =" + gson.toJson(result));
//                    }, e -> {
//                        Log.i("AAA", "groups onError ...");
//                        testList.remove(0);
//                    }, () -> {
//                        Log.i("AAA", "groups onCompleted ...TID=" + Thread.currentThread().getId());
//                        testList.remove(0);
//                    });
//        }
//
//        //获取所有员工
//        if (false) {
//            testList.add("Company+groups");
//            CompanyApi api = new CompanyApi("http://127.0.0.1:8080");
//            api.getApi().members2(0, -1, -1)
//                    .subscribeOn(Schedulers.newThread())
//                    .subscribe(result -> {
//                        //请求成功
//                        Log.i("AAA", "members successful =" + gson.toJson(result));
//                    }, e -> {
//                        Log.i("AAA", "members onError ...");
//                        testList.remove(0);
//                    }, () -> {
//                        Log.i("AAA", "members onCompleted ...TID=" + Thread.currentThread().getId());
//                        testList.remove(0);
//                    });
//        }
        //生产公司的组织架构树（包括部门和员工）
        if (false) {
            testList.add("Company+groups+tree");

        }

        if (true) {
            testList.add("GetTempUser ++");

        }
        long time = SystemClock.uptimeMillis();
        while (testList.size() > 0
                && (SystemClock.uptimeMillis() - time < 30_000)) {
            SystemClock.sleep(100);
        }
        if (testList.size() > 0) {
            Log.i("AAA", "Test HorionResult timeout ....");
        }
//        testContacts();
        //testRooms();
        Log.i("AAA", "TEST MODE END ----------." + SystemClock.uptimeMillis() + ", TID=" + Thread.currentThread().getId());
    }


    private static final String JSON_groups = "{\n" +
            "  \"code\": 0,\n" +
            "  \"start\": 0,\n" +
            "  \"next\":-1,\n" +
            "  \"groups\": [\n" +
            "    {\n" +
            "      \"name\": \"深圳康冠科技集团\",\n" +
            "      \"id\": 0,\n" +
            "      \"pid\": -1\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"信息信息管理部\",\n" +
            "      \"id\": 101,\n" +
            "      \"pid\": 0\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"财务处\",\n" +
            "      \"id\": 201,\n" +
            "      \"pid\": 0\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"审计部\",\n" +
            "      \"id\": 2001,\n" +
            "      \"pid\": 201\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    private static final String JSON_members = "{\n" +
            "  \"code\": 0,\n" +
            "  \"start\": 0,\n" +
            "  \"next\": -1,\n" +
            "  \"members\": [\n" +
            "    {\n" +
            "      \"userinfo\": {\n" +
            "        \"uid\": 0,\n" +
            "        \"username\": \"董事长\",\n" +
            "        \"icon\": \"http://xxxx\"\n" +
            "      },\n" +
            "      \"gid\": 0,\n" +
            "      \"permissions\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"userinfo\": {\n" +
            "        \"uid\": 1,\n" +
            "        \"username\": \"总裁\"\n" +
            "      },\n" +
            "      \"gid\": 0,\n" +
            "      \"permissions\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"userinfo\": {\n" +
            "        \"uid\": 2,\n" +
            "        \"username\": \"财务总监\"\n" +
            "      },\n" +
            "      \"gid\": 201,\n" +
            "      \"permissions\": 1\n" +
            "    }\n" +
            "  ]\n" +
            "}";





    private static final String JSON_rooms = "{\n" +
            "  \"code\": 0,\n" +
            "  \"groups\": [\n" +
            "    {\n" +
            "      \"id\": 0,\n" +
            "      \"name\": \"KTC会议室\",\n" +
            "      \"pid\": -1\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 1,\n" +
            "      \"name\": \"总部会议室\",\n" +
            "      \"pid\": 0\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 101,\n" +
            "      \"name\": \"总部一楼会议室\",\n" +
            "      \"pid\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 201,\n" +
            "      \"name\": \"惠南会议室\",\n" +
            "      \"pid\": 0\n" +
            "    }\n" +
            "  ],\n" +
            "  \"rooms\": [\n" +
            "    {\n" +
            "      \"id\": 0,\n" +
            "      \"gid\": 1,\n" +
            "      \"name\": \"一楼会议室1\",\n" +
            "      \"permissions\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 103,\n" +
            "      \"gid\": 1,\n" +
            "      \"name\": \"一楼会议室2\",\n" +
            "      \"permissions\": 1\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 203,\n" +
            "      \"gid\": 201,\n" +
            "      \"name\": \"惠南惠南生产车间\",\n" +
            "      \"permissions\": 1\n" +
            "    }\n" +
            "  ]\n" +
            "}";





    private AtomicInteger ID_RUNNALB = new AtomicInteger(0);
    public void testFilePersist() {
//        //先写入一些存储值
//        IFilePersist persist = FilePersistUtil.open(null);
//        for (int i = 0; i < 100; i++) {
//            persist.save("[" + i + "]", String.valueOf(i * 10));
//        }
//        persist.close();

        //开启多个线程，用来读取和写入
        for (int i = 0; i < 10; i++) {
//            new Thread(runnableRead).start();
//            new Thread(runnableWrite).start();
            new Thread(runnableWrite2).start();
        }
    }

    private Runnable runnableRead = new Runnable() {
        @Override
        public void run() {
            final int id = ID_RUNNALB.incrementAndGet();
            //Log.i("HQ", "READ ID=" + id);
            String key = "[" + (SystemClock.uptimeMillis() % 100) + "]";
            IFilePersist persist = FilePersistUtil.open(null);
            String value = persist.read(key);
            persist.close();
            Log.i("HQ", "READ ID=" + id + ", RESULT: " + key + " = " + value);
        }
    };

    private Runnable runnableWrite = new Runnable() {
        @Override
        public void run() {
            final int id = ID_RUNNALB.incrementAndGet();
            //Log.i("HQ", "WRITE ID=" + id);
            int index = (int) (SystemClock.uptimeMillis() % 100);
            String key = "[" + index + "]";
            IFilePersist persist = FilePersistUtil.open(null);
            String value = persist.read(key, "1");
            Integer ivalue = Integer.valueOf(value);
            ivalue *= 100000;
            persist.save(key, String.valueOf(ivalue));
            persist.close();
            Log.i("HQ", "WRITE ID=" + id + ", RESULT: " + key + " = " + ivalue);
        }
    };
    private Runnable runnableWrite2 = new Runnable() {
        @Override
        public void run() {
            final int id = ID_RUNNALB.incrementAndGet();
            //final String key = "[24]";
            final String key = "[测试的key\"&:]";
            final IFilePersist persist = FilePersistUtil.open(null);
            final String value = persist.read(key, "1");
            final Integer ivalue = Integer.valueOf(value) + 1;
            persist.save(key, String.valueOf(ivalue));
            Log.i("HQ", "WRITE ID=" + id + ", RESULT: " + key + " = " + ivalue + " <------ " + value);
            persist.close();
        }
    };
}
