package ran.tmpTest;

import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import ran.tmpTest.sharedData.AppData;
import ran.tmpTest.utils.DragAndDropList;
import ran.tmpTest.utils.Game;

import java.util.Collections;
import java.util.List;


public class SettingFragment extends Fragment
{
    static View view;
    RadioGroup selectList,whereToadd;
    Button addBtn,deleteBtn,editBtn;
    RecyclerView eventsListView,gamesListView;
    DragAndDropList eventsList,gamesList,crntList;

    TextView header, eventOrGameEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        AppData.settingFragment = this;
        selectList = view.findViewById(R.id.selectList);
        whereToadd = view.findViewById(R.id.whereToAdd);
        header = view.findViewById(R.id.topHeader);
        eventOrGameEditText = view.findViewById(R.id.eventOrGameEditText);
        deleteBtn = view.findViewById(R.id.delete);
        deleteBtn.setOnClickListener((View) -> deleteBtn());
        editBtn = view.findViewById(R.id.edit);
        editBtn.setOnClickListener((View) -> editBtn());
        addBtn = view.findViewById(R.id.add);
        addBtn.setOnClickListener((View) -> addBtn());
        eventsListView = view.findViewById(R.id.dragAndDropListEvents);
        gamesListView = view.findViewById(R.id.dragAndDropListGames);
        eventsList = createDragAndDropList(AppData.events,eventsListView);
        gamesList = createDragAndDropList(AppData.gamesStringList,gamesListView);
        AppData.listToShow = AppData.events;
        crntList = eventsList;
        selectList.setOnCheckedChangeListener((radioGroup,checkedId) -> onListChange(checkedId));
        return view;
    }

    public void onResume()
    {
        super.onResume();
        eventOrGameEditText.setText("");
    }

    public void onStop()
    {
        super.onStop();
        dismissKeyboard();
        if(AppData.listChoosePosition != -1)
        {
            AppData.listChoosePosition = -1;
            crntList.notifyItemChanged(AppData.listChoosePosition);
        }
    }

    private void dismissKeyboard()
    {
        eventOrGameEditText.setEnabled(false);
        eventOrGameEditText.setEnabled(true);
    }

    private void onListChange(int checkedId)
    {
        eventOrGameEditText.setText("");
        int itemChoosePosition = AppData.listChoosePosition;
        AppData.listChoosePosition = -1;
        changeToNoneChooseListItemMode();
        switch(checkedId)
        {
            case R.id.gamesList:
                AppData.listToShow = AppData.gamesStringList;
                crntList = gamesList;
                eventsList.notifyItemChanged(itemChoosePosition);
                gamesListView.setVisibility(View.VISIBLE);
                eventsListView.setVisibility(View.INVISIBLE);
                header.setText("רשימת משחקים :");
                eventOrGameEditText.setHint("הכנס שם משחק");
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
                eventOrGameEditText.setHint("הכנס שם אירוע");
                addBtn.setText("הוסף אירוע");
                editBtn.setText("שנה אירוע");
                deleteBtn.setText("מחק אירוע");
                break;
        }
    }

    public void changeToChooseListItemMode()
    {
        deleteBtn.setVisibility(View.VISIBLE);
        editBtn.setVisibility(View.VISIBLE);
        addBtn.setVisibility(View.INVISIBLE);
        whereToadd.setVisibility(View.INVISIBLE);
    }

    public void changeToNoneChooseListItemMode()
    {
        AppData.listChoosePosition = -1;
        deleteBtn.setVisibility(View.INVISIBLE);
        editBtn.setVisibility(View.INVISIBLE);
        addBtn.setVisibility(View.VISIBLE);
        whereToadd.setVisibility(View.VISIBLE);
    }
    public DragAndDropList createDragAndDropList(List<String> list,RecyclerView recyclerView)
    {
        DragAndDropList recyclerAdapter = new DragAndDropList(list);
        recyclerView.setAdapter(recyclerAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0)
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
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return recyclerAdapter;
    }

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

    private void editBtn()
    {
        String changeToName = eventOrGameEditText.getText().toString();
        if (changeToName.isEmpty())
        {
            Toast.makeText(getActivity(), eventOrGameEditText.getHint(), Toast.LENGTH_SHORT).show();
            return;
        }
        AppData.listToShow.set(AppData.listChoosePosition, changeToName);
        if (AppData.listToShow == AppData.gamesStringList)
            AppData.games.get(AppData.listChoosePosition).gameName = changeToName;
        eventOrGameEditText.setText("");
        int prevListChoosePosition = AppData.listChoosePosition;
        changeToNoneChooseListItemMode();
        crntList.notifyItemChanged(prevListChoosePosition);
    }

    private void deleteBtn()
    {
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
        eventOrGameEditText.setText("");
        crntList.notifyItemRemoved(AppData.listChoosePosition);
        changeToNoneChooseListItemMode();
    }
    private void addBtn()
    {
        String newName = eventOrGameEditText.getText().toString();
        if (newName.isEmpty())
        {
            Toast.makeText(getActivity(), eventOrGameEditText.getHint(), Toast.LENGTH_SHORT).show();
            return;
        }
        if ( whereToadd.getCheckedRadioButtonId() == R.id.addToUp )
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
        else // whereToadd.getCheckedRadioButtonId() == R.id.addToDown
        {
            if (AppData.listToShow == AppData.gamesStringList)
                AppData.games.add(new Game(newName));
            AppData.listToShow.add(newName);
            crntList.notifyItemInserted(AppData.listToShow.size() -1);
        }
        eventOrGameEditText.setText("");
    }
}