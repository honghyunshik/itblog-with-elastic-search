package org.example.domain.postDetail;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.domain.post.Post;

@Entity
@Getter
@Setter
public class PostDetail {

    @Id
    private Long id;

    @Column(name = "postDetail_content")
    private String content;

    @OneToOne
    @MapsId
    @JoinColumn(name = "post_id")
    private Post post;

    public PostDetail(String content){
        this.content = content;
    }
}
