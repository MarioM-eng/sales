package com.mmdevelopment.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "colors")
@Data
@NoArgsConstructor
@ToString(exclude = {"stocks"})
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Debe enviar el nombre del color")
    @Column(nullable = false, length = 45)
    private String name;

    @OneToMany(mappedBy = "color", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Stock> stocks;

    @Column(nullable = false)
    private boolean enabled;

    public Color(String name) {
        this.name = name;
    }

    @PrePersist
    protected void onCreate() {
        this.setEnabled(true);
    }

}
