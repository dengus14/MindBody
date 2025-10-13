package com.app.mindbody.models;


import com.app.mindbody.enums.RequirementTypeEnums;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "badges")
@NoArgsConstructor
@AllArgsConstructor
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String badge_name;
    @Column
    private String badge_description;
    @Column
    @Enumerated(EnumType.STRING)
    private RequirementTypeEnums requirement_type;

    @Column
    private int requirement_value;


}
