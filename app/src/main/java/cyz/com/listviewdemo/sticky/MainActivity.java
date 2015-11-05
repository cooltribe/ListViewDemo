package cyz.com.listviewdemo.sticky;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import java.security.SecureRandom;

import cyz.com.listviewdemo.R;

public class MainActivity extends Activity {

  private RecyclerView mRecyclerView;
  private ToggleButton mIsReverseButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_sticky);
    mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    Button button = (Button) findViewById(R.id.button_update);
    mIsReverseButton = (ToggleButton) findViewById(R.id.button_is_reverse);

    // Set adapter populated with example dummy data
    final AnimalsHeadersAdapter adapter = new AnimalsHeadersAdapter();
    adapter.add("Animals below!");
    adapter.addAll(getDummyDataSet());
    mRecyclerView.setAdapter(adapter);

    // Set button to update all views one after another (Test for the "Dance")
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < adapter.getItemCount(); i++) {
          final int index = i;
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              adapter.notifyItemChanged(index);
            }
          }, 50);
        }
      }
    });

    // Set layout manager
    int orientation = getLayoutManagerOrientation(getResources().getConfiguration().orientation);
    final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, mIsReverseButton.isChecked());
    mRecyclerView.setLayoutManager(layoutManager);

    // Add the sticky headers decoration
    final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
    mRecyclerView.addItemDecoration(headersDecor);

    // Add decoration for dividers between list items
    mRecyclerView.addItemDecoration(new DividerItemDecoration(this,orientation));

    // Add touch listeners
    StickyRecyclerHeadersTouchListener touchListener =
        new StickyRecyclerHeadersTouchListener(mRecyclerView, headersDecor);
    touchListener.setOnHeaderClickListener(
        new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
          @Override
          public void onHeaderClick(View header, int position, long headerId) {
            Toast.makeText(MainActivity.this, "Header position: " + position + ", id: " + headerId,
                    Toast.LENGTH_SHORT).show();
          }
        });
    mRecyclerView.addOnItemTouchListener(touchListener);
    mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {
        adapter.remove(adapter.getItem(position));
      }
    }));
    adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override
      public void onChanged() {
        headersDecor.invalidateHeaders();
      }
    });

    mIsReverseButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        boolean isChecked = mIsReverseButton.isChecked();
        mIsReverseButton.setChecked(isChecked);
        layoutManager.setReverseLayout(isChecked);
        adapter.notifyDataSetChanged();
      }
    });
  }

  private String[] getDummyDataSet() {
    return getResources().getStringArray(R.array.animals);
  }

  private int getLayoutManagerOrientation(int activityOrientation) {
    if (activityOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
      return LinearLayoutManager.VERTICAL;
    } else {
      return LinearLayoutManager.HORIZONTAL;
    }
  }

  private class AnimalsHeadersAdapter extends AnimalsAdapter<RecyclerView.ViewHolder>
      implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_item, parent, false);
      return new RecyclerView.ViewHolder(view) {
      };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      TextView textView = (TextView) holder.itemView;
      textView.setText(getItem(position));
    }


    @Override
    public long getHeaderId(int position) {
      if (position == 0) {
        return -1;
      } else {
        long i = getItem(position).charAt(1);
        Log.i("TAG",""+ i);
        return i;
      }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.view_header, parent, false);
      return new RecyclerView.ViewHolder(view) {
      };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
      TextView textView = (TextView) holder.itemView;
      textView.setText(String.valueOf(getItem(position).charAt(1)));
      holder.itemView.setBackgroundColor(getRandomColor());
    }

    private int getRandomColor() {
      SecureRandom rgen = new SecureRandom();
      return Color.HSVToColor(200, new float[]{
          rgen.nextInt(359), 1, 1
      });
    }

  }
}
