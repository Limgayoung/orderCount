package nunu.orderCount.domain.product.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    private Long productId;
    private String name;
    private String option;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    private String zigzagProductId;
    private Long stock; //재고 수량
}
