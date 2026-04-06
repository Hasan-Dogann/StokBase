package stokapp.entity;

// Bu entity ana satış fişini indirim bilgisiyle birlikte temsil eder.
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
@Data
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double originalTotalPrice;

    @Column(nullable = false)
    private Double discountAmount;

    @Column(nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Column(nullable = false)
    private LocalDateTime saleTime;

    @Column(nullable = true)
    private String customerName;

    @Column(nullable = true)
    private String customerSurname;

    @Column(nullable = true)
    private String customerTc;

    @Column(nullable = false)
    private String sellerUsername;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SaleItem> items = new ArrayList<>();
}