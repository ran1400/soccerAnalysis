package ran.tmpTest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import ran.tmpTest.R;

import ran.tmpTest.sharedData.AppData;
import ran.tmpTest.utils.Event;
import ran.tmpTest.utils.saveInMemoryLists.EventsList;
import ran.tmpTest.utils.saveInMemoryLists.GamesList;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
{

    private FragmentManager fm = getFragmentManager();

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;

    private View mainActivityView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivityView = findViewById(R.id.mainActivity);
        AppData.mainActivity = this;
        sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        getDataFromMemory();
        clockCheck();
        AppData.gameFragment = new GameFragment();
        loadFragment(AppData.gameFragment);
    }


    protected void onPause()
    {
        super.onPause();
        saveDataToMemory("events",new EventsList(AppData.events));
        saveDataToMemory("games",new GamesList(AppData.games));
        editor.putBoolean("clockRun",AppData.clockRun);
        if (AppData.clockRun)
        {
            editor.putInt("min", AppData.min);
            editor.putInt("sec", AppData.sec);
            editor.putLong("time", System.currentTimeMillis());
        }
        saveDataToMemory("teamChosen", AppData.teamChosen);
        saveDataToMemory("gamePartChosen",AppData.gamePartChosen);
        editor.putInt("gameChosenGameFragment",GameFragment.gameChosen);
        editor.putInt("gameChosenEventsFragment",EventsFragment.gameChosen);
        editor.putInt("playerChosenDigit1",AppData.playerChosenDigit1);
        editor.putInt("playerChosenDigit2",AppData.playerChosenDigit2);
        editor.commit();
        Log.d("mainActivity", " : pause");
    }

    protected void onResume()
    {
        super.onResume();
        clockCheck();
        Log.d("mainActivityCheck", " : resume");
    }

    private void getDataFromMemory()
    {
        EventsList events = getDataFromMemory("events",EventsList.class);
        if (events == null)
            AppData.events = new ArrayList<>();
        else
            AppData.events = events.list;
        GamesList games = getDataFromMemory("games",GamesList.class);
        if ( games == null)
            AppData.games = new ArrayList<>();
        else
            AppData.games = games.list;
        AppData.makeGamesStringList();
        GameFragment.gameChosen = sharedPreferences.getInt("gameChosenGameFragment",-1);
        EventsFragment.gameChosen = sharedPreferences.getInt("gameChosenEventsFragment",-1);
        AppData.clockRun = sharedPreferences.getBoolean("clockRun",false);
        Event.GamePart gamePartChosen = getDataFromMemory("gamePartChosen", Event.GamePart.class);
        if (gamePartChosen == null)
            AppData.gamePartChosen = Event.GamePart.HALF_1;
        else
            AppData.gamePartChosen = gamePartChosen;
        Event.Team teamChosen = getDataFromMemory("teamChosen", Event.Team.class);
        if (teamChosen == null)
            AppData.teamChosen = Event.Team.NON;
        else
            AppData.teamChosen = teamChosen;
        AppData.playerChosenDigit1 = sharedPreferences.getInt("playerChosenDigit1",0);
        AppData.playerChosenDigit2 = sharedPreferences.getInt("playerChosenDigit2",0);
    }

    protected void clockCheck()
    {
        if ( AppData.clockRun )
        {
            AppData.min = sharedPreferences.getInt("min", 0);
            AppData.sec = sharedPreferences.getInt("sec", 0);
            long pastTime = sharedPreferences.getLong("time", 0);
            int timeToAdd = (int) ((System.currentTimeMillis() - pastTime) / 1000);
            AppData.sec = sharedPreferences.getInt("sec", 0) + (timeToAdd % 60);
            if (AppData.sec >= 60)
            {
                AppData.sec -= 60;
                AppData.min =  (timeToAdd / 60) + 1;
            }
            else
                AppData.min += (timeToAdd / 60) ;
            if (AppData.min > 999 )
            {
                AppData.clockRun = false;
                AppData.min = 0;
                AppData.sec = -1;
            }
        }
        else
        {
            AppData.min = 0;
            AppData.sec = -1;
        }
    }

    public void showSnackBar(String msg, int time)
    {
        Snackbar snackBar = Snackbar.make(mainActivityView, msg, Snackbar.LENGTH_SHORT);
        snackBar.setAction("", null).setDuration(time).show();
    }

    private void loadFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    public void gameBtn(View view)
    {
        if (AppData.gameFragment == null)
            AppData.gameFragment = new GameFragment();
        loadFragment(AppData.gameFragment);
    }

    public void eventsBtn(View view)
    {
        if (AppData.eventsFragment == null)
            AppData.eventsFragment = new EventsFragment();
        loadFragment(AppData.eventsFragment);
    }

    public void settingBtn(View view)
    {
        AppData.listToShow = AppData.events;
        if (AppData.settingFragment == null)
            AppData.settingFragment = new SettingFragment();
        loadFragment(AppData.settingFragment);
    }

    public <T> void saveDataToMemory(String key,T data)
    {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(key, json);
        editor.commit();
    }

    public <T> T getDataFromMemory(String key,Type type)
    {
        T data = null;
        String serializedObject = sharedPreferences.getString(key, null);
        if (serializedObject != null)
        {
            Gson gson = new Gson();
            data = gson.fromJson(serializedObject, type);
        }
        return data;
    }
}