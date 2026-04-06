package stokapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ilacIsmi;

    @Column(nullable = false)
    private Double alisFiyati;

    @Column(nullable = false)
    private Double kdv;

    @Column(nullable = false)
    private Double satisFiyati;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean active = true;
}