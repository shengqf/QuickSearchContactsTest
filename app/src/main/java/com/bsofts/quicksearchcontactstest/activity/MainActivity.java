package com.bsofts.quicksearchcontactstest.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bsofts.quicksearchcontactstest.Constants;
import com.bsofts.quicksearchcontactstest.R;
import com.bsofts.quicksearchcontactstest.adapter.CommonAdapter;
import com.bsofts.quicksearchcontactstest.adapter.ViewHolder;
import com.bsofts.quicksearchcontactstest.model.Person;
import com.bsofts.quicksearchcontactstest.util.PinyinUtils;
import com.bsofts.quicksearchcontactstest.view.QuickIndexBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private QuickIndexBar mBar;
    private TextView mCenterTv;

    private CommonAdapter<Person> mAdapter;
    private List<Person> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setClick();
        initData();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerview);
        mBar = findViewById(R.id.bar);
        mCenterTv = findViewById(R.id.tv_center);

        mAdapter = new CommonAdapter<Person>(this,R.layout.item,mList) {
            @Override
            protected void convert(ViewHolder holder, final Person person, int position) {
                holder.setText(R.id.tv_name, person.name);
                holder.setText(R.id.tv_phone, person.phoneNum);

                //首字母相关
                String letter = null;
                String currentLetter = PinyinUtils.getPinyin(person.name).charAt(0) + "";
                if (position == 0) {
                    letter = currentLetter;
                } else {
                    String preContactsVOLetter = PinyinUtils.getPinyin(mList.get(position - 1).name).charAt(0) + "";
                    if (!preContactsVOLetter.equals(currentLetter)) {
                        letter = currentLetter;
                    }
                }
                if (letter == null) {
                    holder.setVisible(R.id.tv_letter, false);
                } else {
                    holder.setVisible(R.id.tv_letter, true);
                }
                holder.setText(R.id.tv_letter, letter);

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("tel:"+person.phoneNum);
                        Intent it = new Intent(Intent.ACTION_DIAL, uri);
                        startActivity(it);
                    }
                });
            }
        };
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setClick() {
        mBar.setOnLetterUpdataListener(new QuickIndexBar.OnLetterUpdataListener() {
            @Override
            public void UpdataLetter(String letter) {
                mBar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.light_gray));
                mCenterTv.setVisibility(View.VISIBLE);
                mCenterTv.setText(letter);
                for (int i = 0; i < mList.size(); i++) {
                    Person person = mList.get(i);
                    String tempLetter = PinyinUtils.getPinyin(person.name).charAt(0)+"";
                    if (TextUtils.equals(tempLetter, letter)) {
                        scrollToPosition(i); //使item滑到recyclerview可见区域的第一个位置
                        break;
                    }
                }
            }

            @Override
            public void InvisiableTextView() {
                mBar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.transparent));
                mCenterTv.setVisibility(View.INVISIBLE);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (move) {
                    move = false;
                    scrollToPosition(mPosition);
                }
            }
        });
    }

    private boolean move = false;
    private int mPosition;

    private void scrollToPosition(int position) {
        /**
         * 判断是当前layoutManager是否为LinearLayoutManager,
         * 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
         */
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int lastItemPosition = linearLayoutManager.findLastVisibleItemPosition();

            if (position < firstItemPosition) {
                mRecyclerView.scrollToPosition(position);
            }
            if (firstItemPosition < position && position <= lastItemPosition) {
                //使item滚动到recyclerview可见区域第一个位置
                int movePosition = position - firstItemPosition;
                if (0 <= movePosition && movePosition < mRecyclerView.getChildCount()) {
                    int dy = mRecyclerView.getChildAt(movePosition).getTop();
                    mRecyclerView.smoothScrollBy(0, dy);
                }
            }
            if (position > lastItemPosition) {
                //先使item滚动到recyclerview可见区域
                mRecyclerView.scrollToPosition(position);
                move = true;
                mPosition = position;
            }
        }
    }

    private void initData() {
        for (String name : Constants.NAMES){
            mList.add(new Person(name,"13896583627"));
        }
        Collections.sort(mList);
        mAdapter.notifyDataSetChanged();
    }


}
