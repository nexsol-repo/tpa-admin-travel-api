```json
{
  "data": {
    "contractId": 199,
    "insuranceSection": {
      "product": {
        "name": "15540",
        "plan": "가뿐한플랜B_15~69세",
        "displayPlanName": "가뿐한플랜",
        "silsonExclude": false,
        "country": "",
        "countryCode": "JA00"
      },
      "subscription": {
        "partner": "TPA KOREA",
        "channel": "TPA KOREA",
        "insurer": "MERITZ"
      },
      "term": {
        "applicationDate": "2026-03-17T17:30:48",
        "startDate": "2026-03-25",
        "endDate": "2026-03-26"
      },
      "status": {
        "statusName": "임의해지",
        "statusCode": "CANCELED",
        "insuredCount": 0,
        "totalPremium": 3240.00
      },
      "policyNumber": "15540-207849",
      "policyLink": "https://msea.meritzfire.com/mblTrvInsDcmCnf.do?polNo=15540-207849&pdCd=15540&quotGrpNo=TPA-20260317-173133946&quotReqNo=202603170023&otptDiv=A&aflcoDivCd=TPA&gnrAflcoCd=020&bizpeNo=2368801872&coprCtrYn=1"
    },
    "applicantSection": {
      "name": "이정진",
      "residentNumber": "941118-1894313",
      "phoneNumber": "01028664851",
      "email": "devback90.nexsol@gmail.com"
    },
    "payment": {
      "status": "CANCELED",
      "method": "BANK",
      "totalAmount": 3240.00,
      "paidAt": "2026-03-17T17:31:36",
      "canceledAt": "2026-03-21T17:33:50"
    },
    "refund": {
      "refundAmount": 3240.00,
      "refundMethod": "BANK",
      "bankName": "BNK",
      "accountNumber": "266667777",
      "depositorName": "강",
      "refundReason": null,
      "refundedAt": "2026-03-17T17:31:50"
    },
    "companions": []
  },
  "error": null,
  "result": "SUCCESS"
}
```

## PUT /v1/admin/travel/contract/199 수정 요청 Body

```json
{
  "statusName": "가입완료",
  "applicant": {
    "name": "이정진",
    "residentNumber": "941118-1894313",
    "phoneNumber": "01028664851",
    "email": "devback90.nexsol@gmail.com"
  },
  "period": {
    "startDate": "2026-03-25",
    "endDate": "2026-03-26"
  },
  "subscriptionOrigin": {
    "insurerId": 1,
    "insurerName": "MERITZ",
    "channelId": 1,
    "channelName": "TPA KOREA",
    "partnerId": 1,
    "partnerName": "TPA KOREA"
  },
  "planName": "가뿐한플랜",
  "silsonExclude": false,
  "travelCountry": "",
  "countryCode": "JA00",
  "policyNumber": "15540-207849",
  "policyLink": "https://msea.meritzfire.com/mblTrvInsDcmCnf.do?polNo=15540-207849&pdCd=15540&quotGrpNo=TPA-20260317-173133946&quotReqNo=202603170023&otptDiv=A&aflcoDivCd=TPA&gnrAflcoCd=020&bizpeNo=2368801872&coprCtrYn=1",
  "applicationDate": "2026-03-17T17:30:48",
  "payment": {
    "status": "COMPLETED",
    "method": "BANK",
    "totalAmount": 3240.00,
    "paidAt": "2026-03-17T17:31:36",
    "canceledAt": null
  },
  "refund": {
    "refundAmount": 3240.00,
    "refundMethod": "BANK",
    "bankName": "BNK",
    "accountNumber": "266667777",
    "depositorName": "강",
    "refundReason": null,
    "refundedAt": "2026-03-17T17:31:50"
  },
  "memo": "임의해지 → 가입완료로 상태 변경"
}
```