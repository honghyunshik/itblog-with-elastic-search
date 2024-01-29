package org.example.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.post.Post;
import org.example.domain.postDetail.PostDetail;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SavepostRequestDto {

    @NotBlank(message = "제목은 비어 있을 수 없습니다.")
    @NotNull
    @Size(min = 3, max = 60, message = "제목은 3자 이상, 60자 이하이어야 합니다.")
    private String title;

    @NotBlank(message = "내용은 비어 있을 수 없습니다.")
    @NotNull
    @Size(min = 10, message = "내용은 최소 10자 이상이어야 합니다.")
    private String content;

    public Post toPostEntity(){
        return new Post(title);
    }

    public PostDetail toPostDetailEntity(){
        return new PostDetail(content);
    }
}
