import java.util.*;
import com.googlecode.lanterna.TextColor;
import java.util.Random;

public class preGame{

    Random rand = new Random();
    private String trainingFocus;
    private int hoursTrained;
    private double exhaustion;
    private int trainingIntensity;
    private double injuryRisk;
    private Horse horseStats = new Horse();
    private String injLev;
    private String trackType;
    private UI.Panel window;

    public preGame(UI.Panel window){
        this.window = window;
    }

    public int calcTrainingIntensity(){
        // Training intensity is on a scale of 1 to 5, 5 most intense (high exhaustion) and 1 least intense (low exhaustion)
        if (hoursTrained ==0){
            trainingIntensity = 0;
        }
        else if (hoursTrained <= 4){
            trainingIntensity = 1;
        }
        else if (hoursTrained > 4 && hoursTrained <=8){
            trainingIntensity = 2;
        }
        else if (hoursTrained > 8 && hoursTrained <=12){
            trainingIntensity = 3;
        }
        else if (hoursTrained > 12 && hoursTrained <=16){
            trainingIntensity = 4;
        }
        else {
            trainingIntensity = 5;
        }
        return trainingIntensity;
    }
 
 
    public double calcExhaustion(){
        // purpose is to identify the exhaustion propurtion required for race conditions
        // hours trained and exhaustrion are inversly proportional
        double exhaustionValue;
        exhaustionValue = hoursTrained * trainingIntensity * 1.5;

        // cap exhaustion at 100
        if (exhaustionValue > 100){
            exhaustionValue = 100;
        }

        return exhaustionValue;
        
    }
    public double calcInjuryRisk(){
        double risk;

        // risk is calculated on a scale from 1 to 5, guarantees an injury of some kind
        if (exhaustion <= 20) {
            risk = 1;
            injLev = "Low";
            // low chance of injury, if it happens it is likely very minor
        }
        else if (exhaustion > 20 && exhaustion <=40) {
            risk = 2;
            injLev = "Fairly Low";
            // moderate injury chance
        }
        else if (exhaustion > 40 && exhaustion <=60) {
            risk = 3;
            injLev = "Moderate";
            // risk at 3 means that the horse has a fairly high chance of getting an injury
        }
        else if (exhaustion > 60 && exhaustion <=80) {
            risk = 4;
            injLev = "High";
            // risk at 4 means that the horse has a high chance of getting an injury
        }
        else{
            risk = 5; 
            injLev = "Very High";
            // risk at 5 means that the horse will have an injury as soon as the exhaustion reaches 100
        }
        return risk;
    }
    
    public String trackAssign(){
        // different tracks favor different traits
        // Flat favors speed; muddy favors strength, long favors stamina 
        String[] tracks = {"Flat", "Muddy", "Long"};
        int randTrack = rand.nextInt(tracks.length);
        trackType = tracks[randTrack];
        return trackType;
    }
    public int trackBonus(){
        int bonus = 0;
        if(trackType.equals("Flat")){
            bonus = horseStats.getSpeed() * 2;
        }
        else if(trackType.equals("Muddy")){
            bonus = horseStats.getStrength() * 2;
        }     
        else {
            bonus = horseStats.getStamina() * 2;
        }   
        return bonus;
    }
    public void raceSetupUI(UI ui){

        window.println("RACE SETUP", TextColor.ANSI.YELLOW);
        window.println("TrackType: "+trackType);

        if(trackType.equals("Flat")){
            window.println("Speed is favored!");
        }
        else if(trackType.equals("Muddy")){
            window.println("Strength is favored!");
        }
        else{
            window.println("Stamina is favored!");
        }
        window.println("");
        window.input("Press enter to continue to the race");
        window.resetTextBox();
    }
    // creates the display and handles user inputs for the beginning of the game
    public void trainHorseUI(UI ui) {
        //window.resetTextBox();

        int totalHoursUsed = 0;
        boolean keepTraining = true;

        while (keepTraining && totalHoursUsed < 20) {

            int oldSpeed = horseStats.getSpeed();
            int oldStamina = horseStats.getStamina();
            int oldStrength = horseStats.getStrength();

            window.println("Choose your training focus:");
            window.println("1) strength");
            window.println("2) stamina");
            window.println("3) speed");

            trainingFocus = window.input("Enter choice: ").toLowerCase();

            while (!trainingFocus.equals("1") && !trainingFocus.equals("2") && !trainingFocus.equals("3")) {
                window.println("Invalid choice. Enter 1, 2, or 3:", TextColor.ANSI.RED);
                trainingFocus = window.input("Enter choice: ").toLowerCase();
            }

            int hoursLeft = 20 - totalHoursUsed;

            window.println("How many hours? 0 to " + hoursLeft);
            int currentHours = Integer.parseInt(window.input("Hours: "));

            while (currentHours < 0 || currentHours > hoursLeft) {
                window.println("Invalid number. Enter 0 to " + hoursLeft + ":", TextColor.ANSI.RED);
                currentHours = Integer.parseInt(window.input("Hours: "));
            }

            if (currentHours == 0) {
                window.println("No training selected.");
            }
            else {
                hoursTrained = currentHours;
                trainingIntensity = calcTrainingIntensity();

                window.println("Your horse is being trained...");
                ui.refresh();
                ui.wait(currentHours * 300);

                horseStats.trainStat(trainingFocus, currentHours, trainingIntensity);

                totalHoursUsed += currentHours;

                int newSpeed = horseStats.getSpeed();
                int newStamina = horseStats.getStamina();
                int newStrength = horseStats.getStrength();

                window.println("Training complete!", TextColor.ANSI.GREEN);

                if (trainingFocus.equals("1")) {
                    window.println("Strength: " + oldStrength + " -> " + newStrength);
                }
                else if (trainingFocus.equals("2")) {
                    window.println("Stamina: " + oldStamina + " -> " + newStamina);
                }
                else {
                    window.println("Speed: " + oldSpeed + " -> " + newSpeed);
                }

                window.println("Hours used: " + totalHoursUsed + " / 20");
            }

            if (totalHoursUsed < 20) {
                String again = window.input("Train another skill? y/n: ").toLowerCase();

                while (!again.equals("y") && !again.equals("n")) {
                    again = window.input("Enter y or n: ").toLowerCase();
                }

                if (again.equals("n")) {
                    keepTraining = false;
                }

                window.resetTextBox();
            }
        }

        hoursTrained = totalHoursUsed;
        trainingIntensity = calcTrainingIntensity();
        exhaustion = calcExhaustion();
        injuryRisk = calcInjuryRisk();

        window.input("Press enter to see updated card");
        window.resetTextBox();
    }
    public void displayCardUI(UI ui) {
        TextColor horseColor = horseStats.getHorseTextColor();
        window.resetTextBox();
        if (hoursTrained == 0){
            window.println("*** YOUR HORSE *** ");
            window.println("                 >>\\.", horseColor);
            window.println("                /_  )`.", horseColor);
            window.println("               /  _)`^)`.   _.---. _", horseColor);
            window.println("              (_,' \\  `^-)\"\"      `.\\\\", horseColor);
            window.println("                    |              | \\\\", horseColor);
            window.println("                    \\              / |", horseColor);
            window.println("                   / \\  /.___.'\\  (", horseColor);
            window.println("                  < ,\"||     \\ |`. \\", horseColor);
            window.println("                   \\\\ ()      )|  )/", horseColor);
            window.println("                    |_>|     /_] //", horseColor);
            window.println("                     /_]        /_] ", horseColor);
        }
        else{
            window.println("*** UPDATED HORSE STATS ***",TextColor.ANSI.GREEN);
        }
        
        window.println("Name: " + horseStats.getName());
        window.println("Color: " + horseStats.getColor());
        window.println("Gender: " + horseStats.getGender());
        window.println("");
        window.println("Strength: " + horseStats.getStrength()+"/10");
        window.println("Stamina: " + horseStats.getStamina()+"/10");
        window.println("Speed: " + horseStats.getSpeed()+"/10");
        
    
        if (hoursTrained > 0) {
            window.println("*** TRAINING RESULTS ***", TextColor.ANSI.GREEN);
            String foc;
            if (trainingFocus.equals("1")){
                foc = "Strength";
            }
            else if(trainingFocus.equals("2")){
                foc = "Stamina";
            }
            else{
                foc = "Speed";
            }
            window.println("Focus: " + foc);
            window.println("Hours: " + hoursTrained);
            window.println("Exhaustion 1 to 100: " + exhaustion);
            window.println("Injury Risk: " + injLev +" "+ injuryRisk);
        }
    
        window.println("");
        window.input("Press enter to continue");
    
    }
    public Horse getHorse(){ return horseStats;}
    public double getExhaust(){return exhaustion;}
    public String getTrackType() {return trackType;}

    public static void main(String[] args) {
        System.out.println("Run the game class to start the program.");
    }

}
