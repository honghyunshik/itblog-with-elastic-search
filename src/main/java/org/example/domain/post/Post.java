package org.example.domain.post;

import jakarta.persistence.*;
import lombok.*;
import org.example.domain.member.Member;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private long id;

    @Column(name = "post_title")
    private String title;

    @Column(name = "post_save_date")
    private LocalDateTime saveDate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public Post(String title){
        this.title = title;
        this.saveDate = LocalDateTime.now();
    }
}
