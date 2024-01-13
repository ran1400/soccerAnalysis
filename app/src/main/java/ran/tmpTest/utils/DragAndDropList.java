package ran.tmpTest.utils;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ran.tmpTest.R;
import ran.tmpTest.SettingFragment;
import ran.tmpTest.sharedData.AppData;

import java.util.List;

public class DragAndDropList extends RecyclerView.Adapter<DragAndDropList.ViewHolder>
{

    List<String> list;

    public DragAndDropList(List<String> list)
    {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.drag_and_drop_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.textView.setText(list.get(position));
        if (AppData.listChoosePosition == position)
            holder.textView.setTextColor(Color.RED);
        else
            holder.textView.setTextColor(Color.BLACK);
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView textView;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if (AppData.listChoosePosition == -1) // none choose item from the list before click
            {
                AppData.listChoosePosition = getAdapterPosition();
                notifyItemChanged(AppData.listChoosePosition);
                AppData.settingFragment.changeToChooseListItemMode();
            }
            else if (AppData.listChoosePosition == getAdapterPosition())
            {
                AppData.settingFragment.changeToNoneChooseListItemMode();
                notifyItemChanged(getAdapterPosition());
            }
            else
            {
                int prevChoosePosition = AppData.listChoosePosition;
                AppData.listChoosePosition = getAdapterPosition();
                notifyItemChanged(prevChoosePosition);
                notifyItemChanged(AppData.listChoosePosition);
            }
        }

    }
}

