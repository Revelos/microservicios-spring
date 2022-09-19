package com.msvccursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients //Esto es como declarar el crudRepository
@SpringBootApplication
public class MsvcCursosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsvcCursosApplication.class, args);
    }

}
