package models;

import com.google.gson.Gson;

public class GsonDataModel {
    public static void main(String[] args) {
        // Create a sample data object
        Data data = new Data(new Brakes(true, 80), 120);

        // Create an instance of the Gson class
        Gson gson = new Gson();

        // Convert the Java object to a JSON string
        String jsonString = gson.toJson(data);

        // Print the JSON string
        System.out.println("JSON String: " + jsonString);
    }

    private Data data;

    // CONSTRUCTOR
    public GsonDataModel() {
        this.data = new Data(new Brakes(false, 0), 0);
    }

    // SETTERS
    public void setBrakes(boolean activated, int temperature) {
        this.data.setBrakes(activated, temperature);
    }
    public void setSpeed(int speed) {
        this.data.setSpeed(speed);
    }
    public void toggleBrakes(boolean activated) {
        this.data.toggleBrakes(activated);
    }
    public void incrementSpeed() {
        this.data.incrementSpeed();
    }
    public void decrementSpeed() {
        this.data.decrementSpeed();
    }

    // GET JSON STRING
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this.data);
    }

    static class Data {
        private Brakes brakes;
        private int speed;

        public Data(Brakes brakes, int speed) {
            this.brakes = brakes;
            this.speed = speed;
        }

        public void setBrakes(boolean activated, int temperature) {
            brakes.setActivated(activated);
            brakes.setTemperature(temperature);
        }
        public void setSpeed(int speed) {this.speed = speed;}
        public void toggleBrakes(boolean activated) {this.brakes.setActivated(activated);}
        public void incrementSpeed() {if (this.speed < 100) this.speed++;}
        public void decrementSpeed() {if (this.speed > 0) this.speed--;}

        public int getSpeed() {return this.speed;}
        public boolean getActivated() {return this.brakes.getActivated();}
        public int getTemperature() {return this.brakes.getTemperature();}
    }

    static class Brakes {
        private boolean activated;
        private int temperature;

        public Brakes(boolean activated, int temperature) {
            this.activated = activated;
            this.temperature = temperature;
        }

        public void setActivated(boolean activated) {this.activated = activated;}
        public void setTemperature(int temperature) {this.temperature = temperature;}

        public boolean getActivated() {return this.activated;}
        public int getTemperature() {return this.temperature;}
    }
}
