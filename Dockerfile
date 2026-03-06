# Stage 1: Build (Menggunakan Gradle untuk kompilasi)
FROM gradle:9.3.0-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Melakukan build tanpa menjalankan tes (karena tes sudah dijalankan di CI)
RUN gradle build -x test --no-daemon

# Stage 2: Runtime (Hanya menyalin hasil build agar image kecil)
FROM eclipse-temurin:21-jre-jammy
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar

# Menjalankan aplikasi
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "/app.jar"]