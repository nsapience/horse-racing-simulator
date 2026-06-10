import java.util.Random;
import java.util.Arrays;

// no change since 5/27 6:56
public class raceMoves {
    Random rand = new Random();
    private int position;
    private int speed;
    private int dqChance;
    private double energy;

    private Horse horse;
    private preGame pregame;
    private int injrPos;
    private boolean injured;

    
    public raceMoves(preGame pregame, Horse[] horses){
        this.pregame = pregame;
        this.horse = pregame.getHorse();
        this.position = 0;
        this.injured = false;

        // basic starting speed based on horse traits
        this.speed =  horse.getSpeed() + horse.getStrength();

        // small track bonus so track type matters
        if (pregame.getTrackType().equals("Flat")) {
            speed += horse.getSpeed();
        }
        else if (pregame.getTrackType().equals("Muddy")) {
            speed += horse.getStrength();
        }
        else {
            speed += horse.getStamina();
        }

        // starting energy depends on stamina, strength, and training exhaustion
        this.energy = 400 + (horse.getStamina() * 45) + (horse.getStrength() * 20) - pregame.getExhaust();
        fixBounds();
        this.dqChance = (int) pregame.calcInjuryRisk();
        this.injrPos = rand.nextInt(300,1000);
       
    }

    public int getPosition(){
        return position;
    }

    public int getSpeed(){
        return speed;
    }

    public double getEnergy(){
        return energy;
    }

    public void injury(){
        // injury lowers energy once based on injury risk level
        if(dqChance == 1){
            energy -= 25;
        }
        else if(dqChance == 2){
            energy -= 50;
        }
        else if(dqChance == 3){
            energy -= 100;
        }
        else if(dqChance == 4){
            energy -= 175;
        }
        else{
            energy -= 250;
        }

        injured = true;
        fixBounds();
    }
    public String injuryWarning(){
        // warn only when injury can actually happen soon
        if ((dqChance == 4 || dqChance == 5) && energy <= 150 && energy > 100) {
            return "Injury risk rising. Slow down soon.";
        }

        return "";
    }
    
    public String injure(){
        // high-risk horses are injured when energy gets too low
        if ((dqChance == 4 || dqChance == 5) && energy <= 100) {
            injured = true;
            return "Horse injured. Game Over.";
        }

        return "";
    }

    public void speed(String i){
        /*
        1 = slow down
        2 = speed up
        */

        if (energy < 50) {
            speed -= 5;
        }

        else if (i.equals("1")){
            // slow down using division so it doesnt stay at 1 forever
            speed = speed/2;
        }

        else if (i.equals("2")){
            // speed up by a small amount
            speed += 5;
        }

        fixBounds();
    }

    public void exhaust(String i){
        double drainAmt = ((speed * 0.8) - (horse.getStamina() * 0.3) - (horse.getStrength() * 0.2)) 
                * (pregame.getExhaust() / 70.0);
        
        if (drainAmt < 1) {
            drainAmt = 1;
        }
        // speeding up burns more energy
        if (i.equals("2")) {
            drainAmt *= 1.5;
        }
        // slowing down conserves energy
        else if (i.equals("1")) {
            drainAmt *= 0.5;
        }
        energy -= drainAmt;
        fixBounds();
    }
    
    public void move(String i){
        position += speed;
        
        exhaust(i);
        fixBounds();
    }


    public int[] positions(int[] h){
        int[] places = new int[h.length+1];
        places[0] = getPosition();
        for (int i = 0; i<h.length; i++){
            places[i+1] = h[i];
        }
        return places;
    }

    public int place(int[] h){
        int place = 1;

        for(int pos : h){
            if(pos > position){
                place++;
            }
        }

        return place;
    }

    public boolean checkPositions(int[] h){
        return (checkWin(h) || checkOtherWin(h));
    }

    public boolean checkWin(int[] h){
        int[] places = positions(h);
        if (places[0] >= 1600){
            return true;
        }
        return false;
    }

    public boolean checkOtherWin(int[] h){
        int[] places = positions(h);
        for (int p:places){
            if (p >= 1600) return true;
            }
        return false;
    }

    public boolean checkEnergy(){
        if (energy <= 0){
            return true;
        }
        return false;

    }

    public void fixBounds(){
    // prevents speed from becoming negative or making horse move backward
    if (speed < 1) {
        speed = 1;
    }

    // prevents energy from displaying negative
    if (energy < 0) {
        energy = 0;
    }

    // caps position at the finish line
    if (position > 1600) {
        position = 1600;
    }
}
    public String warning(){
        // gives race advice based on current race condition
        if (position > 200 && energy < 100) {
            return "WARNING: Energy is critical. Slow down soon.";
        }
        else if (position > 200 && energy < 180) {
            return "Caution: Energy is dropping. Consider conserving pace.";
        }
        else if (position > 300 && pregame.getExhaust() > 60 && speed > 20) {
            return "Overtraining is affecting endurance. Pushing too hard is risky.";
        }
        else if (pregame.getTrackType().equals("Flat")) {
            return "Flat track: speed gives the strongest advantage.";
        }
        else if (pregame.getTrackType().equals("Long")) {
            return "Long track: stamina helps preserve energy.";
        }
        else {
            return "Muddy track: strength helps resist energy loss.";
        }
    }
    public String motion(String i, int[] h){
        speed(i);
        move(i);

        String injuryMessage = injure();

        if (!injuryMessage.equals("")) {
            return injuryMessage;
        }

        if(checkEnergy()) {
            return "You ran out of energy";
        }

        if(checkPositions(h)){
            if(checkWin(h)) {
                return "YOU WIN! You were first to reach the finish line!";
            }
            else if(checkOtherWin(h)) {
                return "Opponent won. Other player reached finish line.";
            }
        }

        return "";
    }
}
