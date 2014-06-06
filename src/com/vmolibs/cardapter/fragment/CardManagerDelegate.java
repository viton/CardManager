package com.vmolibs.cardapter.fragment;

import com.vmolibs.cardapter.fragment.CardManagerFragment.PCNListener;

public interface CardManagerDelegate {

	public void reloadCards();
	
	public void setPCNListener(PCNListener listener);
	
	public int getCurrentCard();
	
	public void setCurrentPositionCard(int position);
	
}
