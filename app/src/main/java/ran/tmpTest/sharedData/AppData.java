package ran.tmpTest.sharedData;


import android.util.Log;

import ran.tmpTest.EventsFragment;
import ran.tmpTest.GameFragment;
import ran.tmpTest.MainActivity;
import ran.tmpTest.SettingFragment;
import ran.tmpTest.utils.Event;
import ran.tmpTest.utils.Game;


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
    //public static  List<String> listToShow; //games or events in setting fragment
    public static Event.GamePart gamePartChosen ;
    public static Event.Team teamChosen;
    public static int playerChosenDigit1;
    public static int playerChosenDigit2;
    public static int min,sec;
    public static boolean clockRun;

    public static int listChoosePosition = -1; // for drag and dop list


    public static void makeGamesStringList()
    {
        gamesStringList = new ArrayList<>();
        for(Game game : games)
            gamesStringList.add(game.gameName);
    }

}