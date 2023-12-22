package com.soccerAnalyst;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.soccerAnalyst.sharedData.AppData;
import com.soccerAnalyst.utils.Event;
import com.soccerAnalyst.utils.ExelHandel;

import java.util.List;


public class EventsFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    public static int gameChosen;
    private TextView gameEvents;
    private List<Event> listToShow;

    private Button saveFileBtn;
    private Spinner chooseGameDropDownList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        AppData.eventsFragment = this;
        saveFileBtn = view.findViewById(R.id.saveFileBtn);
        if (AppData.gamesStringList.isEmpty())
        {
            AppData.mainActivity.showSnackBar("הוסף משחקים בהגדרות",700);
            saveFileBtn.setVisibility(View.INVISIBLE);
            return view;
        }
        saveFileBtn.setOnClickListener(this::setSaveFileBtn);
        gameEvents = view.findViewById(R.id.scrollViewText);
        chooseGameDropDownList = (Spinner)view.findViewById(R.id.dropDownList);
        createDropDownList();
        return view;
    }

    public void onResume()
    {
        super.onResume();
        if(gameChosen != -1)
            chooseGameDropDownList.setSelection(gameChosen);
    }

    public void setSaveFileBtn(View view)
    {
        ExelHandel exelHandel = new ExelHandel();
        boolean success =  exelHandel.makeEventsFile();
        if (success)
            AppData.mainActivity.showSnackBar("הקובץ נשמר בתיקיית הורדות",1000);
        else
            AppData.mainActivity.showSnackBar("שמירת הקובץ נכשלה",1000);
    }

    public void createDropDownList()
    {

        ArrayAdapter<String>adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,AppData.gamesStringList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseGameDropDownList.setAdapter(adapter);
        chooseGameDropDownList.setOnItemSelectedListener(this);
    }

    private void showGameEvent()
    {
        if(listToShow == null)
        {
            AppData.mainActivity.showSnackBar("הוסף משחקים בהגדרות",700);
            saveFileBtn.setVisibility(View.INVISIBLE);
            gameEvents.setText("");
            return;
        }
        if(listToShow.size() == 0)
        {
            saveFileBtn.setVisibility(View.INVISIBLE);
            gameEvents.setText("");
            return;
        }
        saveFileBtn.setVisibility(View.VISIBLE);
        StringBuilder stringToShow = new StringBuilder();
        for (Event event : listToShow)
        {
            stringToShow.append("\n");
            stringToShow.append(event.toString());
            stringToShow.append("\n");
        }
        stringToShow.append("\n\n");
        gameEvents.setText(stringToShow);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        gameChosen = position;
        listToShow = AppData.games.get(position).events;
        showGameEvent();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        gameChosen = -1;
        AppData.listToShow = null;
        showGameEvent();
    }
}