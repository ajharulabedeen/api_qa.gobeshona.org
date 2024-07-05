package com.bezkoder.springjwt.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users"
//    uniqueConstraints = {
//      @UniqueConstraint(columnNames = "username"),
//      @UniqueConstraint(columnNames = "email")
//    }
    )
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 20)
  private String username;

//  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 120)
  private String password;

  @Column(nullable=true)
  private String name;

  @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
  @JoinTable(
          name="users_roles",
          joinColumns={@JoinColumn(name="USER_ID", referencedColumnName="ID")},
          inverseJoinColumns={@JoinColumn(name="ROLE_ID", referencedColumnName="ID")})
  private Set<Role> roles = new HashSet<>();

  public User(String username, String name, String email, String password) {
    this.name = name;
    this.username = username;
    this.email = email;
    this.password = password;
  }
}
