package com.meg.listshop.lmt.data.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by margaretmartin on 24/10/2017.
 */
@Entity
@Table(name = "dish_items")
@GenericGenerator(
        name = "dish_item_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "dish_item_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
public class DishItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dish_item_sequence")
    @Column(name = "dish_item_id")
    private Long dishItemId;

    @OneToOne
    @JoinColumn(name = "tag_id")
    private TagEntity tag;

    @OneToOne
    @JoinColumn(name = "dish_id")
    private DishEntity dish;

    public DishItemEntity(Long id) {
        dishItemId = id;
    }

    public DishItemEntity() {
        // necessary for jpa construction
    }


    public Long getDishItemId() {
        return dishItemId;
    }

    public void setDishItemId(Long dishItemId) {
        this.dishItemId = dishItemId;
    }

    public TagEntity getTag() {
        return tag;
    }

    public void setTag(TagEntity tag) {
        this.tag = tag;
    }

    public DishEntity getDish() {
        return dish;
    }

    public void setDish(DishEntity dish) {
        this.dish = dish;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DishItemEntity that = (DishItemEntity) o;
        return Objects.equals(dishItemId, that.dishItemId) && Objects.equals(tag, that.tag) && Objects.equals(dish, that.dish);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dishItemId, tag, dish);
    }

    @Override
    public String toString() {
        return "DishItemEntity{" +
                "dishItemId=" + dishItemId +
                ", tag=" + tag +
                ", dish=" + dish +
                '}';
    }
}
