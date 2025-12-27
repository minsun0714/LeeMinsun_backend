
# =======================
# 1단계: 빌드
# =======================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Gradle wrapper 및 설정 먼저 복사 (캐시 활용)
COPY --chmod=0755 gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Gradle dependency 캐싱 (의존성만 미리 다운로드)
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=cache,target=/workspace/.gradle \
    ./gradlew --no-daemon dependencies || true

# 소스 복사 후 빌드 (clean 제거, 병렬 빌드 적용, test 제외)
COPY src src
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=cache,target=/workspace/.gradle \
    ./gradlew --no-daemon --parallel bootJar -x test

# =======================
# 2단계: 실행
# =======================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 컨테이너 타임존 KST
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 비루트 사용자 추가
RUN addgroup -S app && adduser -S app -G app && chown -R app:app /app
USER app:app

# 빌드 산출물(app.jar) 복사
COPY --from=builder --chown=app:app /workspace/build/libs/app.jar /app/app.jar

EXPOSE 8080

# 기본 프로필/자바 옵션
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Duser.timezone=Asia/Seoul -Dfile.encoding=UTF-8"

# 실행
ENTRYPOINT ["java","-jar","/app/app.jar"]
CMD ["--spring.profiles.active=docker"]