package nunu.orderCount.domain.option.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.product.model.Product;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Table(name = "options")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        if (!Objects.equals(optionId, option.optionId)) return false;
        if (!Objects.equals(name, option.name)) return false;
        if (!Objects.equals(inventoryQuantity, option.inventoryQuantity))
            return false;
        return Objects.equals(product, option.product);
    }

    @Override
    public int hashCode() {
        int result = optionId != null ? optionId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (inventoryQuantity != null ? inventoryQuantity.hashCode() : 0);
        result = 31 * result + (product != null ? product.hashCode() : 0);
        return result;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
