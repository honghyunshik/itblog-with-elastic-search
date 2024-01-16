package org.example.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.common.exception.member.JwtAccessTokenValidButNoUser;
import org.example.domain.member.Member;
import org.example.domain.post.Post;
import org.example.domain.post.PostRepostiory;
import org.example.domain.postDetail.PostDetail;
import org.example.domain.postDetail.PostDetailRepository;
import org.example.dto.post.PostpostRequestDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl {

    private final PostRepostiory postRepostiory;
    private final PostDetailRepository postDetailRepository;
    private final MemberServiceImpl memberService;

    @Transactional
    public void post(PostpostRequestDto postpostRequestDto, HttpServletRequest httpServletRequest){

        Post post = postpostRequestDto.toPostEntity();
        //유저 찾기
        Member member = memberService.getLoginUser(httpServletRequest)
                .orElseThrow(JwtAccessTokenValidButNoUser::new);
        post.setMember(member);
        postRepostiory.save(post);

        /*
        PostDetail postDetail = postpostRequestDto.toPostDetailEntity();
        postDetail.setPost(post);
        postDetailRepository.save(postDetail);

         */
    }
}
