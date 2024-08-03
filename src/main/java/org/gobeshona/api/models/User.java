package org.gobeshona.api.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotEmpty(message = "First name is required")
  private String firstName;

  @NotEmpty(message = "Last name is required")
  private String lastName;

  @Email(message = "Email should be valid")
  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private String mobile;

  @NotNull(message = "Country code is required")
  private String countryMobile;

  @NotEmpty(message = "Last name is required")
  private String username;

  @NotNull(message = "Username type is required")
  private String usernameType;

  @NotNull(message = "Username type is required")
  private String verificationMethod;

  private Boolean enabled;

  private String password;

  private boolean verified;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModifiedDate;


  @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
  @JoinTable(
          name="users_roles",
          joinColumns={@JoinColumn(name="USER_ID", referencedColumnName="ID")},
          inverseJoinColumns={@JoinColumn(name="ROLE_ID", referencedColumnName="ID")})
  private Set<Role> roles = new HashSet<>();

  public User(String username, String name, String email, String password) {
    this.firstName = name;
    this.usernameType = username;
    this.email = email;
    this.password = password;
  }
}
