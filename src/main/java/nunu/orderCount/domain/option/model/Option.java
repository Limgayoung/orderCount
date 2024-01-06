package nunu.orderCount.domain.option.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.product.model.Product;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Table(name = "options")
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long optionId;

    @NotNull
    private String name;
    @NotNull
    private Integer inventoryQuantity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Product product;

    @Builder
    public Option(String name, Product product) {
        this.name = name;
        this.inventoryQuantity = 0;
        this.product = product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
