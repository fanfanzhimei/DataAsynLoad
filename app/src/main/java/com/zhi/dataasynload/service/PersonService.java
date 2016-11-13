package com.zhi.dataasynload.service;

import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import com.zhi.dataasynload.domain.Person;
import com.zhi.dataasynload.utils.MD5;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/13.
 */
public class PersonService {

    /**
     * 从网络中获取数据，并解析成List<Person>输出
     * @return
     */
    public static List<Person> getPersons(String path) throws IOException, XmlPullParserException {
        URL url = new URL(path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        int state = connection.getResponseCode();
        if(200 == state){
            return parseData(connection.getInputStream());
        }
        return null;
    }

    private static List<Person> parseData(InputStream inputStream) throws XmlPullParserException, IOException {
        Person person = null;
        List<Person> persons = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inputStream, "UTF-8");
        int event = parser.getEventType();
        while(XmlPullParser.END_DOCUMENT != event){
            switch (event){
                case XmlPullParser.START_TAG:
                    if("persons".equals(parser.getName())){
                        persons = new ArrayList<Person>();
                        break;
                    }
                    if("person".equals(parser.getName())){
                        person = new Person();
                        person.setId(new Integer(parser.getAttributeValue(0)));
                        break;
                    }
                    if("name".equals(parser.getName())){
                        person.setName(parser.nextText());
                        break;
                    }
                    if("image".equals(parser.getName())){
                        person.setHeadImage(parser.getAttributeValue(0));
                        break;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if("person".equals(parser.getName())){
                        persons.add(person);
                        person = null;
                    }
                    break;
            }
            event = parser.next();
        }
        return persons;
    }

    /**
     * 获取图片的路径（缓存、网络图片存到本地的路径）
     * @param cache
     * @return
     */
    public static Uri getImagePath(File cache, String imagePath) throws IOException {
        File localFile = new File(cache, MD5.getMD5(imagePath)+imagePath.substring(imagePath.lastIndexOf(".")));
        if(localFile.exists()){ // 缓存文件存在
            return Uri.fromFile(localFile);
        }
        //  缓存文件不存在，就需要去网络上获取，并缓存在本地
        URL url = new URL(imagePath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        int state = connection.getResponseCode();
        if(200 == state){
            FileOutputStream fos = new FileOutputStream(localFile);
            InputStream is = connection.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            while((len = is.read(bytes))!=-1){
                fos.write(bytes, 0, len);
            }
            fos.close();
            is.close();
        }
        return Uri.fromFile(localFile);
    }
}