package com.imuliar.uzTicketsBot.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * <p>Defines common for all entities properties and behavior.</p>
 *
 * @author imuliar
 * @since 1.0
 */
public abstract class EntityFrame {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
