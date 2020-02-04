package cc.kevinliao.xixi;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import cc.kevinliao.xixi.adapter.OutputAdapter;
import cc.kevinliao.xixi.entity.OutputBean;
import cc.kevinliao.xixi.speech.util.FileConstant;


public class OutputActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    OutputAdapter outputAdapter;
    ArrayList<OutputBean> list;
    File jsonFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("已合成音频");
        recyclerView = findViewById(R.id.output_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        list = new ArrayList<>();


        jsonFile = new File(FileConstant.getJsonRootPath() + File.separator + FileConstant.OutPutlistFileName);
        if (FileUtils.isFileExists(jsonFile)) {
            String json = FileIOUtils.readFile2String(jsonFile);
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (jsonObject.length() > 0) {
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String text = keys.next();
                        OutputBean outputBean = new OutputBean();
                        outputBean.setText(text);
                        outputBean.setPath(jsonObject.optString(text));
                        list.add(outputBean);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        outputAdapter = new OutputAdapter(R.layout.item_output, list);
        outputAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OutputBean outputBean = list.get(position);
                if (outputBean != null && !TextUtils.isEmpty(outputBean.getPath())) {
                    AudioPlayerUtils.getInstance().play(OutputActivity.this, outputBean.getPath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {

                        }
                    });
                }
            }
        });
        recyclerView.setAdapter(outputAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.output_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_clear:
                new AlertDialog.Builder(this)
                        .setMessage("确定清空所有文件？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (FileUtils.deleteAllInDir(FileConstant.getJsonRootPath()) &&
                                        FileUtils.deleteAllInDir(FileConstant.getSpeechRootPath())) {
                                    list.clear();
                                    outputAdapter.notifyDataSetChanged();
                                    showTip("已清空!");
                                } else {
                                    showTip("清空失败!");
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                return true;
            case R.id.action_export:
                String zipFilePath = FileConstant.getJsonRootPath() + File.separator + FileConstant.OutPutlistZipFileName;
                if (!FileUtils.isFileExists(zipFilePath)) {
                    if (list != null && list.size() > 0) {
                        try {
                            if (ZipUtils.zipFile(FileConstant.getSpeechRootPath(), zipFilePath))  {
                                showTip("已打包成压缩包，快发送到QQ或微信吧~");
                                showShare(zipFilePath);
                            } else {
                                showTip("失败了!");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    showTip("已打包成压缩包，快发送到QQ或微信吧~");
                    showShare(zipFilePath);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTip(String str) {
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    private void showShare(String zipFilePath) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(zipFilePath);
        if(fileWithinMyDir.exists()) {
            intentShareFile.setType("application/zip");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileProvider", fileWithinMyDir);
                intentShareFile.putExtra(Intent.EXTRA_STREAM, contentUri);
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + zipFilePath));
            }
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "发送文件...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "发送文件...");
            startActivity(Intent.createChooser(intentShareFile, "发送文件..."));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioPlayerUtils.getInstance().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioPlayerUtils.getInstance().resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioPlayerUtils.getInstance().release();
    }
}
