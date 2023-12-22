package ran.tmpTest.utils;

public class Event
{
    public enum GamePart {HALF_1,HALF_2,EXTRA_TIME_1,EXTRA_TIME_2}
    public enum Team {HOME_TEAM,NON,AWAY_TEAM}
    GamePart gamePart;
    Team team;
    String time;
    int playerNum;
    String eventName;


    public Event(GamePart gamePart,Team team,int min,int sec,int playerNum,String eventName)
    {
        this.team = team;
        this.playerNum = playerNum;
        this.eventName = eventName;
        this.gamePart = gamePart;
        if (min  > 9)
            time = min + ":";
        else
            time = "0" + min + ":";
        if (sec > 9)
            time += sec;
        else
            time += "0" + sec;
    }

    public String toString()
    {
        String res;
        if (gamePart == GamePart.EXTRA_TIME_2)
            res = "ET2 ";
        else if (gamePart == GamePart.HALF_2)
            res = "H2 ";
        else if (gamePart == GamePart.EXTRA_TIME_1)
            res = "ET1 ";
        else // (gamePart == GamePart.half1)
            res = "H1 ";
        res += time;

        if (team == null && playerNum == 0) //team and player are null
            return res + ":" + eventName;
        if (team != Team.NON)
        {
            if (this.team == Team.HOME_TEAM)
                res += " (home team";
            else //(team == Team.AWAY_TEAM)
                res += " (away team";
            if (playerNum == 0) //team is not null and player is null
                return res + ") : " + eventName;
            else
                return res + " P" + playerNum + ") : " + eventName;
        }
        else if (playerNum != 0) //team is null and player is not 0
            return res + " (P" + playerNum + ")" + " : " + eventName;
        //team and player are not null
        return res + " : " + eventName;
    }
}
