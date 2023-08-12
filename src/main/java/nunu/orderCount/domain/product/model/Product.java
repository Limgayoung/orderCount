package nunu.orderCount.domain.product.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.global.common.BaseEntity;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long productId;
    private String name;
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    @Column(unique = true)
    private String zigzagProductId;
    @ManyToOne
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
}

