package com.zhi.dataasynload.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhi.dataasynload.R;
import com.zhi.dataasynload.domain.Person;
import com.zhi.dataasynload.service.PersonService;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/11/13.
 */
public class PersonAdapter extends BaseAdapter {
    private Context context;
    private List<Person> persons;
    private File cache;

    public PersonAdapter(Context context, List<Person> persons, File cache) {
        this.context = context;
        this.persons = persons;
        this.cache = cache;
    }

    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Object getItem(int position) {
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_item_person, null);
            holder.mIvHeadImage = (ImageView) convertView.findViewById(R.id.iv_headImage);
            holder.mTvName = (TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.mTvName.setText(persons.get(position).getName());
        showHeadImage(holder.mIvHeadImage, persons.get(position).getHeadImage());
        return convertView;
    }

    public final static class Holder {
        ImageView mIvHeadImage;
        TextView mTvName;
    }

    private void showHeadImage(ImageView headImage, String path) {
        new PersonAsyncTask(headImage, path).execute();
    }

    class PersonAsyncTask extends AsyncTask<String, Integer, Uri> {
        ImageView headImage;
        String path;

        public PersonAsyncTask(ImageView headImage, String path) {
            this.headImage = headImage;
            this.path = path;
        }

        @Override
        protected Uri doInBackground(String... params) {
            try {
                Uri imagePath = PersonService.getImagePath(cache, path); // 获取图片路径涉及到网络处理，需要在子线程中完成
                return imagePath;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            headImage.setImageURI(uri);
        }
    }
}