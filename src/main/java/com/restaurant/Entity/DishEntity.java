package com.restaurant.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Dishes")
@Getter
@Setter
public class DishEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Dish_Id")
    private long dishId;

    @Column(name = "Name_Of_The_Dish")
    private String nameOfTheDish;

    @Column(name = "Price_Of_The_Dish")
    private String priceOfTheDish;
}
