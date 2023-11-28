package nunu.orderCount.domain.product.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.global.common.BaseEntity;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long productId;
    @NotNull
    private String name;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    @NotNull
    @Column(unique = true)
    private String zigzagProductId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @NotNull
    private Member member;

    @Builder
    public Product(String name, String imageUrl, String zigzagProductId, Member member) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.zigzagProductId = zigzagProductId;
        this.member = member;
    }

    @Builder
    public Product(String name, String zigzagProductId, Member member) {
        this.name = name;
        this.zigzagProductId = zigzagProductId;
        this.member = member;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (!Objects.equals(productId, product.productId)) return false;
        if (!name.equals(product.name)) return false;
        if (!Objects.equals(imageUrl, product.imageUrl)) return false;
        if (!zigzagProductId.equals(product.zigzagProductId)) return false;
        return member.equals(product.member);
    }

    @Override
    public int hashCode() {
        int result = productId != null ? productId.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + zigzagProductId.hashCode();
        result = 31 * result + member.hashCode();
        return result;
    }
}

