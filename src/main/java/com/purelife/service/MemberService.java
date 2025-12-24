package com.purelife.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.purelife.controller.dto.request.ChangePasswordRequest;
import com.purelife.controller.dto.request.UpdateMemberRequest;
import com.purelife.controller.dto.response.MemberResponse;
import com.purelife.entity.Member;
import com.purelife.repository.MemberRepository;
import com.purelife.util.PasswordUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    // 不需要 PasswordEncoder 了

    /**
     * 取得會員資料
     */
    public MemberResponse getMemberInfo(Integer memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));

        return convertToResponse(member);
    }

    /**
     * 更新會員資料
     */
    @Transactional
    public MemberResponse updateMember(Integer memberId, UpdateMemberRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));

        member.setName(request.getName());
        
        if (request.getPhone() != null) {
            member.setPhone(request.getPhone());
        }

        Member saved = memberRepository.save(member);
        return convertToResponse(saved);
    }

    /**
     * 修改密碼
     */
    @Transactional
    public void changePassword(Integer memberId, ChangePasswordRequest request) {
        // 1. 檢查新密碼確認
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("新密碼與確認密碼不一致");
        }

        // 2. 取得會員
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));

        // 3. 驗證目前密碼（用你的 PasswordUtil）
        String[] parts = member.getPasswordHash().split(":");
        String salt = parts[0];
        String storedHash = parts[1];
        
        if (!PasswordUtil.verifyPassword(request.getCurrentPassword(), salt, storedHash)) {
            throw new RuntimeException("目前密碼錯誤");
        }

        // 4. 產生新的 salt 並加密新密碼
        String newSalt = PasswordUtil.generateSalt();
        String newHashedPassword = PasswordUtil.hashPassword(request.getNewPassword(), newSalt);
        
        // 5. 更新密碼（格式：salt:hash）
        member.setPasswordHash(newSalt + ":" + newHashedPassword);
        memberRepository.save(member);
    }

    /**
     * 轉換為 Response DTO
     */
    private MemberResponse convertToResponse(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .account(member.getAccount())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .memberLevel(member.getMemberLevel())
                .registrationTime(member.getRegistrationTime())
                .build();
    }
}