package com.github.aivle6th.ai23.springboot_backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "BOARD")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "board_title")
    private String boardTitle;

    @Column(name = "description")
    private String description;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToMany
    @JoinTable(
        name = "BOARD_DEPARTMENT",
        joinColumns = @JoinColumn(name = "board_id"),
        inverseJoinColumns = @JoinColumn(name = "dept_id")
    )
    private Set<Department> departments = new HashSet<>(); 

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @Builder
    public Board(Long boardId, String boardTitle, String description, Boolean isPublic, LocalDateTime endDate) {
        this.boardId = boardId;
        this.boardTitle = boardTitle;
        this.description = description;
        this.isPublic = isPublic;
        this.endDate = endDate;
        this.createdAt = LocalDateTime.now();
    }

    public void updateBoard(String boardTitle, String description, Boolean isPublic, LocalDateTime endDate) {
        this.boardTitle = boardTitle;
        this.description = description;
        this.isPublic = isPublic;
        this.endDate = endDate;
    }

    public void setDepartments(Set<Department> departments){
        departments.stream()
               .forEach(this.departments::add);
    }
}


