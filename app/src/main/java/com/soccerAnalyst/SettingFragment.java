package com.soccerAnalyst;

import android.graphics.Color;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.soccerAnalyst.sharedData.AppData;
import com.soccerAnalyst.utils.DragAndDropList;
import com.soccerAnalyst.utils.Game;

import java.util.Collections;
import java.util.List;


public class SettingFragment extends Fragment implements View.OnClickListener
{
    static View view;
    RadioGroup selectList,whereToadd;
    Button addBtn,deleteBtn,editBtn;
    TextView textView;
    RecyclerView eventsListView,gamesListView;
    DragAndDropList eventsList,gamesList,crntList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void onStop()
    {
        super.onStop();
        Log.d("exitFragemnt"," ");
        AppData.listChoosePosition = -1;
        crntList.notifyItemChanged(AppData.listChoosePosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        AppData.settingFragment = this;
        selectList = view.findViewById(R.id.selectList);
        whereToadd = view.findViewById(R.id.whereToAdd);
        final TextView header = view.findViewById(R.id.topHeader);
        final TextView eventNameEditText = view.findViewById(R.id.eventName);
        deleteBtn = view.findViewById(R.id.delete);
        deleteBtn.setOnClickListener(this);
        editBtn = view.findViewById(R.id.edit);
        editBtn.setOnClickListener(this);
        addBtn = view.findViewById(R.id.add);
        addBtn.setOnClickListener(this);
        textView = view.findViewById(R.id.eventName);
        eventsListView = view.findViewById(R.id.dragAndDropListEvents);
        gamesListView = view.findViewById(R.id.dragAndDropListGames);
        eventsList = createDragAndDropList(AppData.events,eventsListView);
        gamesList = createDragAndDropList(AppData.gamesStringList,gamesListView);
        AppData.listToShow = AppData.events;
        crntList = eventsList;
        selectList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                eventNameEditText.setText("");
                int itemChoosePosition = AppData.listChoosePosition;
                AppData.listChoosePosition = -1;
                changeMode();
                textView.setHintTextColor(Color.BLACK);
                switch(checkedId)
                {
                    case R.id.gamesList:
                        AppData.listToShow = AppData.gamesStringList;
                        crntList = gamesList;
                        eventsList.notifyItemChanged(itemChoosePosition);
                        gamesListView.setVisibility(View.VISIBLE);
                        eventsListView.setVisibility(View.INVISIBLE);
                        header.setText("רשימת משחקים :");
                        eventNameEditText.setHint("הכנס שם משחק");
                        addBtn.setText("הוסף משחק");
                        editBtn.setText("שנה שם משחק");
                        deleteBtn.setText("מחק משחק");
                        break;
                    case R.id.evetnsList :
                        AppData.listToShow = AppData.events;
                        crntList = eventsList;
                        gamesList.notifyItemChanged(itemChoosePosition);
                        gamesListView.setVisibility(View.INVISIBLE);
                        eventsListView.setVisibility(View.VISIBLE);
                        header.setText("רשימת אירועים :");
                        eventNameEditText.setHint("הכנס שם אירוע");
                        addBtn.setText("הוסף אירוע");
                        editBtn.setText("שנה אירוע");
                        deleteBtn.setText("מחק אירוע");
                        break;
                }
            }
        });
        return view;
    }

    public void changeMode()
    {
        if (AppData.listChoosePosition != -1)
        {
            addBtn.setVisibility(View.INVISIBLE);
            whereToadd.setVisibility(View.INVISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            deleteBtn.setVisibility(View.INVISIBLE);
            editBtn.setVisibility(View.INVISIBLE);
            addBtn.setVisibility(View.VISIBLE);
            whereToadd.setVisibility(View.VISIBLE);
        }

    }

    public DragAndDropList createDragAndDropList(List<String> list,RecyclerView recyclerView)
    {
        DragAndDropList recyclerAdapter = new DragAndDropList(list,this);
        recyclerView.setAdapter(recyclerAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);;
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return recyclerAdapter;
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0)
    {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
        {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(AppData.listToShow, fromPosition, toPosition);
            if (AppData.listToShow == AppData.gamesStringList )
            {
                Collections.swap(AppData.games, fromPosition, toPosition);
                changeSelectedGamePosition(fromPosition,toPosition);
            }
            changeChoosePosition(fromPosition,toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            Log.d("moveItem3","from " + fromPosition + " to " + toPosition) ;
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
        {

        }

    };

    public void changeSelectedGamePosition(int fromPosition,int toPosition)
    {
        if(GameFragment.gameChosen != -1)
        {
            if (GameFragment.gameChosen == fromPosition)
                GameFragment.gameChosen = toPosition;
            else if (fromPosition < GameFragment.gameChosen && toPosition >= GameFragment.gameChosen)
                GameFragment.gameChosen--;
            else if (fromPosition > GameFragment.gameChosen && toPosition <= GameFragment.gameChosen)
                GameFragment.gameChosen++;
        }
        if (EventsFragment.gameChosen != -1)
        {
            if (EventsFragment.gameChosen == fromPosition)
                EventsFragment.gameChosen = toPosition;
            else if (fromPosition < EventsFragment.gameChosen && toPosition >= EventsFragment.gameChosen)
                EventsFragment.gameChosen--;
            else if (fromPosition > EventsFragment.gameChosen && toPosition <= EventsFragment.gameChosen)
                EventsFragment.gameChosen++;
        }
    }

    public void changeChoosePosition(int fromPosition,int toPosition)
    {
        if (AppData.listChoosePosition == fromPosition)
            AppData.listChoosePosition = toPosition;
        else if (fromPosition < AppData.listChoosePosition && toPosition >= AppData.listChoosePosition)
            AppData.listChoosePosition--;
        else if (fromPosition > AppData.listChoosePosition && toPosition <= AppData.listChoosePosition)
            AppData.listChoosePosition++;
    }

    @Override
    public void onClick(View v) //buttons listener
    {
        switch (v.getId())
        {
            case R.id.edit:
                String changeToName = textView.getText().toString();
                if (changeToName.isEmpty() == false)
                {
                    AppData.listToShow.set(AppData.listChoosePosition,changeToName);
                    if (AppData.listToShow == AppData.gamesStringList)
                    {
                        AppData.games.get(AppData.listChoosePosition).gameName = changeToName;
                    }
                    textView.setHintTextColor(Color.BLACK);
                    textView.setText("");
                    int prevListChoosePosition = AppData.listChoosePosition;
                    AppData.listChoosePosition = -1;
                    crntList.notifyItemChanged(prevListChoosePosition);
                }
                else
                    textView.setHintTextColor(Color.RED);
                changeMode();
                break;
            case R.id.delete:
                if (AppData.listToShow == AppData.gamesStringList)
                {
                    AppData.games.remove(AppData.listChoosePosition);
                    if(GameFragment.gameChosen == AppData.listChoosePosition)
                        GameFragment.gameChosen = -1;
                    else if (GameFragment.gameChosen > AppData.listChoosePosition)
                        GameFragment.gameChosen--;
                    if(EventsFragment.gameChosen == AppData.listChoosePosition)
                        EventsFragment.gameChosen = -1;
                    else if (EventsFragment.gameChosen > AppData.listChoosePosition)
                        EventsFragment.gameChosen--;
                }
                AppData.listToShow.remove(AppData.listChoosePosition);
                textView.setHintTextColor(Color.BLACK);
                textView.setText("");
                crntList.notifyItemRemoved(AppData.listChoosePosition);
                AppData.listChoosePosition = -1;
                changeMode();
                break;
            case R.id.add:
                String newName = textView.getText().toString();
                if (newName.isEmpty())
                {
                    textView.setHintTextColor(Color.RED);
                    return;
                }
                if ( whereToadd.getCheckedRadioButtonId() == R.id.addToUp ) //up
                {
                    if (AppData.listToShow == AppData.gamesStringList)
                    {
                        AppData.games.add(0, new Game(newName));
                        if(GameFragment.gameChosen != -1)
                            GameFragment.gameChosen++;
                        if(EventsFragment.gameChosen != -1)
                            EventsFragment.gameChosen++;
                    }
                    AppData.listToShow.add(0, newName);
                    crntList.notifyItemInserted(0);
                }
                else // down
                {
                    if (AppData.listToShow == AppData.gamesStringList)
                        AppData.games.add(new Game(newName));
                    AppData.listToShow.add(newName);
                    crntList.notifyItemInserted(AppData.listToShow.size() -1);
                }
                textView.setHintTextColor(Color.BLACK);
                textView.setText("");
        }
    }
}