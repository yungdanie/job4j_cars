package ru.job4j.cars.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "price_history")
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@DynamicUpdate(value = true)
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "price")
    private int price;

    @Column(name = "created")
    @CreationTimestamp
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "is_last")
    private boolean isLast;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PriceHistory that = (PriceHistory) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
