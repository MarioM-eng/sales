package com.mmdevelopment.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prices")
@Data
@NoArgsConstructor
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El valor del precio es obligatorio")
    @Column(name = "price_value", nullable = false, length = 45)
    private Double value;

    @ManyToOne()
    @JoinColumn(name = "price_type_id", nullable = false)
    private PriceType priceType;

    @ManyToOne
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.setCreatedAt(LocalDateTime.now());
    }

}
