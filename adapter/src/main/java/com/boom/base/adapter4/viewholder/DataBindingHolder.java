package com.boom.base.adapter4.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A viewHolder quick to use when the project enabled ViewDataBinding.
 * ViewDataBinding 快速使用的 ViewHolder
 * @param <DB> ViewDataBinding
 */
public class DataBindingHolder<DB extends ViewDataBinding> extends RecyclerView.ViewHolder {

    private final DB binding;

    public DataBindingHolder(DB binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public DataBindingHolder(@NonNull View itemView) {
        super(itemView);
        this.binding = DataBindingUtil.bind(itemView);

        if (this.binding == null)
            throw new NullPointerException("DataBinding is Null. Please check Layout resource or ItemView");
    }

    public DataBindingHolder(@LayoutRes int resId, @NonNull ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext()).inflate(resId, parent, false));
    }

    @NonNull
    public DB getBinding() {
        return binding;
    }
}
