package com.restauration.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Table")
@Getter
@Setter
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private long tableId;

    @ManyToMany(mappedBy = "tables")
    private List<UserEntity> users;
}
