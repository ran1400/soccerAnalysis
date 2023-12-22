package com.soccerAnalyst.sharedData;


import android.util.Log;

import com.soccerAnalyst.EventsFragment;
import com.soccerAnalyst.GameFragment;
import com.soccerAnalyst.MainActivity;
import com.soccerAnalyst.SettingFragment;
import com.soccerAnalyst.utils.Event;
import com.soccerAnalyst.utils.Game;


import java.util.ArrayList;

import java.util.List;

public class AppData
{

    public static MainActivity mainActivity;
    public static GameFragment gameFragment;
    public static SettingFragment settingFragment;
    public static EventsFragment eventsFragment;
    public static List<Game> games;
    public static List<String> events;
    public static List<String> gamesStringList;
    public static  List<String> listToShow; //games or events in setting fragment
    public static Event.GamePart gamePartChosen ;
    public static Event.Team teamChosen;
    public static int playerChosenDigit1;
    public static int playerChosenDigit2;
    public static int min,sec;
    public static boolean clockRun;

    public static int listChoosePosition = -1; // for drag and dop list


    public static List<String> getGamesStringList()
    {
        List<String> res = null;
        try
        {
            res = new ArrayList<>();
            for(Game game : games)
                res.add(game.gameName);
        }
        catch (Exception e)
        {
            Log.d("ranCheck",e.toString());
        }
        return res;
    }

    /*public void addExamples() // for checks
    {
        eventsList.add("איבוד כדור");
        eventsList.add("גול");
        eventsList.add("פנדל");
        gamesList.addGame("מכבי חיפה - הפועל באר שבע");
        gamesList.addGame("מכבי פתח תקווה - הפועל באר שבע");
        gamesList.addGame("מכבי תל אביב - הפועל באר שבע");
        gamesList.addEvent(0, makeEvent(35, 56, half1, team1, null, 3));
        gamesList.addEvent(1, makeEvent(5, 16, half1, team2, 2, 1));
        gamesList.addEvent(1, makeEvent(25, 16, half2, team2, 22, 1));
    }*/

}