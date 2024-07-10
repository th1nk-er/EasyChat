package top.th1nk.easychat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("top.th1nk.easychat.mapper")
public class EasyChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyChatApplication.class, args);
    }

}
