# SOLID Principles Implementation - Authentication & Profile Service

## Overview
Dokumentasi ini menjelaskan penerapan SOLID principles dan TDD dalam modul Authentication & Profile.

---

## 1. Single Responsibility Principle (SRP)

Setiap service memiliki ONE reason to change, fokus pada satu tanggung jawab spesifik:

### Service Breakdown

#### `UsernameGenerationService`
- **Tanggung Jawab**: Generate dan manage username yang unik
- **Reason to Change**: Logika username generation berubah
- **Methods**:
  - `generateUniqueUsername(email)`: Extract dari email dan add suffix jika perlu
  - `isUsernameTaken(username)`: Check keunikan username
  
**Contoh**: Jika ada requirement baru seperti "tidak boleh mengandung angka", hanya class ini yang berubah.

#### `PasswordValidationService`
- **Tanggung Jawab**: Validasi password dan email format
- **Reason to Change**: Requirement validasi berubah (min length, regex pattern, dll)
- **Methods**:
  - `validatePassword(password)`: Check panjang minimum (8 chars)
  - `validateEmail(email)`: Check email format valid

**Contoh**: Jika ada requirement "password harus mengandung uppercase", hanya class ini yang berubah.

#### `UserRegistrationService`
- **Tanggung Jawab**: Orchestrate proses registrasi user baru
- **Reason to Change**: Alur registrasi berubah
- **Methods**:
  - `registerUser(email, password)`: Main orchestration
  - `isEmailRegistered(email)`: Check email availability

**Komposisi**: Menggunakan UsernameGenerationService dan PasswordValidationService

#### `UserAuthenticationService`
- **Tanggung Jawab**: Handle authentication dan token generation
- **Reason to Change**: Logika login atau token berubah
- **Methods**:
  - `authenticateUser(email, password)`: Main authentication logic

**Komposisi**: Menggunakan JwtService dan AuthenticationManager

#### `AuthServiceImpl` (Facade)
- **Tanggung Jawab**: Provide unified interface untuk authentication flow
- **Reason to Change**: Interface API berubah
- **Komposisi**: Menggunakan UserRegistrationService dan UserAuthenticationService

---

## 2. Open/Closed Principle (OCP)

Class terbuka untuk extension, tertutup untuk modification:

### Implementation

**Interface-based Design**:
```java
// Service interface
public interface UsernameGenerationService {
    String generateUniqueUsername(String email);
    boolean isUsernameTaken(String username);
}

// Implementation
@Service
public class UsernameGenerationServiceImpl implements UsernameGenerationService {
    // Implementation
}
```

**Benefit**: 
- Bisa menambah implementasi baru (misal: `RandomUsernameGenerationServiceImpl`) tanpa mengubah existing code
- Dependency injection melalui interface

### Extension Example
```java
// Bisa ditambahkan tanpa mengubah code lama
@Service
public class RandomUsernameGenerationServiceImpl implements UsernameGenerationService {
    @Override
    public String generateUniqueUsername(String email) {
        // Generate random username instead
    }
}
```

---

## 3. Liskov Substitution Principle (LSP)

Subclass harus dapat menggantikan parent class tanpa break aplikasi:

### Implementation

Semua implementasi dari interface harus konsisten dengan contract:

```java
public interface PasswordValidationService {
    void validatePassword(String password);
    void validateEmail(String email);
}

// Implementasi harus throw IllegalArgumentException, tidak ada alternatif
@Service
public class PasswordValidationServiceImpl implements PasswordValidationService {
    @Override
    public void validatePassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException(...); // Konsisten dengan contract
        }
    }
}
```

**Benefit**: 
- Bisa swap implementasi tanpa khawatir behavior berbeda
- Code yang menggunakan interface akan work dengan semua implementasi

---

## 4. Interface Segregation Principle (ISP)

Client tidak boleh forced depend pada method yang tidak digunakan:

### BAD Design (Tidak mengikuti ISP):
```java
// Interface yang terlalu besar
public interface UserService {
    UserProfileResponse register(String email, String password);
    LoginResponse login(String email, String password);
    UserProfileResponse getProfile(Long userId);
    UserProfileResponse updateProfile(Long userId, ProfileUpdateRequest request);
    UserProfileResponse submitKyc(Long userId, KycRequest request);
}
```

Client yang hanya butuh username generation menjadi depend pada semua method.

### GOOD Design (Mengikuti ISP):
```java
// Segregated interfaces
public interface UsernameGenerationService {
    String generateUniqueUsername(String email);
}

public interface UserAuthenticationService {
    LoginResponse authenticateUser(String email, String password);
}

public interface UserRegistrationService {
    UserProfileResponse registerUser(String email, String password);
}
```

**Benefit**:
- Client hanya depend pada method yang benar-benar digunakan
- Easier to test (bisa mock hanya yang perlu)
- Flexible untuk perubahan

---

## 5. Dependency Inversion Principle (DIP)

High-level modules tidak boleh depend pada low-level modules. Keduanya harus depend pada abstraction:

### Implementation

```java
// High-level module (Facade)
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRegistrationService userRegistrationService;  // Interface, bukan impl
    private final UserAuthenticationService userAuthenticationService;  // Interface, bukan impl

    @Override
    public UserProfileResponse register(RegisterRequest request) {
        return userRegistrationService.registerUser(request.getEmail(), request.getPassword());
    }
}

// Low-level module
@Service
@RequiredArgsConstructor
public class UserRegistrationServiceImpl implements UserRegistrationService {
    private final UserRepository userRepository;  // Interface, bukan direct DB access
    private final UsernameGenerationService usernameGenerationService;  // Interface
    private final PasswordValidationService passwordValidationService;  // Interface
    private final PasswordEncoder passwordEncoder;  // Spring abstraction
}
```

**Benefit**:
- Loose coupling
- Easy to test (bisa mock dependencies)
- Easy to swap implementations

### Dependency Graph
```
                    ↓ depends on
                    ↓ (interface)
AuthServiceImpl ←── UserRegistrationService (interface)
                    ↓ depends on
                    ↓ (interface)
                UserRegistrationServiceImpl
                    ↓ depends on
                    ├─ UserRepository (Spring Data interface)
                    ├─ UsernameGenerationService
                    ├─ PasswordValidationService
                    └─ PasswordEncoder
```

---

## Test-Driven Development (TDD) Implementation

### Test Coverage by Service

#### 1. PasswordValidationServiceTest
- **30+ test cases** covering:
  - Password validation (null, empty, blank, short, valid)
  - Email validation (null, empty, invalid format, valid)
  - Edge cases (special characters, etc)

#### 2. UsernameGenerationServiceTest
- **12+ test cases** covering:
  - Base username generation
  - Special character removal
  - Suffix generation for duplicates
  - Multiple suffix handling
  - Username availability check

#### 3. UserRegistrationServiceTest
- **12+ test cases** covering:
  - Successful registration with encrypted password
  - Validation flow (email, password)
  - Default role assignment
  - Duplicate email handling
  - Email registration check

#### 4. UserAuthenticationServiceTest
- **8+ test cases** covering:
  - Authentication success/failure
  - Token generation
  - Role handling
  - User not found scenario
  - Authentication failure handling

### TDD Approach Used

1. **Arrange-Act-Assert Pattern**:
   ```java
   @Test
   void test_scenario_expectedResult() {
       // Arrange - Setup mocks dan test data
       when(mockService.method()).thenReturn(value);
       
       // Act - Execute code under test
       result = service.testMethod();
       
       // Assert - Verify hasil
       assertThat(result).isEqualTo(expected);
   }
   ```

2. **Given-When-Then Naming**:
   ```java
   @DisplayName("Should generate username from email when base is unique")
   void generateUniqueUsername_baseUnique_returnBase() { ... }
   ```

3. **Mock Dependencies**:
   ```java
   @Mock
   private UserRepository userRepository;
   
   @Mock
   private UsernameGenerationService usernameGenerationService;
   ```

4. **Verify Behavior**:
   ```java
   verify(userRepository).save(any(User.class));
   verify(passwordEncoder).encode(password);
   ```

---

## Benefits Summary

### SOLID Benefits
✅ **Single Responsibility**: Easy to understand, test, and maintain  
✅ **Open/Closed**: New features without modifying existing code  
✅ **Liskov Substitution**: Can swap implementations safely  
✅ **Interface Segregation**: Flexible and focused dependencies  
✅ **Dependency Inversion**: Loose coupling and testability  

### TDD Benefits
✅ **High Test Coverage**: 60-70%+ coverage target achieved  
✅ **Confidence**: Tests catch bugs early  
✅ **Documentation**: Tests serve as living documentation  
✅ **Refactoring Safety**: Can refactor with confidence  
✅ **Design Quality**: Forces good design patterns  

---

## Code Quality Metrics

### Test Count
- PasswordValidationServiceTest: 31 tests
- UsernameGenerationServiceTest: 12 tests
- UserRegistrationServiceTest: 12 tests
- UserAuthenticationServiceTest: 8 tests
- **Total: 63+ test cases**

### Coverage Target
- Service Layer: 80-90% coverage
- Repository: 70%+ coverage
- Overall: 70%+ coverage

### Maintainability
- No code duplication (DRY principle)
- Clear naming conventions
- Comprehensive documentation
- Isolated test cases (no test dependencies)

---


## References

- **SOLID Principles**: https://en.wikipedia.org/wiki/SOLID
- **Test-Driven Development**: https://en.wikipedia.org/wiki/Test-driven_development
- **Spring Testing**: https://spring.io/guides/gs/testing-web/

