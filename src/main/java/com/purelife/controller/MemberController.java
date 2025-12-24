package com.purelife.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.purelife.controller.dto.request.ChangePasswordRequest;
import com.purelife.controller.dto.request.UpdateMemberRequest;
import com.purelife.controller.dto.response.ApiResponse;
import com.purelife.controller.dto.response.MemberResponse;
import com.purelife.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 從 SecurityContext 取得當前登入的 memberId
     */
    private Integer getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("請先登入");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Integer) {
            return (Integer) principal;
        } else if (principal instanceof Long) {
            return ((Long) principal).intValue();
        } else if (principal instanceof String) {
            try {
                return Integer.parseInt((String) principal);
            } catch (NumberFormatException e) {
                throw new RuntimeException("無法解析會員ID");
            }
        } else {
            throw new RuntimeException("無法取得會員資訊");
        }
    }

    /**
     * 取得當前會員資料
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyInfo() {
        Integer memberId = getCurrentMemberId();
        MemberResponse member = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(ApiResponse.success(member));
    }

    /**
     * 更新會員資料
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMyInfo(
            @Valid @RequestBody UpdateMemberRequest request) {
        
        Integer memberId = getCurrentMemberId();
        MemberResponse member = memberService.updateMember(memberId, request);
        return ResponseEntity.ok(ApiResponse.success("資料更新成功", member));
    }

    /**
     * 修改密碼
     */
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        
        Integer memberId = getCurrentMemberId();
        memberService.changePassword(memberId, request);
        return ResponseEntity.ok(ApiResponse.success("密碼修改成功", null));
    }
}
