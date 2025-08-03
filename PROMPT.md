Implementa una API con seguridad JWT

Tecnologias:

- Framework: Spring boot
- Lenguaje de programacion: kotlin
- Base de datos: Postgresql
- package: com.bluelock.irongate
- libreria JWT: io.jsonwebtoken:jjwt-api:0.12.6

Especificaciones:

- No uses DaoAuthenticationProvider
- Implementa 'UserDetailsService' como 'UserDetailsServiceImpl'
- Implementa 'UserDetails' como 'UserDetailsImpl'
- UserDetailsImpl debe recibir un objeto 'User' y debe implementar los metodos de UserDetails
- Implementa refresh token

- Entidad 'User':
```kotlin
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val lastName: String,

    @Column(nullable = false)
    val isEnabled: Boolean = true,

    @Column(nullable = false)
    val isAccountNonExpired: Boolean = true,

    @Column(nullable = false)
    val isAccountNonLocked: Boolean = true,

    @Column(nullable = false)
    val isCredentialsNonExpired: Boolean = true,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role = Role.USER,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column
    val updatedAt: LocalDateTime? = null
)
```

- Enum 'Role':
```kotlin
enum class Role {
    USER, ADMIN, MODERATOR
}
```

- Entidad 'RefreshToken':
```kotlin
@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val token: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val expiryDate: LocalDateTime,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val isRevoked: Boolean = false
)
```

