
package com.example.application;

import java.io.Serializable;

public class Main implements Serializable
{

    private Double temp;
    private Double feels_like;
    private Double temp_min;
    private Double temp_max;
    private Integer pressure;
    private Integer humidity;
    private Integer sea_level;
    private Integer grnd_level;
    private final static long serialVersionUID = -8897109068044034199L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Main() {
    }

    /**
     * 
     * @param temp
     * @param temp_min
     * @param grnd_level
     * @param humidity
     * @param pressure
     * @param sea_level
     * @param feels_like
     * @param temp_max
     */
    public Main(Double temp, Double feels_like, Double temp_min, Double temp_max, Integer pressure, Integer humidity, Integer sea_level, Integer grnd_level) {
        super();
        this.temp = temp;
        this.feels_like = feels_like;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.pressure = pressure;
        this.humidity = humidity;
        this.sea_level = sea_level;
        this.grnd_level = grnd_level;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(Double feels_like) {
        this.feels_like = feels_like;
    }

    public Double getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(Double temp_min) {
        this.temp_min = temp_min;
    }

    public Double getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(Double temp_max) {
        this.temp_max = temp_max;
    }

    public Integer getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getSea_level() {
        return sea_level;
    }

    public void setSea_level(Integer sea_level) {
        this.sea_level = sea_level;
    }

    public Integer getGrnd_level() {
        return grnd_level;
    }

    public void setGrnd_level(Integer grnd_level) {
        this.grnd_level = grnd_level;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Main.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("temp");
        sb.append('=');
        sb.append(((this.temp == null)?"<null>":this.temp));
        sb.append(',');
        sb.append("feels_like");
        sb.append('=');
        sb.append(((this.feels_like == null)?"<null>":this.feels_like));
        sb.append(',');
        sb.append("temp_min");
        sb.append('=');
        sb.append(((this.temp_min == null)?"<null>":this.temp_min));
        sb.append(',');
        sb.append("temp_max");
        sb.append('=');
        sb.append(((this.temp_max == null)?"<null>":this.temp_max));
        sb.append(',');
        sb.append("pressure");
        sb.append('=');
        sb.append(((this.pressure == null)?"<null>":this.pressure));
        sb.append(',');
        sb.append("humidity");
        sb.append('=');
        sb.append(((this.humidity == null)?"<null>":this.humidity));
        sb.append(',');
        sb.append("sea_level");
        sb.append('=');
        sb.append(((this.sea_level == null)?"<null>":this.sea_level));
        sb.append(',');
        sb.append("grnd_level");
        sb.append('=');
        sb.append(((this.grnd_level == null)?"<null>":this.grnd_level));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
