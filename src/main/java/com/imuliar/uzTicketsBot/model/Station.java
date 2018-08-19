package com.imuliar.uzTicketsBot.model;

import javax.persistence.Entity;

/**
 * <p>Carrying data about Station, retrieved from server.</p>
 * <p>Used for mapping from json-string and for persisting data in DB</p>
 *
 * @author imuliar
 * @since 1.0
 */
@Entity
public class Station extends EntityFrame {

    private String title;

    private String region;

    private String value;

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
        return "Station{" +
                "title='" + title + '\'' +
                ", region='" + region + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
