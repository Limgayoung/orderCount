package nunu.orderCount.domain.option.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.product.model.Product;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long optionId;

    private String name;
    private Integer inventoryQuantity;

    @ManyToOne
    private Product product;

    @Builder
    public Option(String name, Integer inventoryQuantity, Product product) {
        this.name = name;
        this.inventoryQuantity = inventoryQuantity;
        this.product = product;
    }

    @Builder
    public Option(String name, Product product) {
        this.name = name;
        this.inventoryQuantity = 0;
        this.product = product;
    }
}
