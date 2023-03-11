package ro.adi.comparatorprices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ro.adi.comparatorprices")
public class ComparatorPricesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ComparatorPricesApplication.class, args);
    }
}
