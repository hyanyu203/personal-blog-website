package com.jiangou.friendlink.controller;

import com.jiangou.common.result.ApiResult;
import com.jiangou.friendlink.dto.FriendLinkApplyDTO;
import com.jiangou.friendlink.service.FriendLinkService;
import com.jiangou.friendlink.vo.FriendLinkVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Friend Links")
@RestController
@RequestMapping("/api/v1/friend-links")
public class FriendLinkPublicController {

    private final FriendLinkService friendLinkService;

    public FriendLinkPublicController(FriendLinkService friendLinkService) {
        this.friendLinkService = friendLinkService;
    }

    @GetMapping
    public ApiResult<List<FriendLinkVO>> list() {
        return ApiResult.ok(friendLinkService.listApproved());
    }

    @PostMapping("/apply")
    public ApiResult<FriendLinkVO> apply(@Valid @RequestBody FriendLinkApplyDTO dto) {
        return ApiResult.ok(friendLinkService.apply(dto));
    }
}
