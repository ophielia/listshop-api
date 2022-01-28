package com.meg.listshop.lmt.data.entity;

import com.meg.listshop.lmt.api.model.TokenType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tokens")
@GenericGenerator(
        name = "token_sequence",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {@org.hibernate.annotations.Parameter(
                name = "sequence_name",
                value = "token_sequence"),
                @org.hibernate.annotations.Parameter(
                        name = "initial_value",
                        value = "57000"),
                @org.hibernate.annotations.Parameter(
                        name = "increment_size",
                        value = "1")}
)
public class TokenEntity {

    @Column(name = "user_id")
    private Long userId;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_sequence")
    @Column(name = "token_id")
    private Long token_id;

    @Column(name = "token_type")
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "token_value")
    private String tokenValue;


}