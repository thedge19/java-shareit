package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.practicum.shareit.exception.Marker;

import java.util.Objects;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @NotEmpty(groups = Marker.OnCreate.class)
    @Column(name = "user_name")
    private String name;
    @NotEmpty(groups = Marker.OnCreate.class)
    @Email(groups = Marker.OnCreate.class)
    private String email;
}
