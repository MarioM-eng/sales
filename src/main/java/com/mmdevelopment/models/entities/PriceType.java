package com.mmdevelopment.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "price_types")
@Data
@NoArgsConstructor
public class PriceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false, length = 80)
    private String description;

    @Column(nullable = false, length = 45)
    private String prefix;

    public PriceType(String name, String description, String prefix) {
        this.name = name;
        this.description = description;
        this.prefix = prefix;
    }
}
