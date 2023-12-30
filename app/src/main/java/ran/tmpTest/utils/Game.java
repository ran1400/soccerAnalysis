package ran.tmpTest.utils;

import java.util.ArrayList;
import java.util.List;

public class Game
{
    public  String gameName;
    public List<Event> events;
    public Game(String gameName)
    {
        this.gameName = gameName;
        events = new ArrayList<>();
    }
}
