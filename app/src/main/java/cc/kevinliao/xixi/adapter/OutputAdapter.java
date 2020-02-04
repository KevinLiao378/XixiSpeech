package cc.kevinliao.xixi.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.File;
import java.util.List;

import cc.kevinliao.xixi.R;
import cc.kevinliao.xixi.entity.OutputBean;
import cc.kevinliao.xixi.holder.OutputHolder;

public class OutputAdapter extends BaseQuickAdapter<OutputBean, OutputHolder> {

    public OutputAdapter(int layoutResId, @Nullable List<OutputBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull OutputHolder helper, OutputBean item) {
        helper.setText(R.id.textView,item.getText());
        File file = new File(item.getPath());
        if (file.exists()) {
            helper.setText(R.id.tvFileName, file.getName());
        }
    }
}
