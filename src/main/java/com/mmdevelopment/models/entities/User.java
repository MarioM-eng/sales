package com.mmdevelopment.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Column(name = "user_name", nullable = false, length = 45, unique = true)
    private String userName;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min=3, max=1000, message = "La contraseña debe tener al menos 3 caracteres")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false, foreignKey = @ForeignKey(name = "fk_users_profile"))
    private Profile profile;

    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.setCreatedAt(LocalDateTime.now());
        this.setUpdatedAt(LocalDateTime.now());
    }

    public boolean isStore() {
        return (this.getId() != null) ? true : false;
    }


}