package com.imuliar.uzTicketsBot.services.states;

/**
 * <p>Dto for carrying data about Station, retrieved from server.</p>
 * <p>Used for mapping from json-string</p>
 *
 * @author imuliar
 * @since 1.0
 */
public class StationDto {

    String title;

    String region;

    String value;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "StationDto{" +
                "title='" + title + '\'' +
                ", region='" + region + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
