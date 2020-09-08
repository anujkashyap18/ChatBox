package com.vt.chatbox.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vt.chatbox.Interface.Mention;
import com.vt.chatbox.R;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupHolder> {

	Context context;
	List<String> name;
	Mention mention;

	public GroupAdapter(Context context, List<String> name, Mention mention) {
		this.context = context;
		this.name = name;
		this.mention = mention;
	}

	@NonNull
	@Override
	public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.custom_group_users, parent, false);
		return new GroupHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull GroupHolder holder, final int position) {

		holder.showUser.setText(name.get(position));
		holder.showUser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

//				Toast.makeText(context, "wah bhai waah", Toast.LENGTH_SHORT).show();
				mention.itemClicked(position, name.get(position));
			}
		});
	}

	@Override
	public int getItemCount() {
		return name.size();
	}

	class GroupHolder extends RecyclerView.ViewHolder {

		TextView showUser;

		public GroupHolder(@NonNull View itemView) {
			super(itemView);
			showUser = itemView.findViewById(R.id.show_group_user);
		}
	}
}
