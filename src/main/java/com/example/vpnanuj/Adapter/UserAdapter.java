package com.example.vpnanuj.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.vpnanuj.Modals.Vpndata;
import com.example.vpnanuj.R;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.VpnViewHolder> {
    private List<Vpndata> vpnList;
    private Context context;
    private int selectedPosition = -1;
    private OnVpnClickListener vpnClickListener;

    public interface OnVpnClickListener {
        void onVpnClick(String ip, int port ,String vpnProvidername,String countryname);
    }

    public UserAdapter(List<Vpndata> vpnList, Context context, OnVpnClickListener vpnClickListener) {
        this.vpnList = vpnList;
        this.context = context;
        this.vpnClickListener = vpnClickListener;
    }

    @NonNull
    @Override
    public UserAdapter.VpnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vpnshow, parent, false);
        return new VpnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.VpnViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Vpndata vpndata = vpnList.get(position);

        Glide.with(context)
                .load(vpndata.getImage())
                .into(holder.imgs);

        holder.txt.setText(vpndata.getCountryname());

        holder.connectBtn.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();

            // Send IP and Port to MainActivity
            if (vpnClickListener != null) {
                vpnClickListener.onVpnClick(vpndata.getIp(), vpndata.getPort(),vpndata.getVpnProvidername(),vpndata.getCountryname()); // Example port
            }
        });

        if (selectedPosition == position) {
            holder.cvv.setCardBackgroundColor(Color.parseColor("#f4a526"));
        } else {
            holder.cvv.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        int ping = vpndata.getPing();
        int tintColor;

        if (ping < 13) {
            tintColor = Color.GREEN;
        } else if (ping >= 13 && ping <= 26) {
            tintColor = Color.YELLOW;
        } else {
            tintColor = Color.RED;
        }

        holder.net.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public int getItemCount() {
        return vpnList.size();
    }

    public static class VpnViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imgs;
        private TextView txt;
        LinearLayout connectBtn;
        ImageView net;
        CardView cvv;

        public VpnViewHolder(@NonNull View itemView) {
            super(itemView);
            imgs = itemView.findViewById(R.id.imgs);
            txt = itemView.findViewById(R.id.txt);
            connectBtn = itemView.findViewById(R.id.connectBtn);
            net = itemView.findViewById(R.id.net);
            cvv = itemView.findViewById(R.id.cvv);
        }
    }
}
