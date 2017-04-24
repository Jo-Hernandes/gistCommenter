package hernandes.jo.gistcommenter.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import hernandes.jo.gistcommenter.R;


public class LoadingAnimation {

  ViewGroup mParentView = null;
  boolean active = false;

  FrameLayout mBackgroundView = null;



  public LoadingAnimation(ViewGroup parentView) {
    mParentView = parentView;
    mBackgroundView = ((FrameLayout) View.inflate(parentView.getContext(), R.layout.overlay_loading, null));
    mBackgroundView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
      }
    });
  }



  public void start() {
    if(active) return;
    active = true;

    try {
      mBackgroundView.setVisibility(View.VISIBLE);
      mParentView.addView(mBackgroundView);
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  public void stop() {
    if(!active) return;
    active = false;

    try {
      mBackgroundView.setVisibility(View.GONE);
      mParentView.removeView(mBackgroundView);
      mBackgroundView.invalidate();
    } catch (Exception e){
      e.printStackTrace();
    }
  }



}
