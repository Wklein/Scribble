package drawing;

import java.util.ArrayList;

import com.example.drawingfun.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseExpandableListAdapter{
    private LayoutInflater inflater;
    private ArrayList<Parent> mParent;
    
    public CustomAdapter(Context context, ArrayList<Parent> parent){
        mParent = parent;
        inflater = LayoutInflater.from(context);
    }
	
	@Override
	public Object getChild(int i, int h) {
		return mParent.get(i).getArrayChildren().get(h);
	}

	@Override
	public long getChildId(int i, int h) {
		return h;
	}

    @Override
    //in this method you must set the text to see the children on the list
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.child, viewGroup,false);
        }

        TextView textView = (TextView) view.findViewById(R.id.list_item_text_child);
        //"i" is the position of the parent/group in the list and 
        //"i1" is the position of the child
        textView.setText(mParent.get(i).getArrayChildren().get(i1));

        //return the entire view
        return view;
    }

	@Override
	public int getChildrenCount(int i) {
		return mParent.get(i).getArrayChildren().size();
	}

	@Override
	public Object getGroup(int i) {
		return mParent.get(i).getTitle();
	}

	@Override
	public int getGroupCount() {
		return mParent.size();
	}

	@Override
	public long getGroupId(int i) {
		return i;
	}

    @Override
    //in this method you must set the text to see the parent/group on the list
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        if (view == null)

  {
            view = inflater.inflate(R.layout.parent, viewGroup,false);
        }


        TextView textView = (TextView) view.findViewById(R.id.list_item_text_view);
        //"i" is the position of the parent/group in the list
        textView.setText(getGroup(i).toString());

        //return the entire view
        return view;
    }

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int i, int h) {
		return true;
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
	   /* used to make the notifyDataSetChanged() method work */
       super.registerDataSetObserver(observer);
    }

}
