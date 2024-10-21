package com.mmdevelopment.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@ToString(exclude = {"product", "prices"})
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "La cantitdad es obligatoria")
    @Column(name = "quantity_on_hand", nullable = false)
    private Integer quantityOnHand;

    @NotNull(message = "La cantitdad mínima es obligatoria")
    @Column(name = "reorder_point")
    private Integer reorderPoint;

    @Column(nullable = false)
    private boolean enabled;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "El producto es obligatorio")
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "El tamaño es obligatorio")
    @ManyToOne
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @NotNull(message = "El color es obligatorio")
    @ManyToOne
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @NotNull(message = "Todos los precios son obligatorios")
    @OneToMany(mappedBy = "stock", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Price> prices;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.setCreatedAt(LocalDateTime.now());
        this.setUpdatedAt(LocalDateTime.now());
        this.setEnabled(true);
    }
}