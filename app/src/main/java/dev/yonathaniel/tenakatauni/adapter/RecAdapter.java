package dev.yonathaniel.tenakatauni.adapter;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.yonathaniel.tenakatauni.R;
import dev.yonathaniel.tenakatauni.models.UserModel;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.RecVH> {

    List<UserModel> userModels;
    Context context;

    public RecAdapter(List<UserModel> userModels, Context context) {
        this.userModels = userModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new RecVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecVH holder, int position) {
        UserModel userModel = userModels.get(holder.getAdapterPosition());
//        holder.img.
        String folderPath = Environment.getExternalStorageDirectory() + "/tenakata";
        File newFile = new File(folderPath, userModel.getPhoto());
        Picasso.with(context)
                .load(newFile)
                .placeholder(R.mipmap.default_useravatar)
                .into(holder.img);
        holder.txtName.setText(userModel.getName());
        holder.txtAge.setText(String.format("%sYrs", userModel.getAge()));
        holder.txtIQ.setText(String.format("IQ: %s", userModel.getIqTest()));
        holder.txtGender.setText(userModel.getGender().toUpperCase());
        holder.txtHeight.setText(String.format("%scm", userModel.getHeight()));
        holder.txtMStatus.setText(userModel.getMaritalStatus());
    }


    @Override
    public int getItemCount() {
        return userModels.size();
    }

    static class RecVH extends RecyclerView.ViewHolder {

        CircleImageView img;
        TextView txtName;
        TextView txtMStatus;
        TextView txtGender;
        TextView txtAge;
        TextView txtHeight;
        TextView txtIQ;

        public void viewHolder(View convertView) {
            img = convertView.findViewById(R.id.img);
            txtName = convertView.findViewById(R.id.txtName);
            txtMStatus = convertView.findViewById(R.id.txtMStatus);
            txtGender = convertView.findViewById(R.id.txtGender);
            txtAge = convertView.findViewById(R.id.txtAge);
            txtHeight = convertView.findViewById(R.id.txtHeight);
            txtIQ = convertView.findViewById(R.id.txtIQ);
        }

        public RecVH(@NonNull View itemView) {
            super(itemView);
            viewHolder(itemView);
        }
    }
}
