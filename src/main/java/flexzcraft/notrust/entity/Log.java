package flexzcraft.notrust.entity;

import java.sql.Timestamp;

public class Log {

    private String playername;
    private double x;
    private double y;
    private double z;
    private String dimension;
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private String action;
    private String text;

    public Log(String playername, double x, double y, double z, String dimension, String action, String text) {
        this.playername = playername;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.action = action;
        this.text = text;
    }

    public Log(String playername, double x, double y, double z, String dimension, Timestamp timestamp, String action, String text) {
        this.playername = playername;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension;
        this.timestamp = timestamp;
        this.action = action;
        this.text = text;
    }

    public String getPlayername() {
        return playername;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getDimension() {
        return dimension;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getAction() {
        return action;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Log{" +
                "playername='" + playername + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", dimension='" + dimension + '\'' +
                ", timestamp=" + timestamp +
                ", action='" + action + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
