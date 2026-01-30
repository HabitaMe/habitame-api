package com.habitame.api.city.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Table(name = "cities")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CityEntity implements Serializable {
}
