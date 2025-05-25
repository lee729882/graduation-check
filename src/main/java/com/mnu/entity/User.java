package com.mnu.entity;

import jakarta.persistence.*;     // JPA 관련 import
import lombok.*;                 // Lombok 관련 import

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String major;

    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = true, unique = true)
    private String studentId;

}
