package com.name.social_helper_r_p;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LoaderAdapter extends RecyclerView.Adapter<LoaderAdapter.Loader> {
    public class Loader extends RecyclerView.ViewHolder {

        public Loader(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public Loader onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loader_adapter, parent, false);
        Loader loader = new Loader(v);
        return loader;
    }

    @Override
    public void onBindViewHolder(@NonNull Loader holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }


}
