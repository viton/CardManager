package com.vmolibs.cardapter.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.vmolibs.cardadapter.R;

public class CardManagerFragment extends BaseFragment implements OnTouchListener, OnClickListener, CardManagerDelegate{

	private PCNAdapter adapter;
	
	private int currentPosition = 0;
	private long animationDuration = 400;
	
	private List<FrameLayout> views = new ArrayList<FrameLayout>();
	
	//If the movement is a initial move for a view
	private boolean isInitialMove = true;
	
	//delta of the current movement
	private float deltaY;
	
	private float movementCount;
	private float lastY = -1;

	private PCNListener pcnListener;
	

	public static CardManagerFragment newInstance(PCNAdapter adapter){
		CardManagerFragment fragment = new CardManagerFragment();
		
		fragment.adapter = adapter;
		
		return fragment;
	}
	
	
	@Override
	public int getContentView() {
		return R.layout.scroll_pager;
	}

	@Override
	public void getViews() {
//		visibleFrame = (FrameLayout) findViewById(R.id.frag_front);
	}

	@Override
	public void getViewsImpl() {
		RelativeLayout relative = (RelativeLayout) getView();
		
		
		for (int i = 0; i < adapter.getCount(); i++) {
			FrameLayout f = new FrameLayout(getActivity());
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			
			int id = View.generateViewId();
			f.setId(id);
			relative.addView(f, params);
			views.add(f);
		}
		
		loadFragmentIntoView(0);
		loadFragmentIntoView(1);
		
		
		
		
		getView().setOnClickListener(this);
		getView().setOnTouchListener(this);
			
	}

	

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		
		if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
			if(movementCount > 200 && currentPosition < adapter.getCount()-1){
				passToNextCard();
				if(pcnListener != null) pcnListener.onChangeCard(currentPosition, currentPosition-1);
				loadFragmentIntoView(currentPosition+1);
				removeFragmentFromView(currentPosition-2);
			}else if(movementCount > 0){
				stayInThisCard(movementCount);
			}else if(currentPosition != 0 && Math.abs(movementCount) > 100){
				backToPreviousCard();
				if(pcnListener != null) pcnListener.onChangeCard(currentPosition, currentPosition+1);
				loadFragmentIntoView(currentPosition-1);
				removeFragmentFromView(currentPosition+2);
			}
			Log.e("", "CURRENT POSITION: "+currentPosition);
			movementCount = 0;
			return false;
		}
		
		float y = event.getY(); 
		if(isInitialMove){
			isInitialMove = false;
			lastY = y;
			return false;
		}	
		
		
		deltaY = isInitialMove? 1:event.getY() - lastY;
		movementCount += deltaY;

		isInitialMove = false;
		lastY = y;
		return false;
	}
	
	private void stayInThisCard(float deltaY){
		isInitialMove = true;
//		animateStayList(getCurrentView(), deltaY);
	}
	
	private void passToNextCard(){
		isInitialMove = true;
		animateListOut(getCurrentView());
		currentPosition++;
	}

	private void backToPreviousCard(){
		isInitialMove = true;
		currentPosition--;
		animateBringBackView(getCurrentView());
	}
	
	private void animateBringBackView(View v){
		AnimationSet replaceAnimation = new AnimationSet(false);
        replaceAnimation.setFillAfter(true);
        
        TranslateAnimation trans = new TranslateAnimation(
        		TranslateAnimation.RELATIVE_TO_SELF, v.getX(),
                TranslateAnimation.RELATIVE_TO_SELF, v.getX(), 
                TranslateAnimation.ABSOLUTE, 1000,
                TranslateAnimation.ABSOLUTE, 0);
        trans.setDuration(animationDuration);
        trans.setFillAfter(true);
        
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(animationDuration);
 
        replaceAnimation.addAnimation(trans);
        replaceAnimation.addAnimation(fadeIn);
        
        v.startAnimation(replaceAnimation);
	}
	
	public void animateStayList(final View v, float deltaY){
        AnimationSet replaceAnimation = new AnimationSet(false);
        replaceAnimation.setFillAfter(true);

        Log.e("MOVE STAY LIST", "v.getX: " + v.getX());
        Log.e("MOVE STAY LIST", "v.getY: " + v.getY());
        
        Log.e("MOVE STAY LIST", "Movement count: " + movementCount);
        
        TranslateAnimation trans = new TranslateAnimation(
        		TranslateAnimation.RELATIVE_TO_SELF, v.getX(),
                TranslateAnimation.RELATIVE_TO_SELF, v.getX(), 
                TranslateAnimation.ABSOLUTE, deltaY,
                TranslateAnimation.ABSOLUTE, 0-deltaY);
        trans.setDuration(animationDuration);
        trans.setFillAfter(true);
        
        
        
        replaceAnimation.addAnimation(trans);
        
        v.startAnimation(replaceAnimation);
	}
	
	private void animateListOut(View v){
        AnimationSet replaceAnimation = new AnimationSet(false);
        replaceAnimation.setFillAfter(true);

        TranslateAnimation trans = new TranslateAnimation(0, 0,
                TranslateAnimation.ABSOLUTE, v.getX(), 0, 0,
                TranslateAnimation.ABSOLUTE, 1000);
        trans.setDuration(animationDuration);
        
        Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new AccelerateInterpolator());
		fadeOut.setDuration(animationDuration);
        
		replaceAnimation.addAnimation(trans);
        replaceAnimation.addAnimation(fadeOut);

        v.startAnimation(replaceAnimation);
	}
	
	@Override
	public void onClick(View view) {
	}
	
	private View getCurrentView(){
		RelativeLayout relative = (RelativeLayout) getView();
		View v = relative.getChildAt(adapter.getCount() - 1 - currentPosition);
		return v;
	}
	
	private void loadFragmentIntoView(int position){
		if(position < adapter.getCount() && adapter.getCount() > 0 && position >= 0){
			Log.e("", "LOADING FRAGMENT FOR POSITION: " + position);
			
			FragmentManager fm = getChildFragmentManager();
			
			RelativeLayout relative = (RelativeLayout) getView();
			View v = relative.getChildAt(adapter.getCount() - 1 - position);
			
			fm
			.beginTransaction()
			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
			.add(v.getId(), adapter.getFragment(position), "current_fragment_"+position)
			.commit();
		}
	}
	
	private void removeFragmentFromView(int position){
		if(position < adapter.getCount() && adapter.getCount() > 0 && position >= 0){
			Log.e("", "REMOVE FRAGMENT FOR POSITION: " + position);
			
			FragmentManager fm = getChildFragmentManager();
			Fragment f = fm.findFragmentByTag("current_fragment_"+position);
			if(f != null){
				fm
				.beginTransaction()
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
				.remove(f)
				.commit();
			}
		}
	}
	
	
	
	public void reloadCards(){}
	
	public void setPCNListener(PCNListener listener){
		this.pcnListener = listener;
	}
	
	public int getCurrentCard(){
		if(adapter.getCount() == 0){
			return -1;
		}
		return currentPosition;
	}
	
	public interface PCNAdapter{
		
		public int getCount();
		
		public Fragment getFragment(int position);
		
	}
	
	public interface PCNListener{
		
		public void onChangeCard(int currentPosition, int previousPosition);
		
	}
}


