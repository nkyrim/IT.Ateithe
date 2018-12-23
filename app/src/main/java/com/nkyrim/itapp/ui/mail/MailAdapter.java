package com.nkyrim.itapp.ui.mail;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nkyrim.itapp.ItApp;
import com.nkyrim.itapp.R;
import com.nkyrim.itapp.domain.mail.Email;
import com.nkyrim.itapp.ui.util.base.BaseRecyclerAdapter;
import com.nkyrim.itapp.ui.util.BusEvents;
import com.nkyrim.itapp.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import org.greenrobot.eventbus.EventBus;

class MailAdapter extends BaseRecyclerAdapter<Email, MailAdapter.MailViewHolder> {

	public MailAdapter(ArrayList<Email> dataset) {
		super(dataset);
	}

	@Override
	public MailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mail, parent, false);
		return new MailViewHolder(v);
	}

	@Override
	public void onBindViewHolder(MailViewHolder holder, int position) {
		Email m = dataset.get(position);
		holder.bind(m);
	}

	class MailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@BindView(R.id.container1) View container;
		@BindView(R.id.tv_subject) TextView tv1;
		@BindView(R.id.tv_sender) TextView tv2;
		@BindView(R.id.tv_date) TextView tv3;
		@BindView(R.id.iv_attach) ImageView iv;

		private Email mail;

		public MailViewHolder(View v) {
			super(v);
			ButterKnife.bind(this, v);
			v.setOnClickListener(this);
		}

		public void bind(Email m) {
			this.mail = m;
			java.text.DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault());
			df.setTimeZone(TimeZone.getTimeZone("EET"));
			String subject = (m.getSubject() == null) ? Util.getString(R.string.no_subject) : m.getSubject();
			tv1.setText(subject);
			tv2.setText(m.getSender());
			tv3.setText(df.format(m.getDate()));
			if(!mail.isRead()) container.setBackgroundColor(ContextCompat.getColor(ItApp.getAppContext(), R.color.light_yellow));
			else if(mail.isDeleted()) container.setBackgroundColor(ContextCompat.getColor(ItApp.getAppContext(), R.color.grey));
			else container.setBackgroundColor(ContextCompat.getColor(ItApp.getAppContext(), android.R.color.transparent));
			if(m.hasAttachments()) iv.setVisibility(View.VISIBLE);
			else iv.setVisibility(View.GONE);
		}

		public void onClick(View v) {
			if(mail != null) {
				if(!mail.isDeleted()) container.setBackgroundColor(Color.TRANSPARENT);
				EventBus.getDefault().post(new BusEvents.MailSelectedEvent(mail));
			}
		}
	}
}
