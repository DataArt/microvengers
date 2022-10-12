package com.dataart.shipping.domain;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Country {

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    private Boolean active;

}
