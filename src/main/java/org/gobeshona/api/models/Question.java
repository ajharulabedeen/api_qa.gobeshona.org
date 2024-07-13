package org.gobeshona.api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 512)
    private String title;

    @NotBlank
    @Column(length = 5000 , columnDefinition = "TEXT")
    private String description;

    @Size(max = 512)
    private String audioFilePath;

    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private List<Images> images = new ArrayList<>();

}
