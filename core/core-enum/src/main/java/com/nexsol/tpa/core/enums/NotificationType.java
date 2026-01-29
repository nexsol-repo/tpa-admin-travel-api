package com.nexsol.tpa.core.enums;

/**
 * 알림(SMS/Email) 발송 유형
 */
public enum NotificationType {

    REJOIN("재가입 안내", "재가입창", "재가입 링크", "해외여행보험 재가입 안내입니다. 아래 링크에서 재가입을 진행해주세요."),
    CERTIFICATE("가입확인서 안내", "가입확인서(PDF)창", "가입확인서 보기", "해외여행보험 가입확인서 안내입니다. 아래 링크에서 가입확인서를 확인하실 수 있습니다.");

    private final String titleSuffix;

    private final String targetName;

    private final String linkText;

    private final String smsMessage;

    NotificationType(String titleSuffix, String targetName, String linkText, String smsMessage) {
        this.titleSuffix = titleSuffix;
        this.targetName = targetName;
        this.linkText = linkText;
        this.smsMessage = smsMessage;
    }

    /**
     * 이메일 제목 반환
     */
    public String getEmailTitle() {
        return "[TPA KOREA] " + titleSuffix;
    }

    /**
     * 이메일 본문에서 사용할 대상 이름 반환
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * 이메일 본문에서 사용할 링크 텍스트 반환
     */
    public String getLinkText() {
        return linkText;
    }

    /**
     * SMS 메시지 기본 내용 반환
     */
    public String getSmsMessage() {
        return smsMessage;
    }

    /**
     * SMS 전체 메시지 생성 (이름, 링크 포함)
     */
    public String formatSmsMessage(String name, String link) {
        return String.format("[TPA KOREA] %s 고객님, %s\n%s", name, smsMessage, link);
    }

}
