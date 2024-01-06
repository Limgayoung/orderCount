package nunu.orderCount.domain.product.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nunu.orderCount.domain.member.model.Member;
import nunu.orderCount.global.common.BaseEntity;
import nunu.orderCount.infra.zigzag.model.dto.response.ResponseZigzagOrderDto;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Table(name = "product")
@EqualsAndHashCode
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

    //todo: setImageUrl 과 함께 삭제 예정
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

