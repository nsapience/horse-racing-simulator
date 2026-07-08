import com.googlecode.lanterna.TextColor;

public class FinalProject {
    private UI ui;
    private preGame pg;
    private raceUI race = new raceUI();
    private UI.Panel window;
    private String racerName;
    private static final int RACE_DISTANCE = 1000; // meters
    public FinalProject(){
        ui = new UI(140, 55);
        window = ui.makePanel(1, 1, 130, 50);
        pg = new preGame(window);
    }

    public void startGame() {

        window.println("-----------------------------------", TextColor.ANSI.BLUE_BRIGHT);
        window.println("       WELCOME TO HORSE RACING!    ", TextColor.ANSI.MAGENTA_BRIGHT);
        window.println("-----------------------------------", TextColor.ANSI.BLUE_BRIGHT);
        racerName = window.input("What is your name? ");
        window.println("Hello " + racerName + "!");
        window.println("Meet your horse.");
        ui.wait(2000);
        window.resetTextBox();

        ui.clear();
        ui.refresh();

        pg.displayCardUI(ui);

        //ui.clear();
        //ui.refresh();

        pg.trainHorseUI(ui);

        ui.clear();
        ui.refresh();

        pg.displayCardUI(ui);

        race.competitors(window);

        // ui.clear();
        // ui.refresh();

        pg.trackAssign();
        pg.raceSetupUI(ui);


        window.println("*** RACE CONTROLS ***", TextColor.ANSI.YELLOW);
        window.println("1 = slow down and save energy");
        window.println("2 = speed up and risk more energy");
        window.println("Enter = keep steady pace");
        window.println("");
        window.input("Press enter to begin the race");

        race();

        ui.stopUI();
    }

    public String getRacerName() {return racerName;}
    public void raceCompetitors(){
        race.competitors(window);
    }
    public void printStats(raceMoves rm,int[] h, String message){
        // show the user their stats 
        window.println("");
        window.println("Current Stats:", TextColor.ANSI.MAGENTA_BRIGHT);
        window.println("    Place:    " + rm.place(h));
        window.println("    Distance: " +getDistanceMeters(rm) +" / " +RACE_DISTANCE +" m");
        window.println("    Speed:    "+ rm.getSpeed());
        window.println("    Energy:   "+ rm.getEnergy());
        window.println("");
        window.println(rm.warning(), TextColor.ANSI.YELLOW);
        window.println("");
        window.println(message, TextColor.ANSI.CYAN);
        String injuryMsg = rm.injuryWarning();

        if(!injuryMsg.equals("")){
        window.println(injuryMsg, TextColor.ANSI.RED);
}
    }

    public void screen(raceMoves rm, int count, int h, int[] h1, int[] h2, int[] h3, int[] h4, int[] ho, String message){
        race.drawRaceScreen(ui, window, h/20, h1[count]/20, h2[count]/20, h3[count]/20, h4[count]/20, pg.getTrackType(), racerName);
        printStats(rm, ho, message);
        ui.refresh();
    }
    
    public void race() {
        // defines competitors positions from the start
        boolean competitorWon = false;
        int[] ho = new int[4];
        raceMoves rm = new raceMoves(pg, race.getCompetitors());

        Competitors rh1 = new Competitors();
        int[] h1 = rh1.getpositions();

        Competitors rh2 = new Competitors();
        int[] h2 = rh2.getpositions();

        Competitors rh3 = new Competitors();
        int[] h3 = rh3.getpositions();

        Competitors rh4 = new Competitors();
        int[] h4 = rh4.getpositions();

        int count = 0;
        boolean raceOver = false;

        // race intro
        
        window.println("*** WELCOME TO THE TRACK! ***", TextColor.ANSI.YELLOW);
        window.println("");
        window.println("The horses are lined up at the gate.");
        window.println("Track: " + pg.getTrackType(), TextColor.ANSI.CYAN);
        window.println("");
        window.input("Press enter to begin racing!");

        String message = "MAKE YOUR MOVE TO BEGIN";
        String finalResult = "Race ended.";

        while (!raceOver && count < 295) {

            ho[0] = h1[count];
            ho[1] = h2[count];
            ho[2] = h3[count];
            ho[3] = h4[count];

            window.resetTextBox();

            // display current race state
            screen(rm, count, rm.getPosition(), h1, h2, h3, h4, ho, message);

            ui.wait(250);

            // player decision
            String choice = window.input(
                "Enter 1[to slow down], 2[to speed up], or [enter] to keep moving: "
            ).toLowerCase();

            while (!choice.equals("1") &&!choice.equals("2") &&!choice.equals("")) {
                window.println("Invalid choice. Enter 1, 2, or [enter].",TextColor.ANSI.RED);
                choice = window.input("Enter choice: ").toLowerCase();
            }

            if (choice.equals("1")) {
                message = "You slowed down to conserve energy.";
            }
            else if (choice.equals("2")) {
                message = "You pushed forward aggressively.";
            }
            else {
                message = "You kept a steady pace.";
            }

            // apply choice once
            String result = rm.motion(choice, ho);

            // animate between checkpoints
            for (int frame = 0; frame < 12 && count < 295; frame++) {

                count++;

                ho[0] = h1[count];
                ho[1] = h2[count];
                ho[2] = h3[count];
                ho[3] = h4[count];

                // continue normal movement
                result = rm.motion("", ho);

                if (result.equals("Opponent won. Other player reached finish line.")) {
                    competitorWon = true;
                    message = "A competitor finished first. Keep racing!";
                    result = "";
                
                    // if player is out of energy, end race now
                    if(rm.getEnergy() <= 0){
                        result = "You ran out of energy!";
                    }
                    else{
                        result = "";
                    }
                }
                window.resetTextBox();

                screen(rm,count,rm.getPosition(),h1,h2,h3,h4,ho,message);

                ui.wait(500);
            }

            if (!result.equals("")) {
                ui.wait(3000);
                if (competitorWon && result.contains("YOU WIN")) {
                    finalResult = "Good job for finishing! Another competitor finished first.";
                }
                else {
                    finalResult = result;
                }

                raceOver = true;
            }

                
        }

        showPostRaceSummary(rm, ho, finalResult, count, count);

        String again = window.input("Play again? (y/n): ").toLowerCase();

        while (!again.equals("y") && !again.equals("n")) {
            again = window.input("Enter y or n: ").toLowerCase();
        }

        if (again.equals("y")) {
            ui.stopUI();

            FinalProject newGame = new FinalProject();
            newGame.startGame();
        }
        else {
            ui.stopUI();
        }
    }
    public int getDistanceMeters(raceMoves rm) {
        return (int)((rm.getPosition() / 295.0) * RACE_DISTANCE);
    }

    public void showPostRaceSummary(raceMoves rm, int[] ho, String result, int checkpointsUsed, int maxChoices) {
        Horse playerHorse = pg.getHorse();
        window.resetTextBox();
        window.println("*** POST-RACE SUMMARY ***", TextColor.ANSI.YELLOW);
        window.println("Outcome: " + result, TextColor.ANSI.BLUE_BRIGHT);
        window.println("");
        window.println("Race Details:");
        window.println("    Track Type: " + pg.getTrackType());
        window.println("    Checkpoints Used: " + checkpointsUsed + " / " + maxChoices);
        window.println("    Final Speed: " + rm.getSpeed());
        window.println("    Final Energy: " + String.format("%.2f", rm.getEnergy()));
        window.println("");
        window.println("Your Horse:");
        window.println("    Name: " + playerHorse.getName());
        window.println("    Color: " + playerHorse.getColor());
        window.println("    Gender: " + playerHorse.getGender());
        window.println("    Strength: " + playerHorse.getStrength());
        window.println("    Stamina: " + playerHorse.getStamina());
        window.println("    Speed: " + playerHorse.getSpeed());
        window.println("");
        window.input("Press enter to finish");
}

    public static void main(String[] args){
        FinalProject g = new FinalProject();
        g.startGame();
        
        
    }
}
