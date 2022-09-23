##referencia al jdk
#FROM openjdk:18.0.1.1-oracle
#
##creacion de carpeta -LA IMAGENTODO LOQUE DIGA COPY VA DENTRO DE WORKDIR
#WORKDIR /app/msvc-usuario
#
##copia el jar a la carpeta raiz
##descarga todas las dep nuevo y las guarda en la carpeta /app
#COPY ./pom.xml /app
#COPY ./msvc-usuario/.mvn ./.mvn
#COPY ./mscv-usuario/mvnw .
#COPY ./mscv-usuario/pom.xml .
#
##COPY ./msvc-usuario .
##COPY ./target/msvc-usuario-0.0.1-SNAPSHOT.jar .
#
#RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
#
##RUN ./mvnw clean package -DskipTests
#COPY ./msvc-usuario/src ./src
#
#RUN ./mvnw clean package -DskipTests
##puerto de despliegue
#EXPOSE 8001
#
##referencia del jar
#ENTRYPOINT ["java","-jar", "./target/msvc-usuario-0.0.1-SNAPSHOT.jar"]

FROM openjdk:18-jdk-alpine as builder

WORKDIR /app/msvc-usuario

COPY ./pom.xml /app
COPY ./msvc-usuario/.mvn ./.mvn
COPY ./msvc-usuario/mvnw .
COPY ./msvc-usuario/pom.xml .

RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
#RUN ./mnvw dependency:gi-offline
COPY ./msvc-usuario/src ./src

RUN ./mvnw clean package -DskipTests

FROM openjdk:18-jdk-alpine

WORKDIR /app
COPY --from=builder /app/msvc-usuario/target/msvc-usuario-0.0.1-SNAPSHOT.jar .

EXPOSE 8001

ENTRYPOINT ["java","-jar", "msvc-usuario-0.0.1-SNAPSHOT.jar"]