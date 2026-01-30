package com.habitame.api.property.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity(name = "properties")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyEntity implements Serializable {

}
