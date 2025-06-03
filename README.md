# 📚 Online Course Backend API

Bu loyiha `Spring Boot 3`, `Spring Security`, `JWT`, va `Swagger` yordamida ishlab chiqilgan **onlayn kurs platformasining backend tizimi** hisoblanadi.

## 🚀 Texnologiyalar

- Java 17+
- Spring Boot 3
- Spring Security + JWT
- PostgreSQL
- Swagger UI (API Documentation)
- Maven
- Hibernate (JPA)
- Lombok
- RESTful API

## 📂 Loyihaning strukturasi

```bash
src/
├── main/
│   ├── java/
│   │   └── uz.zafar.onlineshop/
│   │       ├── controller/           # API endpoint'lar
│   │       ├── dto/                  # Request/Response DTO'lar
│   │       ├── entity/               # JPA Entity klasslar
│   │       ├── repository/           # JpaRepository interfeyslar
│   │       ├── security/             # JWT, Auth konfiguratsiyasi
│   │       ├── service/              # Business logika
│   │       └── OnlineCourseApp.java  # Main application
│   └── resources/
│       ├── application.yml           # Konfiguratsiya fayli
│       └── static/                   # Statik fayllar (agar kerak bo‘lsa)
