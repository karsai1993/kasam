package karsai.laszlo.kasam.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import karsai.laszlo.kasam.OnContentViewTypeChangeListener;
import karsai.laszlo.kasam.R;
import karsai.laszlo.kasam.model.Information;

public class InformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Information> mInformationList;
    private OnContentViewTypeChangeListener mOnContentViewTypeChangeListener;

    private final static int TYPE_COLLAPSED = 1;
    private final static int TYPE_EXPANDED = 2;

    public InformationAdapter(Context context, List<Information> informationList) {
        this.mContext = context;
        this.mOnContentViewTypeChangeListener = (OnContentViewTypeChangeListener) context;
        this.mInformationList = informationList;
    }

    @Override
    public int getItemViewType(int position) {
        if (mInformationList.get(position).isExpanded()) {
            return TYPE_EXPANDED;
        } else {
            return TYPE_COLLAPSED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_COLLAPSED:
                return new InformationCollapsedViewHolder(
                        LayoutInflater.from(mContext)
                                .inflate(R.layout.item_information_collapsed, parent, false)
                );
            default:
                return new InformationExpandedViewHolder(
                        LayoutInflater.from(mContext)
                                .inflate(R.layout.item_information_expanded, parent, false)
                );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Information information = mInformationList.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_COLLAPSED:
                InformationCollapsedViewHolder collapsedViewHolder = (InformationCollapsedViewHolder) holder;
                collapsedViewHolder.header.setText(information.getHeader());
                collapsedViewHolder.content.setText(information.getContent());
                break;
            default:
                InformationExpandedViewHolder expandedViewHolder = (InformationExpandedViewHolder) holder;
                expandedViewHolder.header.setText(information.getHeader());
                expandedViewHolder.content.setText(information.getContent());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mInformationList.size();
    }

    class InformationCollapsedViewHolder extends RecyclerView.ViewHolder {

        TextView header;
        TextView content;
        ImageView arrowImageView;

        InformationCollapsedViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.tv_header);
            content = itemView.findViewById(R.id.tv_content);
            arrowImageView = itemView.findViewById(R.id.iv_arrow);
            arrowImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnContentViewTypeChangeListener.onViewTypeChangeNeeded(getAdapterPosition());
                }
            });
        }
    }

    class InformationExpandedViewHolder extends RecyclerView.ViewHolder {

        TextView header;
        TextView content;
        ImageView arrowImageView;

        InformationExpandedViewHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.tv_header);
            content = itemView.findViewById(R.id.tv_content);
            arrowImageView = itemView.findViewById(R.id.iv_arrow);
            arrowImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnContentViewTypeChangeListener.onViewTypeChangeNeeded(getAdapterPosition());
                }
            });
        }
    }
}
