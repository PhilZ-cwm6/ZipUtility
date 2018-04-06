package com.sentaroh.android.ZipUtility;

/*
The MIT License (MIT)
Copyright (c) 2011-2013 Sentaroh

Permission is hereby granted, free of charge, to any person obtaining a copy of 
this software and associated documentation files (the "Software"), to deal 
in the Software without restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to 
the following conditions:

The above copyright notice and this permission notice shall be included in all copies or 
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

*/ 

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sentaroh.android.Utilities.NotifyEvent;
import com.sentaroh.android.Utilities.ThemeColorList;
import com.sentaroh.android.Utilities.ThemeUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class CustomTreeFilelistAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Integer>mShowItems=new ArrayList<Integer>();
	private ArrayList<TreeFilelistItem>mDataItems=null;
	private boolean mSingleSelectMode=false;
	private boolean mShowLastModified=true;
	private boolean mSetColor=true;
	private int[] mIconImage= new int[] {R.drawable.cc_expanded,
			R.drawable.cc_expanded,
			R.drawable.cc_folder,
			R.drawable.cc_sheet,
			R.drawable.cc_blank};
	
	private ThemeColorList mThemeColorList;
	
	private NotifyEvent mNotifyExpand=null;

	private boolean mCheckBoxEnabled=true;
	
	public void setCheckBoxEnabled(boolean p) {mCheckBoxEnabled=p;}
	public boolean isCheckBoxEnabled() {return mCheckBoxEnabled;}
	
	public CustomTreeFilelistAdapter(Context c) {
		mContext = c;
		mDataItems=new ArrayList<TreeFilelistItem>();
		initTextColor();
		
	};

	public CustomTreeFilelistAdapter(Context c,
                                     boolean singleSelectMode, boolean showLastModified) {
		mContext = c;
		this.mSingleSelectMode=singleSelectMode;
		this.mShowLastModified=showLastModified;
		mDataItems=new ArrayList<TreeFilelistItem>();
		initTextColor();
	};

	public CustomTreeFilelistAdapter(Context c,
                                     boolean singleSelectMode, boolean showLastModified, boolean set_color) {
		mContext = c;
		this.mSingleSelectMode=singleSelectMode;
		this.mShowLastModified=showLastModified;
		mDataItems=new ArrayList<TreeFilelistItem>();
		mSetColor=set_color;
		initTextColor();
	};

	private void initTextColor() {
		mThemeColorList=ThemeUtil.getThemeColorList(mContext);
	}
	
	@Override
	public int getCount() {return mShowItems.size();}

	@Override
	public Integer getItem(int arg0) {return mShowItems.get(arg0);}

	@Override
	public long getItemId(int arg0) {return mShowItems.get(arg0);}
	
	public ArrayList<TreeFilelistItem> getDataList() {return mDataItems;}
	
	public void setDataList(ArrayList<TreeFilelistItem> fl) {
//		mDataItems.clear();
//		if (fl!=null) {
//			for (int i=0;i<fl.size();i++) mDataItems.add(fl.get(i));
//		}
		mDataItems=fl;
		sort();
//		createShowList();
	};

	public void setShowLastModified(boolean p) {
		mShowLastModified=p;
	};

	public void setSingleSelectMode(boolean p) {
		mSingleSelectMode=p;
	};

	public void setDataItemIsSelected(int pos) {
		if (mSingleSelectMode) {
			setAllItemUnchecked();
			mDataItems.get(pos).setChecked(true);
		} else {
			mDataItems.get(pos).setChecked(!mDataItems.get(pos).isChecked());
		}
		createShowList();
	};

	public void setDataItemIsUnselected(int pos) {
		mDataItems.get(pos).setChecked(false);
		createShowList();
	};

	public boolean isDataItemIsSelected() {
		boolean result=false;
		
		for (int i=0;i<mDataItems.size();i++) {
			if (mDataItems.get(i).isChecked()) {
				result=true;
				break;
			}
		}
		return result;
	};

	public boolean isAllDataItemIsSelected() {
		boolean result=true;
		
		for (int i=0;i<mDataItems.size();i++) {
			if (!mDataItems.get(i).isChecked()) {
				result=false;
				break;
			}
		}
		return result;
	};

	public int getDataItemSelectCount() {
		int result=0;
		
		for (int i=0;i<mDataItems.size();i++) {
			if (mDataItems.get(i).isChecked()) {
				result++;
			}
		}
		return result;
	};

	public void setAllItemUnchecked() {
		for (int i=0;i<mDataItems.size();i++) 
			mDataItems.get(i).setChecked(false); 
	};

	public void setAllItemChecked(boolean checked) {
		for (int i=0;i<mDataItems.size();i++) 
			mDataItems.get(i).setChecked(checked); 
	};

//	public void setItemSelected(int pos) {
//		if (isSingleSelectMode()) {
//			setAllItemUnchecked();
//			mDataItems.get(pos).setChecked(true);
//		} else {
//			mDataItems.get(pos).setChecked(!mDataItems.get(pos).isChecked());
//		}
//	};
	
	public boolean isSingleSelectMode() {
		return mSingleSelectMode;
	};

	public void removeDataItem(int dc) {
		mDataItems.remove(dc);
		createShowList();
		notifyDataSetChanged();
	};

	public void removeDataItem(TreeFilelistItem fi) {
		mDataItems.remove(fi);
		createShowList();
		notifyDataSetChanged();
	};

	public void replaceDataItem(int i, TreeFilelistItem fi) {
		mDataItems.set(i,fi);
		notifyDataSetChanged();
	};

	public int getDataItemCount() {
		return mDataItems.size();
	};

	public void addDataItem(TreeFilelistItem fi) {
		mDataItems.add(fi);
		notifyDataSetChanged();
	};

	public void insertDataItem(int i, TreeFilelistItem fi) {
		mDataItems.add(i,fi);
		notifyDataSetChanged();
	};

	public TreeFilelistItem getDataItem(int i) {
		return mDataItems.get(i);
	};

	public void createShowList() {
		mShowItems.clear();
		if (mDataItems!=null) {
			for (int i=0;i<mDataItems.size();i++) 
				if (!mDataItems.get(i).isHideListItem()) mShowItems.add(i);
		}
		notifyDataSetChanged();
	};
	
	public void hideChildItem(TreeFilelistItem pfi, int cp) {
		int sn=pfi.getListLevel()+1;
		TreeFilelistItem tfi;
		for (int i=cp+1;i<getDataItemCount();i++) {
			tfi=getDataItem(i);
			if (tfi.getListLevel()<sn) {
				break;
			} else {
				tfi.setHideListItem(true);
			}
		}
		pfi.setChildListExpanded(false);
		createShowList();
	};

	public void removeChildItem(TreeFilelistItem pfi, int cp) {
		int sn=pfi.getListLevel()+1;
		List<Integer> dl=new ArrayList<Integer>();
		for (int i=cp+1;i<getDataItemCount();i++) {
			if (getDataItem(i).getListLevel()<sn) break;
			else dl.add(i);
		}
		pfi.setChildListExpanded(false);
		pfi.setSubDirLoaded(false);
//		replaceDataItem(cp,pfi);
		
		for (int i=dl.size()-1;i>=0;i--) removeDataItem(dl.get(i));
		
		createShowList();
	};

	public void addChildItem(TreeFilelistItem pfi, CustomTreeFilelistAdapter afa, int cp) {
		//from adapter
		if (afa.getCount()!=0) {
			for (int i=0;i<afa.getDataItemCount();i++) {
				TreeFilelistItem tfi=afa.getDataItem(i);
				tfi.setListLevel(pfi.getListLevel()+1);
				insertDataItem(cp+i+1,tfi);
			}
			pfi.setChildListExpanded(true);
			pfi.setSubDirLoaded(true);
//			replaceDataItem(cp,pfi);
		} 
		createShowList();
	};
	
	public void addChildItem(TreeFilelistItem pfi, 
			ArrayList<TreeFilelistItem> afa, int cp) {
		//from Arraylist
		if (afa.size()!=0) {
			for (int i=0;i<afa.size();i++) {
				TreeFilelistItem tfi=afa.get(i);
				tfi.setListLevel(pfi.getListLevel()+1);
				insertDataItem(cp+i+1,tfi);
			}
			pfi.setChildListExpanded(true);
			pfi.setSubDirLoaded(true);
//			replaceDataItem(cp,pfi);
		} 
		createShowList();
	};


//	public void reshowChildItem(TreeFilelistItem fi, int cp) {
//		int ll=fi.getListLevel()+1;
//		for (int i=cp+1;i<getDataItemCount();i++) {
//			TreeFilelistItem tfi=getDataItem(i);
//			if (ll>tfi.getListLevel()) break;
//			else {
//				if (!singleSelectMode) if (fi.isChk())tfi.setChk(true);
//				if (fi.isChildListExpanded()) {
//					tfi.setHideListItem(false);
//					replaceDataItem(i,tfi);
//				}
//			}
//		}
//		fi.setChildListExpanded(true);
//		if (!singleSelectMode) fi.setChk(false);
//		replaceDataItem(cp,fi);
//		createShowList();
//	};
	
	public void reshowChildItem(TreeFilelistItem fi, int cp) {
//		dumpDataItemList();
		fi.setChildListExpanded(true);
//		replaceDataItem(cp,fi);
		if (fi.isDirectory()) {
			if (fi.isChildListExpanded()) {
				fi.setHideListItem(false);
				int litem_pos=0;
				//Directoryの?��?囲を求め?��?
				int lvl=fi.getListLevel();
				int d_lvl=lvl+1;
				for (int i=cp+1;i<getDataItemCount();i++) {
					if (lvl>=getDataItem(i).getListLevel()) {//end
						break;
					} else litem_pos=i;
				}
//				Log.v("","parent range start="+(cp+1)+", end="+litem_pos+
//						", cp="+cp+", name="+fi.getName()+", ll="+lvl+", d_lvl="+d_lvl);
				for (int i=cp+1;i<=litem_pos;i++) {
					//ドリル?��?ウンDirectory
//					Log.v("","parent cp="+i+", name="+getDataItem(i).getName()+
//							", ll="+(getDataItem(i).getListLevel()+1)+
//							", p_ll="+lvl);
					if (getDataItem(i).getListLevel()==d_lvl)
						reshowChildByParent(getDataItem(i),i, getDataItem(i).getListLevel()+1);
				}
			} else {
				fi.setHideListItem(false);
//				replaceDataItem(cp,fi);
			}
		} else {
			fi.setHideListItem(false);
//			replaceDataItem(cp,fi);
		}
		createShowList();
//		dumpDataItemList();
	};
	
	private void reshowChildByParent(TreeFilelistItem fi, int cp, int p_lvl) {
//		Log.v("","rc cp="+cp+", name="+fi.getName()+", ll="+p_lvl);
		if (fi.isDirectory()) {
			if (fi.isChildListExpanded()) {
				fi.setHideListItem(false);
				int litem_pos=0;
				int lvl=fi.getListLevel();
				for (int i=cp+1;i<getDataItemCount();i++) {
					if (lvl>=getDataItem(i).getListLevel()) {//end
						break;
					} else litem_pos=i;
				}
				for (int i=cp+1;i<=litem_pos;i++) {
					reshowChildByParent(getDataItem(i),i,getDataItem(i).getListLevel()+1);
				}
			} else {
				fi.setHideListItem(false);
//				replaceDataItem(cp,fi);
			}
		} else {
			boolean processed=false;
//			Log.v("","rc child name="+getDataItem(cp).getName()+", pos="+cp+", ll="+getDataItem(cp).getListLevel());
			for (int i=cp-1;i>=0;i--) {
//				Log.v("","rc check name="+getDataItem(i).getName()+", pos="+i);
				if (getDataItem(i).isDirectory() && getDataItem(i).getListLevel()==(getDataItem(cp).getListLevel()-1)) {
//					Log.v("","rc dir name="+getDataItem(i).getName()+", pos="+i+
//							", exp="+getDataItem(i).isChildListExpanded());
					processed=true;
					if (!getDataItem(i).isChildListExpanded()) {
						fi.setHideListItem(true);
//						Log.v("","rc name="+getDataItem(i).getName());
					} else fi.setHideListItem(false);
					break;
				}
			}
			if (!processed) fi.setHideListItem(false);
//			replaceDataItem(cp,fi);
		}
	};
	
	public void dumpDataItemList() {
		for (int i=0;i<mDataItems.size();i++) {
			Log.v("TreeFilelist"," pos="+i+
					", path="+mDataItems.get(i).getPath()+
					", name="+mDataItems.get(i).getName()+
					", cap="+mDataItems.get(i).getCap()+
					", level="+mDataItems.get(i).getListLevel()+
					", hide="+mDataItems.get(i).isHideListItem()+
					", expand="+mDataItems.get(i).isChildListExpanded()+
					", loaded="+mDataItems.get(i).isSubDirLoaded());
		}
	}

	public void setTriState(TreeFilelistItem fi, int cp) {
		boolean chk_found=false, unchk_found=false;
		if (fi.isChecked()) chk_found=true;
		else unchk_found=true;
		int ll=fi.getListLevel()+1;
		for (int i=cp+1;i<getDataItemCount();i++) {
			TreeFilelistItem tfi=getDataItem(i);
			if (ll>tfi.getListLevel()) break;
			else {
				if (tfi.isChecked()) chk_found=true;
				else unchk_found=true;
			}
		}
		for (int i=cp-1;i>0;i--) {
			TreeFilelistItem tfi=getDataItem(i);
			if (ll<tfi.getListLevel()) break;
			else {
				if (tfi.isChecked()) chk_found=true;
				else unchk_found=true;
			}
		}
		if (chk_found && unchk_found) {
			//tri state found
			Log.v("","tri state found");
			for (int i=cp-1;i>0;i--) {
				TreeFilelistItem tfi=getDataItem(i);
				if (ll<tfi.getListLevel()) {
					tfi.setTriState(true);
//					replaceDataItem(i,tfi);
					createShowList();
					break;
				}
			}
		}
	};

	private boolean mSortAscendant=true;
	public void setSortAscendant() {
		mSortAscendant=true;
	};
	
	public void setSortDescendant() {
		mSortAscendant=false;
	};

	public boolean isSortAscendant() {
		return mSortAscendant;
	};

	public boolean isSortKeyName() {
		return mSortKey==SORT_KEY_NAME;
	};

	public boolean isSortKeySize() {
		return mSortKey==SORT_KEY_SIZE;
	};

	public boolean isSortKeyTime() {
		return mSortKey==SORT_KEY_TIME;
	};

	final static private int SORT_KEY_NAME=0;
	final static private int SORT_KEY_TIME=1;
	final static private int SORT_KEY_SIZE=2;
	private int mSortKey=SORT_KEY_NAME;
	public void setSortKeyName() {
		mSortKey=SORT_KEY_NAME;
	};

	public void setSortKeyTime() {
		mSortKey=SORT_KEY_TIME;
	};

	public void setSortKeySize() {
		mSortKey=SORT_KEY_SIZE;
	};
	
	public void sort() {
		if (mSortKey==SORT_KEY_NAME) {
			sortByName(mDataItems,mSortAscendant);
		} else if (mSortKey==SORT_KEY_TIME) {
			sortByTime(mDataItems,mSortAscendant);
		} else if (mSortKey==SORT_KEY_SIZE) {
			sortBySize(mDataItems,mSortAscendant);
		}
		createShowList();
	};

	static private void sortByName(ArrayList<TreeFilelistItem>list, final boolean orderAsc) {
		if (orderAsc) {
			Collections.sort(list, new Comparator<TreeFilelistItem>(){
				@Override
				public int compare(TreeFilelistItem lhs, TreeFilelistItem rhs) {
					return lhs.getSortKeyName().compareToIgnoreCase(rhs.getSortKeyName());
				}
			});
		} else {
			Collections.sort(list, new Comparator<TreeFilelistItem>(){
				@Override
				public int compare(TreeFilelistItem lhs, TreeFilelistItem rhs) {
					return rhs.getSortKeyName().compareToIgnoreCase(lhs.getSortKeyName());
				}
			});
		}
	};

	static private void sortBySize(ArrayList<TreeFilelistItem>list, final boolean orderAsc) {
		if (orderAsc) {
			Collections.sort(list, new Comparator<TreeFilelistItem>(){
				@Override
				public int compare(TreeFilelistItem lhs, TreeFilelistItem rhs) {
					return lhs.getSortKeySize().compareToIgnoreCase(rhs.getSortKeySize());
				}
			});
		} else {
			Collections.sort(list, new Comparator<TreeFilelistItem>(){
				@Override
				public int compare(TreeFilelistItem lhs, TreeFilelistItem rhs) {
					return rhs.getSortKeySize().compareToIgnoreCase(lhs.getSortKeySize());
				}
			});
		}
	};

	static private void sortByTime(ArrayList<TreeFilelistItem>list, final boolean orderAsc) {
		if (orderAsc) {
			Collections.sort(list, new Comparator<TreeFilelistItem>(){
				@Override
				public int compare(TreeFilelistItem lhs, TreeFilelistItem rhs) {
					return lhs.getSortKeyTime().compareToIgnoreCase(rhs.getSortKeyTime());
				}
			});
		} else {
			Collections.sort(list, new Comparator<TreeFilelistItem>(){
				@Override
				public int compare(TreeFilelistItem lhs, TreeFilelistItem rhs) {
					return rhs.getSortKeyTime().compareToIgnoreCase(lhs.getSortKeyTime());
				}
			});
		}
	};

	private NotifyEvent cb_ntfy=null;
	public void setCbCheckListener(NotifyEvent ntfy) {
		cb_ntfy=ntfy;
	}

	public void unsetCbCheckListener() {
		cb_ntfy=null;
	}

	private boolean enableListener=true;
	
	public void setExpandCloseListener(NotifyEvent p) {
		mNotifyExpand=p;
	}

	public void unsetExpandCloseListener() {
		mNotifyExpand=null;
	}

	@Override
	public boolean isEnabled(int p) {
//		Log.v("","n="+getDataItem(p).getName()+", e="+getDataItem(p).isEnableItem());
		return getDataItem(p).isEnableItem();
	}
	
	private Drawable mDefaultBgColor=null;
	@SuppressLint("InflateParams")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		 	final ViewHolder holder;
		 	
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.custom_tree_file_list_item, null);
                holder=new ViewHolder();

            	holder.cb_cb1=(CheckBox)v.findViewById(R.id.tree_file_list_checkbox);
            	holder.rb_rb1=(RadioButton)v.findViewById(R.id.tree_file_list_radiobtn);
            	holder.ll_expand_view=(LinearLayout)v.findViewById(R.id.tree_file_list_expand_view);
            	holder.tv_spacer=(TextView)v.findViewById(R.id.tree_file_list_spacer);
            	holder.iv_expand=(ImageView)v.findViewById(R.id.tree_file_list_expand);
            	holder.ll_expand_view.setVisibility(LinearLayout.GONE);
            	holder.iv_image1=(ImageView)v.findViewById(R.id.tree_file_list_icon);
            	holder.tv_name=(TextView)v.findViewById(R.id.tree_file_list_name);
            	holder.tv_size=(TextView)v.findViewById(R.id.tree_file_list_size);
            	holder.tv_moddate=(TextView)v.findViewById(R.id.tree_file_list_date);
            	holder.tv_modtime=(TextView)v.findViewById(R.id.tree_file_list_time);
        		holder.ll_view=(LinearLayout)v.findViewById(R.id.tree_file_list_view);
            	
        		holder.ll_select_view=(LinearLayout)v.findViewById(R.id.tree_file_list_select_view);
        		holder.ll_date_time_view=(LinearLayout)v.findViewById(R.id.tree_file_list_date_time_view);
            	if (mThemeColorList.theme_is_light) {

            		if (mSetColor) {
//                    	holder.tv_spacer.setBackgroundColor(mThemeColorList.dialog_msg_background_color);
//                    	holder.iv_expand.setBackgroundColor(mThemeColorList.dialog_msg_background_color);
//                    	holder.iv_image1.setBackgroundColor(mThemeColorList.dialog_msg_background_color);
//                    	holder.tv_name.setBackgroundColor(mThemeColorList.dialog_msg_background_color);
//                    	holder.tv_size.setBackgroundColor(mThemeColorList.dialog_msg_background_color);
//                    	holder.tv_moddate.setBackgroundColor(mThemeColorList.dialog_msg_background_color);
//                    	holder.tv_modtime.setBackgroundColor(mThemeColorList.dialog_msg_background_color);
//
//                    	holder.ll_date_time_view.setBackgroundColor(mThemeColorList.dialog_msg_background_color);
                		holder.ll_view.setBackgroundColor(mThemeColorList.dialog_msg_background_color);
            		}

            	}
            	if (mDefaultBgColor==null) mDefaultBgColor=holder.ll_view.getBackground();
//            	if (normal_text_color==-1) normal_text_color=holder.tv_name.getCurrentTextColor();
//            	Log.v("","n="+String.format("0x%08x",holder.tv_name.getCurrentTextColor()));
            	v.setTag(holder); 
            } else {
         	   holder= (ViewHolder)v.getTag();
            }
            v.setEnabled(true);
            final TreeFilelistItem o = mDataItems.get(mShowItems.get(position));
//            Log.v("","data_items pos="+show_items.get(position)+", pos="+position);
//            ListView lv=(ListView)parent;
//            Log.v("","s_pos="+lv.getCheckedItemPosition());
            if (o != null) {
            	if (o.isEnableItem()) {
	            	holder.cb_cb1.setEnabled(true);
            		holder.rb_rb1.setEnabled(true);
	            	holder.tv_spacer.setEnabled(true);
	            	holder.iv_expand.setEnabled(true);
	            	holder.iv_image1.setEnabled(true);
	            	holder.tv_name.setEnabled(true);
	            	holder.tv_size.setEnabled(true);
            	} else {
            		holder.cb_cb1.setEnabled(false);
            		holder.rb_rb1.setEnabled(true);
	            	holder.tv_spacer.setEnabled(false);
	            	holder.iv_expand.setEnabled(false);
	            	holder.iv_image1.setEnabled(false);
	            	holder.tv_name.setEnabled(false);
	            	holder.tv_size.setEnabled(false);
            	}
        		if (mSingleSelectMode) {
        			holder.cb_cb1.setVisibility(CheckBox.GONE);
        			holder.rb_rb1.setVisibility(RadioButton.VISIBLE);
        		} else {
        			holder.cb_cb1.setVisibility(CheckBox.VISIBLE);
        			holder.rb_rb1.setVisibility(RadioButton.GONE);
        		}
            	if (o.getName().startsWith("---")) {
            		//空処?��?
            		holder.cb_cb1.setVisibility(CheckBox.GONE);
            		holder.rb_rb1.setVisibility(CheckBox.GONE);
            		holder.iv_expand.setVisibility(ImageView.GONE);
            		holder.iv_image1.setVisibility(ImageView.GONE);
            		holder.tv_name.setText(o.getName());
            	} else {
                	holder.tv_spacer.setWidth(o.getListLevel()*30);
                	holder.tv_name.setText(o.getName());
                	if (mShowLastModified) {
//                		Log.v("","name="+o.getName()+", cap="+o.getCap());
    		            if (!o.getCap().equals("") && !o.getCap().equals(" ")) {
    		            	String[] cap1 = new String[3];
    		            	cap1=o.getCap().split(",");
    		            	holder.tv_size.setText(cap1[1]);
    		            	holder.tv_moddate.setText(cap1[0].substring(0,10));
    		            	holder.tv_modtime.setText(cap1[0].substring(11));
    		            	if (o.isDirectory()) holder.tv_size.setText(""+o.getSubDirItemCount()+" Item");
    		            } else {
    		            	holder.tv_size.setText("");
    		            	holder.tv_moddate.setText("");
    		            	holder.tv_modtime.setText("");
    		            }
//    		       		int wsz_w=mActivity.getWindow()
//    	    					.getWindowManager().getDefaultDisplay().getWidth();
//    		       		int wsz_h=activity.getWindow()
//    	    					.getWindowManager().getDefaultDisplay().getHeight();
    	    		
//    		       		if (wsz_w>=700) holder.tv_size.setVisibility(TextView.VISIBLE);
//                       	else holder.tv_size.setVisibility(TextView.GONE);

//    		            if (!o.isDir()) holder.tv_size.setVisibility(TextView.VISIBLE);
//    		            else holder.tv_size.setVisibility(TextView.GONE);
                	} else {
    	            	holder.tv_size.setVisibility(TextView.GONE);
    	            	holder.tv_moddate.setVisibility(TextView.GONE);
    	            	holder.tv_modtime.setVisibility(TextView.GONE);
                	}
                   	if (!o.isHidden()) {
                   		if (o.isEnableItem()) {
                   			if (o.isZipEncrypted()) {
                           		holder.tv_name.setTextColor(mThemeColorList.text_color_warning);//normal_text_color);
        		            	holder.tv_size.setTextColor(mThemeColorList.text_color_warning);//normal_text_color);
        		            	holder.tv_moddate.setTextColor(mThemeColorList.text_color_warning);//normal_text_color);
        		            	holder.tv_modtime.setTextColor(mThemeColorList.text_color_warning);//normal_text_color);
                   			} else {
                           		holder.tv_name.setTextColor(mThemeColorList.text_color_primary);//normal_text_color);
        		            	holder.tv_size.setTextColor(mThemeColorList.text_color_primary);//normal_text_color);
        		            	holder.tv_moddate.setTextColor(mThemeColorList.text_color_primary);//normal_text_color);
        		            	holder.tv_modtime.setTextColor(mThemeColorList.text_color_primary);//normal_text_color);
                   			}
                   		} else {
                       		holder.tv_name.setTextColor(mThemeColorList.text_color_disabled);//Color.GRAY);
    		            	holder.tv_size.setTextColor(mThemeColorList.text_color_disabled);//Color.GRAY);
    		            	holder.tv_moddate.setTextColor(mThemeColorList.text_color_disabled);//Color.GRAY);
    		            	holder.tv_modtime.setTextColor(mThemeColorList.text_color_disabled);//Color.GRAY);
                   		}
                   	} else {
                   		holder.tv_name.setTextColor(mThemeColorList.text_color_disabled);//Color.GRAY);
		            	holder.tv_size.setTextColor(mThemeColorList.text_color_disabled);//Color.GRAY);
		            	holder.tv_moddate.setTextColor(mThemeColorList.text_color_disabled);//Color.GRAY);
		            	holder.tv_modtime.setTextColor(mThemeColorList.text_color_disabled);//Color.GRAY);
                   	}
                   	if(o.isDirectory()) {
                   		if (o.getSubDirItemCount()>0) {
                   			if (o.isEnableItem()) {
	                   			if (o.isChildListExpanded()) 
	                   				holder.iv_expand.setImageResource(mIconImage[0]);//expanded
	                   			else holder.iv_expand.setImageResource(mIconImage[1]);//collapsed
                   			} else holder.iv_expand.setImageResource(mIconImage[4]); //blank
                   		} else {
                   			holder.iv_expand.setImageResource(mIconImage[4]); //blank
                   		}
                   		holder.iv_image1.setImageResource(mIconImage[2]); //folder
                   	} else {
               			if (o.getMimeType().startsWith("image")) {
               				holder.iv_image1.setImageResource(R.drawable.ic_32_file_image);
               			} else if (o.getMimeType().startsWith("audio")) {
               				holder.iv_image1.setImageResource(R.drawable.ic_32_file_music);                   				
               			} else if (o.getMimeType().startsWith("video")) {
               				holder.iv_image1.setImageResource(R.drawable.ic_32_file_movie);
               			} else {
               				if (o.getFileExtention().equalsIgnoreCase("zip") 
//               						|| o.getFileExtention().equalsIgnoreCase("gz") || 
//               						o.getFileExtention().equalsIgnoreCase("tar")
               						) {
               					holder.iv_image1.setImageResource(R.drawable.ic_32_file_zip);
//               				} else if (o.getFileExtention().equals("doc") || o.getFileExtention().equals("docx")) {
//               					holder.iv_image1.setImageResource(R.drawable.ic_32_file_doc);
//               				} else if (o.getFileExtention().equals("xls") || o.getFileExtention().equals("xlsx")) {
//               					holder.iv_image1.setImageResource(R.drawable.ic_32_file_xls);
//               				} else if (o.getFileExtention().equals("ppt") || o.getFileExtention().equals("pptx")) {
//               					holder.iv_image1.setImageResource(R.drawable.ic_32_file_ppt);
               				} else if (o.getFileExtention().equalsIgnoreCase("pdf")) {
               					holder.iv_image1.setImageResource(R.drawable.ic_32_file_pdf);
               				} else {
                           		holder.iv_image1.setImageResource(mIconImage[3]); //sheet
               				}
               			}
                   		holder.iv_expand.setImageResource(mIconImage[4]); //blank
                   	}
            	}
            	
            	if (o.isChecked()) {
            		if (mThemeColorList.theme_is_light) holder.ll_view.setBackgroundColor(Color.CYAN);
            		else holder.ll_view.setBackgroundColor(Color.DKGRAY);
            	} else {
            		holder.ll_view.setBackgroundColor(mThemeColorList.dialog_msg_background_color);
            	}
               	final int p = position;
               	if (mNotifyExpand!=null) {
               		holder.iv_expand.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						mNotifyExpand.notifyToListener(true, new Object[]{p});
    					}
       				});
               		holder.ll_expand_view.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						mNotifyExpand.notifyToListener(true, new Object[]{p});
//    						Log.v("","clicked");
    					}
       				});
               	}
               	
//               	holder.ll_select_view.setOnClickListener(new OnClickListener(){
//					@Override
//					public void onClick(View v) {
//						holder.cb_cb1.setChecked(!holder.cb_cb1.isChecked());
//						holder.rb_rb1.setChecked(!holder.rb_rb1.isChecked());
//						notifyDataSetChanged();
//					}
//               	});
               	
               	holder.cb_cb1.setEnabled(isCheckBoxEnabled());
           		holder.cb_cb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						setButton(o,p,isChecked);
						notifyDataSetChanged();
  					}
   				});
           		holder.rb_rb1.setEnabled(isCheckBoxEnabled());
           		holder.rb_rb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						setButton(o,p,isChecked);
						notifyDataSetChanged();
  					}
   				});
           		if (mSingleSelectMode) {
           			if (o.isChecked()) {
               			int data_item_pos=mShowItems.get(p);
    					TreeFilelistItem fi;
    					for (int i=0;i<mDataItems.size();i++) {
    						fi=mDataItems.get(i);
    						if (fi.isChecked()&&data_item_pos!=i) {
    							fi.setChecked(false);
    						}
    					}
           			}
           			holder.rb_rb1.setChecked(mDataItems.get(mShowItems.get(position)).isChecked()); 
           		} else holder.cb_cb1.setChecked(mDataItems.get(mShowItems.get(position)).isChecked());
       			
            }
            return v;
    };

    private void setButton(TreeFilelistItem o,int p, boolean isChecked) {
		int data_item_pos=mShowItems.get(p);
		if (enableListener) {
			enableListener=false;
				if (mSingleSelectMode) {
					if (isChecked) {
						TreeFilelistItem fi;
						for (int i=0;i<mDataItems.size();i++) {
							fi=mDataItems.get(i);
							if (fi.isChecked()&&data_item_pos!=i) {
								fi.setChecked(false);
//								replaceDataItem(i,fi);
							}
						}
					}
				}else{ 
					//process child entry
//					if (o.getSubDirItemCount()>0 && o.isSubDirLoaded() &&
//							o.isChk()!=isChecked) 
//						processChildEntry(o, data_item_pos,isChecked);
					//process parent entry
//					if (o.getListLevel()>0 && o.isChk()!=isChecked) 
//						processParentEntry(o,data_item_pos,isChecked );
				}
				enableListener=true;
		}
		boolean c_chk=o.isChecked();
		o.setChecked(isChecked);
//		mDataItems.set(data_item_pos,o);
		
		if (cb_ntfy!=null) cb_ntfy.notifyToListener(isChecked, new Object[]{data_item_pos, c_chk});

    };
    
//	private float toPixel(int dip) {
//		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//				dip, resources.getDisplayMetrics());
//		return px;
//	};
    
    @SuppressWarnings("unused")
	private void processParentEntry(TreeFilelistItem cfi, int cp, boolean isChecked) {
		//process parent entry
		for (int i=cp-1;i>=0;i--) { //find parent entry
			if (mDataItems.get(i).getListLevel()==cfi.getListLevel()-1) {
				//parent founded
				TreeFilelistItem tfi = mDataItems.get(i);
				tfi.setChecked(false);
				mDataItems.set(i,tfi);
				notifyDataSetChanged();
				break;
			} 
		}
    };
	@SuppressWarnings("unused")
	private void processChildEntry(TreeFilelistItem cfi, int cp, boolean isChecked) {
		//process child entry
		int cl=cfi.getListLevel();
		for (int i=cp+1;i<mDataItems.size();i++) {
			if (cl>=mDataItems.get(i).getListLevel()) break;
			else {
				TreeFilelistItem tfi = mDataItems.get(i);
				tfi.setChecked(isChecked);
				mDataItems.set(i, tfi);
				notifyDataSetChanged();
			}
		}
	};

	static class ViewHolder {
		 TextView tv_name, tv_moddate, tv_modtime, tv_size, tv_spacer;
		 ImageView iv_image1;
		 ImageView iv_expand;
		 LinearLayout ll_view, ll_date_time_view, ll_expand_view, ll_select_view;
		 CheckBox cb_cb1;
		 RadioButton rb_rb1;
	}
}
