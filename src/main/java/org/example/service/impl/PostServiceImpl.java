package org.example.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.common.exception.member.JwtAccessTokenValidButNoUser;
import org.example.common.exception.post.PostNotExistException;
import org.example.domain.member.Member;
import org.example.domain.post.Post;
import org.example.domain.post.PostRepostiory;
import org.example.domain.postDetail.PostDetail;
import org.example.domain.postDetail.PostDetailRepository;
import org.example.dto.post.SavepostRequestDto;
import org.example.dto.post.SelectPostResponseDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl {

    private final PostRepostiory postRepostiory;
    private final PostDetailRepository postDetailRepository;
    private final MemberServiceImpl memberService;

    @Transactional
    public long savePost(SavepostRequestDto savepostRequestDto, HttpServletRequest httpServletRequest){

        Post post = savepostRequestDto.toPostEntity();
        //유저 찾기
        Member member = memberService.getLoginUser(httpServletRequest)
                .orElseThrow(JwtAccessTokenValidButNoUser::new);
        post.setMember(member);
        post = postRepostiory.save(post);

        PostDetail postDetail = savepostRequestDto.toPostDetailEntity();
        postDetail.setPost(post);
        postDetailRepository.save(postDetail);

        return post.getId();
    }

    @Transactional
    public SelectPostResponseDto selectPost(Long id){

        Optional<Post> postWithOptional = Optional.ofNullable(postRepostiory.findById(id)
                .orElseThrow(PostNotExistException::new));
        Post post = postWithOptional.get();

        return SelectPostResponseDto.builder()
                .title(post.getTitle())
                .saveDate(post.getSaveDate())
                .content(postDetailRepository.findById(post.getId()).get().getContent())
                .author(post.getMember().getName())
                .email(post.getMember().getEmail())
                .build();
    }
}
