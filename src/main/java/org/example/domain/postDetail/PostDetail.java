package org.example.domain.postDetail;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.domain.post.Post;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PostDetail {

    @Id
    private Long id;

    @Column(name = "postDetail_content")
    @Size(max = 2000)
    private String content;

    @OneToOne
    @MapsId
    @JoinColumn(name = "post_id")
    private Post post;

    public PostDetail(String content){
        this.content = content;
    }
}
