import java.util.Random; 
import com.googlecode.lanterna.TextColor;

public class raceUI { 
    Random rand = new Random(); 
    // genernate random 4 other horse competitors
    private Horse[] competitors = new Horse[4];

    public raceUI() {
        for(int i = 0; i < competitors.length; i++){
            competitors[i] = new Horse();
        }
    }
    public String random(String[] stringList){
        int randomNum = rand.nextInt(stringList.length);
        return stringList[randomNum];
    }

    // NOTE TO MAYBE ADD HORSE IMAGE LATER ON IF NEEDED

    public void competitors(UI.Panel window) {
        window.resetTextBox();
        window.println("*** HERE ARE YOUR COMPETITORS ***", TextColor.ANSI.YELLOW);
        window.println("");
        window.println("You: your trained horse", TextColor.ANSI.BLUE_BRIGHT);

        for(int i = 0; i < competitors.length; i++){
            // print a C_ name for each of the competitor horses to display during race
            window.println("C" + (i+1) + ": "+ competitors[i].getName());
        }

        window.println("");

        window.input("Please enter to continue");
    }
    public Horse[] getCompetitors(){
        return competitors;
    }
    public int calcHorseMove(Horse horse, String trackType, double exhaustion){
        // calculates how far a horse moves based on track type and stats
        int move = rand.nextInt(3) +1;
        if (trackType.equals("Flat")){
            move += horse.getSpeed()/4;
        }
        else if(trackType.equals("Muddy")){
            move += horse.getStrength()/4;
        }
        else{
            move += horse.getStamina()/4;
        }
        return move;

    }
    public void drawRaceScreen(UI ui, UI.Panel window, int playerPos, int c1, int c2, int c3, int c4, String trackType, String racerName) {
        window.resetTextBox();

        int finish = 80;

        window.println("RACE TRACK!", TextColor.ANSI.YELLOW);
        window.println("Track: " + trackType, TextColor.ANSI.CYAN);
        window.println("First horse to reach the finish line wins.");
        window.println("");

        window.println(spaces(finish) + "FINISH", TextColor.ANSI.RED);

        // display all the horses with their colors for further enhancement
        window.println("P1  |" + spaces(playerPos) + laneLabel(racerName) + spaces(finish-playerPos), TextColor.ANSI.GREEN);

        window.println("C1  |" + spaces(c1) + "1" + spaces(finish-c1), TextColor.ANSI.RED);
        window.println("C2  |" + spaces(c2) + "2" + spaces(finish-c1), TextColor.ANSI.BLUE_BRIGHT);
        window.println("C3  |" + spaces(c3) + "3" + spaces(finish-c1) , TextColor.ANSI.MAGENTA);
        window.println("C4  |" + spaces(c4) + "4" + spaces(finish-c1) , TextColor.ANSI.CYAN);
        window.println("");
    }

    // This method is for basic animation
    // Adds more spaces to the left of the charcter so it appears as though the horse is moving
    public String spaces(int count) {
        String s = "";

        if (count < 0) {
            count = 0;
        }

        for (int i = 0; i < count; i++) {
            s += " ";
        }

        return s;
    }
    public String laneLabel(String name) {
        // use first initial if name is too long
        if (name.length() > 3) {
            name = name.substring(0, 1);
        }
        while (name.length() < 3) {
            name += " ";
        }
        return name;
    }
    public void testRaceScreen(UI ui, UI.Panel racePanel, String trackType, String racerName) {
        int playerPos = 0;
        int c1 = 0;
        int c2 = 0;
        int c3 = 0;
        int c4 = 0;

        int finish = 53;

        while (playerPos < finish && c1 < finish && c2 < finish && c3 < finish && c4 < finish) {
            drawRaceScreen(ui, racePanel, playerPos, c1, c2, c3, c4, trackType, racerName);

            ui.wait(400);

            playerPos += rand.nextInt(3) + 1;
            c1 += rand.nextInt(3) + 1;
            c2 += rand.nextInt(3) + 1;
            c3 += rand.nextInt(3) + 1;
            c4 += rand.nextInt(3) + 1;
        }

        racePanel.resetTextBox();

        if (playerPos >= finish) {
            ui.setString(2, 5, "Race finished: You won!", TextColor.ANSI.GREEN);
        }
        else {
            ui.setString(2, 5, "Race finished: A competitor won.", TextColor.ANSI.RED);
        }

        ui.setString(2, 7, "Press q to continue.");
        ui.refresh();
        ui.waitForKey('q');
    }
    public void raceTrack(UI ui, UI.Panel racePanel){
        int playerPose = 0;
        int comp1 = 0;
        int comp2 = 0;
        int comp3 = 0;
        int comp4 = 0;
        
        while(playerPose<25){
            racePanel.resetTextBox();
            ui.setString(2,1, "HORSE RACE", TextColor.ANSI.YELLOW);
            ui.setString(playerPose, 5, "***");

            // competitors
            ui.setString(comp1,8,"A");
            ui.setString(comp2,11,"B");
            ui.setString(comp3,14,"C");
            ui.setString(comp4,17,"D");
            
            ui.wait(300);

            // movement
            playerPose += rand.nextInt(3);

            comp1 += rand.nextInt(3);
            comp2 += rand.nextInt(3);
            comp3 += rand.nextInt(3);
            comp4 += rand.nextInt(3);
        }

            ui.setString(2,20,"Race Finished!",TextColor.ANSI.GREEN);

            ui.refresh();

            ui.waitForKey('q');
        }
    }