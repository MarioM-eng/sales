package com.mmdevelopment.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "sizes")
@Data
@NoArgsConstructor
@ToString(exclude = {"stocks"})
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Debe enviar el nombre del tamaño")
    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "size", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Stock> stocks;

    public Size(String name) {
        this.name = name;
    }

    @PrePersist
    protected void onCreate() {
        this.setEnabled(true);
    }

}
