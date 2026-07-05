package com.restaurant.config;

import com.restaurant.entity.DishEntity;
import com.restaurant.entity.TableEntity;
import com.restaurant.repository.DishRepository;
import com.restaurant.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final DishRepository dishRepository;
    private final TableRepository tableRepository;

    @Bean
    public CommandLineRunner seedSampleData() {
        return args -> {
            if (dishRepository.count() == 0) {
                Map<String, String> dishes = Map.of(
                        "Spaghetti Bolognese", "32.00",
                        "Pizza Margherita", "28.50",
                        "Pizza Pepperoni", "34.00",
                        "Schabowy z ziemniakami", "36.00",
                        "Pierogi ruskie", "24.50",
                        "Zupa pomidorowa", "14.00",
                        "Sernik", "16.00",
                        "Lemoniada", "12.00");
                dishes.forEach((name, price) -> {
                    DishEntity dish = new DishEntity();
                    dish.setName(name);
                    dish.setPrice(new BigDecimal(price));
                    dishRepository.save(dish);
                });
                log.info("Seeded {} sample dishes", dishes.size());
            }
            if (tableRepository.count() == 0) {
                List.of(new int[]{1, 2}, new int[]{2, 4}, new int[]{3, 4}, new int[]{4, 6})
                        .forEach(t -> {
                            TableEntity table = new TableEntity();
                            table.setTableNumber(t[0]);
                            table.setSeats(t[1]);
                            tableRepository.save(table);
                        });
                log.info("Seeded 4 sample tables");
            }
        };
    }
}
