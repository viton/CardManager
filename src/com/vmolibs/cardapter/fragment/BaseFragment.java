package com.vmolibs.cardapter.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment{

	private ProgressDialog pd;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(getContentView(), container, false);
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getViews();
		getViewsImpl();
	}
	
	
	public View findViewById(int resId){
		return getView().findViewById(resId);
	}
	
	public abstract int getContentView();
	
	public abstract void getViews();
	public abstract void getViewsImpl();
	
	
	public void showProgressDialog(String title, String msg){
		pd = ProgressDialog.show(getActivity(), title, msg);
	}
	
	public void dismissProgressDialog(){
		try {
			pd.dismiss();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
