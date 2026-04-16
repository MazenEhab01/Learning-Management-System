run:
	./mvnw spring-boot:run

build:
	./mvnw clean package -DskipTests

clean:
	./mvnw clean