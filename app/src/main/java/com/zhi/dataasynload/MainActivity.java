package com.zhi.dataasynload;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import com.zhi.dataasynload.adapter.PersonAdapter;
import com.zhi.dataasynload.domain.Person;
import com.zhi.dataasynload.service.PersonService;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {
    private final static String DATA_URL = "http://192.168.1.5:8080/FileUpload/person.xml";
    private final static String DATA_CACHE = "cache";
    private List<Person> persons;
    private ListView mLvPerson;
    private File cachefile;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 20){
                cachefile = new File(Environment.getExternalStorageDirectory(), DATA_CACHE);
                if(!cachefile.exists()){
                   cachefile.mkdirs();
                }
                mLvPerson.setAdapter(new PersonAdapter(MainActivity.this, persons, cachefile));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLvPerson = (ListView) findViewById(R.id.lv_person);
        new PersonThread().start();
    }

    class PersonThread extends Thread{
        @Override
        public void run() {
            try {
                persons = PersonService.getPersons(DATA_URL);
                mHandler.sendMessage(mHandler.obtainMessage(20, persons));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(File file : cachefile.listFiles()){
            file.delete();  // 删除目录下的文件
        }
        cachefile.delete();  // 删除cache目录
    }
}