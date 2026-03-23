create table db_tpa_dev.tpa_channel
(
    id           bigint auto_increment comment '고유 ID'
        primary key,
    partner_id   bigint                                 null,
    channel_code varchar(30)                            null comment '채널 코드',
    channel_name varchar(100)                           not null comment '채널명',
    is_active    tinyint(1) default 1                   not null comment '사용여부',
    service_type longtext collate utf8mb4_bin           null comment '타입이 여러개 가능 (PUNGSU,TRAVEL,SOLAR)'
        check (json_valid(`service_type`)),
    created_at   datetime   default current_timestamp() not null comment '생성일시',
    updated_at   datetime   default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at   datetime                               null comment '삭제일시',
    constraint uq_channel_code
        unique (channel_code)
)
    comment '가입 채널 마스터';

create index idx_channel_name
    on db_tpa_dev.tpa_channel (channel_name);

create table db_tpa_dev.tpa_employee
(
    id              bigint auto_increment comment '직원 고유 ID'
        primary key,
    login_id        varchar(50)                  null comment '로그인 아이디',
    password        varchar(255)                 null comment '비밀번호 (암호화)',
    name            varchar(50)                  null comment '성명',
    service_type    longtext collate utf8mb4_bin null comment '상품 접근 권한 (PUNGSU, TRAVEL, SOLAR)'
        check (json_valid(`service_type`)),
    phone_number    varchar(100)                 null comment '연락처',
    role            varchar(20)                  null comment '권한 (MASTER, OPERATION)',
    employee_number varchar(50)                  null comment '사번',
    email           varchar(100)                 null comment '이메일 주소',
    status          varchar(20)                  null comment '재직 상태 (ACTIVE, LEAVE, RESIGNED)',
    created_at      datetime                     null comment '생성일시',
    updated_at      datetime                     null comment '수정일시',
    last_access_ip  varchar(100)                 null comment '마지막 접속 IP',
    position        varchar(50)                  null comment '직책',
    department      varchar(100)                 null comment '부서'
);

create table db_tpa_dev.tpa_insurer
(
    id           bigint auto_increment comment '고유 ID'
        primary key,
    insurer_code varchar(30)                            not null comment '보험사 코드',
    insurer_name varchar(100)                           not null comment '보험사명',
    api_base_url varchar(200)                           null comment 'API Base URL',
    service_type longtext collate utf8mb4_bin           null
        check (json_valid(`service_type`)),
    is_active    tinyint(1) default 1                   not null comment '사용여부',
    created_at   datetime   default current_timestamp() not null comment '생성일시',
    updated_at   datetime   default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at   datetime                               null comment '삭제일시',
    constraint uq_insurer_code
        unique (insurer_code)
)
    comment '보험사 마스터';

create table db_tpa_dev.tpa_partner
(
    id                           bigint auto_increment comment '고유 ID'
        primary key,
    partner_code                 varchar(30)                            null comment '제휴사 코드',
    partner_name                 varchar(100)                           not null comment '제휴사명',
    business_registration_number varchar(20)                            null comment '사업자등록번호',
    ceo_name                     varchar(50)                            null comment '대표자명',
    address                      varchar(200)                           null comment '주소',
    memo                         text                                   null comment '메모',
    service_type                 longtext collate utf8mb4_bin           null comment '["TRAVEL", "PUNGSU","SOLRA"]'
        check (json_valid(`service_type`)),
    is_active                    tinyint(1) default 1                   not null comment '사용여부',
    created_at                   datetime   default current_timestamp() not null comment '생성일시',
    updated_at                   datetime   default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at                   datetime                               null comment '삭제일시',
    constraint uq_partner_code
        unique (partner_code)
)
    comment '제휴사(고객사)';

create index idx_partner_name
    on db_tpa_dev.tpa_partner (partner_name);

create table db_tpa_dev.tpa_service_meta
(
    service_code varchar(30)                            not null comment '서비스 코드 (예: PUNGSU, TRAVEL)'
        primary key,
    service_name varchar(100)                           not null comment '서비스 표시명 (예: 풍수해 보험)',
    description  varchar(255)                           null comment '서비스 설명',
    is_active    tinyint(1) default 1                   not null comment '사용 여부 (1: 사용, 0: 미사용)',
    sort_order   int        default 0                   not null comment '정렬 순서',
    created_at   datetime   default current_timestamp() not null comment '생성일시',
    updated_at   datetime   default current_timestamp() not null on update current_timestamp() comment '수정일시'
)
    comment '서비스 타입 마스터 메타 정보';

create table db_tpa_dev.travel_coverage_summary
(
    id                       bigint auto_increment comment '고유 ID'
        primary key,
    plan_name                varchar(100)                           not null comment '플랜명',
    coverage_code            varchar(20)                            null comment '담보 코드',
    coverage_name            varchar(255)                           not null comment '담보명',
    coverage_amount_under_15 bigint     default 0                   null comment '보장금액 (15세 미만)',
    coverage_amount_15_to_69 bigint     default 0                   null comment '보장금액 (15세~69세)',
    order_number             int        default 0                   not null comment '정렬 순서',
    is_major_coverage        tinyint(1) default 0                   null comment '주요보장여부',
    created_at               datetime   default current_timestamp() null comment '생성일시'
)
    comment '해외여행자보험 보장 상세 요약';

create table db_tpa_dev.travel_favorite_city
(
    id                     bigint auto_increment comment '고유 ID'
        primary key,
    country_code           varchar(10)                          not null comment '국가 코드',
    country_name_korean    varchar(50)                          not null comment '국가명(한글)',
    country_name_english   varchar(50)                          not null comment '국가명(영문)',
    city_name_korean       varchar(50)                          not null comment '도시명(한글)',
    city_name_english      varchar(50)                          not null comment '도시명(영문)',
    travel_risk_grade_code varchar(10)                          not null comment '여행 위험 등급 코드',
    sort_order             int      default 0                   not null comment '정렬 순서',
    created_at             datetime default current_timestamp() not null comment '생성일시',
    updated_at             datetime                             null on update current_timestamp() comment '수정일시',
    deleted_at             datetime                             null comment '삭제일시(Soft Delete)'
)
    comment '자주가는 도시 관리';

create table db_tpa_dev.travel_insurance_coverage
(
    id            bigint auto_increment comment '고유 ID'
        primary key,
    insurer_id    bigint                               not null comment '보험사 ID',
    coverage_code varchar(50)                          null comment '담보코드',
    coverage_name varchar(100)                         not null comment '담보명',
    group_code    varchar(50)                          null comment '담보 그룹코드(UI 분류)',
    claim_reason  varchar(300)                         null comment '보험금 지급사유',
    claim_content text                                 null comment '보험금 지급액 설명',
    sub_title     varchar(200)                         null comment '추가 항목 제목',
    sub_content   text                                 null comment '추가 내용',
    created_at    datetime default current_timestamp() not null comment '생성일시',
    updated_at    datetime default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at    datetime                             null comment '삭제일시'
)
    comment '여행자보험 담보(보장) 마스터';

create index idx_coverage_group
    on db_tpa_dev.travel_insurance_coverage (insurer_id, group_code, id);

create index idx_coverage_insurer
    on db_tpa_dev.travel_insurance_coverage (insurer_id);

create index idx_coverage_name
    on db_tpa_dev.travel_insurance_coverage (coverage_name);

create table db_tpa_dev.travel_insurance_plan
(
    id                     bigint auto_increment comment '고유 ID'
        primary key,
    insurer_id             bigint                                 not null comment '보험사 ID',
    insurance_product_name varchar(50)                            null comment '보험상품명',
    plan_name              varchar(50)                            not null comment '플랜명',
    product_code           varchar(30)                            null comment '상품코드(pdCd)',
    unit_product_code      varchar(30)                            null comment '단위상품코드(untPdCd)',
    plan_group_code        varchar(30)                            null comment '플랜그룹코드(planGrpCd)',
    plan_code              varchar(30)                            null comment '플랜코드',
    age_group_id           bigint                                 null comment '나이구간 ID',
    plan_full_name         varchar(200)                           null comment '플랜 전체명',
    sort_order             int        default 0                   not null comment '플랜 정렬순서',
    effective_from         date                                   null comment '유효시작일',
    effective_to           date                                   null comment '유효종료일',
    is_active              tinyint(1) default 1                   not null comment '사용여부',
    created_at             datetime   default current_timestamp() not null comment '생성일시',
    updated_at             datetime   default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at             datetime                               null comment '삭제일시',
    constraint fk_plan_insurer
        foreign key (insurer_id) references db_tpa_dev.tpa_insurer (id)
)
    comment '여행자보험 플랜 마스터';

create table db_tpa_dev.travel_contract
(
    id                              bigint auto_increment comment '고유 ID'
        primary key,
    insurer_id                      bigint                                     null comment '보험사 ID',
    partner_id                      bigint                                     null comment '제휴사 ID',
    channel_id                      bigint                                     null comment '가입 채널 ID',
    plan_id                         bigint                                     null comment '플랜 ID',
    policy_number                   varchar(100)                               null comment '증권번호',
    partner_name                    varchar(20)                                null comment '가입제휴사명',
    channel_name                    varchar(20)                                null comment '가입채널명',
    insurer_name                    varchar(20)                                null comment '보험사명',
    meritz_quote_group_number       varchar(50)                                null comment '메리츠 견적그룹번호',
    meritz_quote_request_number     varchar(50)                                null comment '메리츠 견적요청번호',
    country_name                    varchar(50)                                null comment '여행 국가명',
    country_code                    varchar(10)                                null comment '여행 국가 코드',
    insured_people_number           int            default 1                   null comment '피보험자수',
    total_premium                   decimal(15, 2) default 0.00                null comment '총보험료(원)',
    policy_link                     varchar(255)                               null comment '증권확인서 주소',
    status                          varchar(20)    default 'PENDING'           null comment '계약 상태(PENDING, COMPLETED, ERROR)',
    apply_date                      datetime       default current_timestamp() null comment '신청일시',
    insure_start_date               date                                       null comment '보험 시작일시',
    insure_end_date                 date                                       null comment '보험 종료일시',
    contract_people_name            varchar(50)                                null comment '계약자명',
    contract_people_resident_number varchar(20)                                null comment '계약자 주민번호(암호화/마스킹 필요)',
    contract_people_hp              varchar(30)                                null comment '계약자 연락처',
    contract_people_mail            varchar(100)                               null comment '계약자 이메일',
    marketing_consent_used          tinyint(1)     default 0                   null comment '마케팅 동의 활용 여부(0:미활용, 1:활용)',
    auth_provider                   varchar(20)                                null comment '본인인증 제공자(DANAL_PASS 등)',
    auth_imp_uid                    varchar(100)                               null comment '본인인증 imp_uid',
    auth_request_id                 varchar(100)                               null comment '본인인증 request_id',
    auth_unique_key                 varchar(200)                               null comment '본인인증 unique_key',
    auth_status                     varchar(20)    default 'NONE'              null comment '본인인증 상태(NONE, SUCCESS, FAIL)',
    auth_date                       datetime                                   null comment '본인인증 완료 일시',
    employee_id                     bigint                                     null comment '담당자(직원) ID',
    created_at                      datetime       default current_timestamp() not null comment '생성일시',
    updated_at                      datetime       default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at                      datetime                                   null comment '삭제일시',
    constraint fk_contract_channel
        foreign key (channel_id) references db_tpa_dev.tpa_channel (id),
    constraint fk_contract_insurer
        foreign key (insurer_id) references db_tpa_dev.tpa_insurer (id),
    constraint fk_contract_partner
        foreign key (partner_id) references db_tpa_dev.tpa_partner (id),
    constraint fk_contract_plan
        foreign key (plan_id) references db_tpa_dev.travel_insurance_plan (id)
)
    comment '여행자보험 계약(청약/증권 기준)';

create index idx_contract_apply_date
    on db_tpa_dev.travel_contract (apply_date);

create index idx_contract_channel
    on db_tpa_dev.travel_contract (channel_id);

create index idx_contract_employee
    on db_tpa_dev.travel_contract (employee_id);

create index idx_contract_insurer
    on db_tpa_dev.travel_contract (insurer_id);

create index idx_contract_partner
    on db_tpa_dev.travel_contract (partner_id);

create index idx_contract_plan
    on db_tpa_dev.travel_contract (plan_id);

create index idx_contract_policy
    on db_tpa_dev.travel_contract (policy_number);

create index idx_contract_status
    on db_tpa_dev.travel_contract (status);

create table db_tpa_dev.travel_contract_snapshot
(
    id            bigint auto_increment comment '고유 ID'
        primary key,
    contract_id   bigint                               null comment '계약 ID (apply 시점에 매핑)',
    insurer_id    bigint                               not null comment '보험사 ID',
    method        varchar(10)                          not null comment '스냅샷 타입(api 등)',
    snapshot_type varchar(30)                          not null comment '스냅샷 타입(QUOTE/PAYMENT/CANCEL 등)',
    json_snapshot longtext collate utf8mb4_bin         not null comment '보험사별 계약 스냅샷(JSON)',
    created_at    datetime default current_timestamp() not null comment '생성일시',
    constraint uq_contract_snapshot
        unique (contract_id, snapshot_type),
    constraint fk_snapshot_contract
        foreign key (contract_id) references db_tpa_dev.travel_contract (id),
    constraint fk_snapshot_insurer
        foreign key (insurer_id) references db_tpa_dev.tpa_insurer (id)
)
    comment '계약 시점 보험사별 JSON 스냅샷';

create index idx_snapshot_contract
    on db_tpa_dev.travel_contract_snapshot (contract_id);

create index idx_snapshot_insurer
    on db_tpa_dev.travel_contract_snapshot (insurer_id);

create index idx_plan_code
    on db_tpa_dev.travel_insurance_plan (plan_code);

create index idx_plan_insurer
    on db_tpa_dev.travel_insurance_plan (insurer_id);

create index idx_plan_sort
    on db_tpa_dev.travel_insurance_plan (insurer_id, is_active, sort_order, id);

create table db_tpa_dev.travel_insurance_plan_family
(
    id                     bigint auto_increment comment 'PK'
        primary key,
    insurer_id             bigint                                 not null comment '보험사 ID (tpa_insurer.id)',
    insurance_product_name varchar(100)                           not null comment '보험상품명 (ex: 해외여행 실손의료비보험)',
    family_name            varchar(100)                           not null comment '플랜 패밀리명 (ex: 가뿐한플랜)',
    sort_order             int        default 0                   not null comment '정렬 순서',
    is_active              tinyint(1) default 1                   not null comment '사용 여부',
    created_at             datetime   default current_timestamp() not null comment '생성일시',
    updated_at             datetime   default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at             datetime                               null comment '삭제일시',
    is_loss                tinyint(1) default 0                   not null comment '실손여부 (0: 실손제외, 1: 실손포함)',
    constraint uk_plan_family
        unique (insurer_id, insurance_product_name, family_name),
    constraint fk_plan_family_insurer
        foreign key (insurer_id) references db_tpa_dev.tpa_insurer (id)
)
    comment '여행자보험 플랜 패밀리(그룹) 마스터';

create index idx_plan_family_lookup
    on db_tpa_dev.travel_insurance_plan_family (insurer_id, insurance_product_name, is_active, sort_order);

create table db_tpa_dev.travel_insurance_plan_family_map
(
    id         bigint auto_increment comment 'PK'
        primary key,
    family_id  bigint                               not null comment 'plan_family.id',
    plan_id    bigint                               not null comment 'travel_insurance_plan.id',
    created_at datetime default current_timestamp() not null,
    constraint uq_family_plan
        unique (family_id, plan_id),
    constraint fk_map_family
        foreign key (family_id) references db_tpa_dev.travel_insurance_plan_family (id),
    constraint fk_map_plan
        foreign key (plan_id) references db_tpa_dev.travel_insurance_plan (id)
)
    comment '플랜 패밀리-플랜 매핑';

create index idx_map_family
    on db_tpa_dev.travel_insurance_plan_family_map (family_id);

create index idx_map_plan
    on db_tpa_dev.travel_insurance_plan_family_map (plan_id);

create table db_tpa_dev.travel_insure_payment
(
    id             bigint auto_increment comment '고유 ID'
        primary key,
    contract_id    bigint                                     not null comment '계약 ID',
    payment_method varchar(30)                                not null comment '결제방법(CARD/BANK/VBANK 등)',
    payment_date   datetime                                   null comment '결제일시',
    cancel_date    datetime                                   null comment '해지/취소일시',
    paid_amount    decimal(15, 2) default 0.00                not null comment '결제금액(원)',
    status         varchar(20)    default 'READY'             not null comment '상태(READY/PAID/CANCELED)',
    created_at     datetime       default current_timestamp() not null comment '생성일시',
    updated_at     datetime       default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at     datetime                                   null comment '삭제일시',
    constraint uq_payment_contract
        unique (contract_id),
    constraint fk_payment_contract
        foreign key (contract_id) references db_tpa_dev.travel_contract (id)
)
    comment '여행자보험 결제정보(계약당 1건 기준)';

create index idx_payment_date
    on db_tpa_dev.travel_insure_payment (payment_date);

create index idx_payment_status
    on db_tpa_dev.travel_insure_payment (status);

create table db_tpa_dev.travel_insure_people
(
    id                            bigint auto_increment comment '고유 ID'
        primary key,
    contract_id                   bigint                                     not null comment '계약 ID',
    insure_people_resident_number varchar(20)                                null comment '피보험자 주민번호(암호화/마스킹 필요)',
    insure_people_gender          varchar(10)                                null comment '성별(M/F 등)',
    insure_people_name            varchar(50)                                null comment '한글이름',
    insure_people_name_eng        varchar(100)                               null comment '영문이름',
    insure_people_passport_number varchar(30)                                null comment '여권번호',
    policy_number                 varchar(50)                                null comment '증권번호(피보험자 단위로 있으면 저장)',
    insure_premium                decimal(15, 2) default 0.00                not null comment '보험료(피보험자 기준)',
    created_at                    datetime       default current_timestamp() not null comment '생성일시',
    updated_at                    datetime       default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at                    datetime                                   null comment '삭제일시',
    constraint fk_people_contract
        foreign key (contract_id) references db_tpa_dev.travel_contract (id)
)
    comment '여행자보험 피보험자(동반자) 정보';

create index idx_people_contract
    on db_tpa_dev.travel_insure_people (contract_id);

create index idx_people_insure_number
    on db_tpa_dev.travel_insure_people (policy_number);

create index idx_people_passport
    on db_tpa_dev.travel_insure_people (insure_people_passport_number);

create table db_tpa_dev.travel_insure_refund
(
    id             bigint auto_increment comment '환불 ID (PK)'
        primary key,
    payment_id     bigint                               not null comment '결제 ID (FK: travel_insure_payment.id)',
    contract_id    bigint                               not null comment '계약 ID (FK: travel_contract.id)',
    refund_amount  decimal(15, 2)                       not null comment '환불 금액',
    refund_method  varchar(30)                          not null comment '환불 수단 (예: CARD, BANK, VBANK)',
    bank_name      varchar(30)                          null comment '은행명 (계좌 환불 시)',
    account_number varchar(50)                          null comment '계좌번호 (계좌 환불 시)',
    depositor_name varchar(50)                          null comment '예금주명 (계좌 환불 시)',
    refund_reason  varchar(200)                         null comment '환불 사유',
    refunded_at    datetime default current_timestamp() not null comment '환불 처리 완료 시각',
    created_at     datetime default current_timestamp() not null comment '데이터 생성 일시',
    updated_at     datetime default current_timestamp() not null on update current_timestamp() comment '데이터 수정 일시',
    constraint fk_refund_contract
        foreign key (contract_id) references db_tpa_dev.travel_contract (id),
    constraint fk_refund_payment
        foreign key (payment_id) references db_tpa_dev.travel_insure_payment (id)
)
    comment '여행보험 환불 정보 테이블';

create table db_tpa_dev.travel_meritz_cov_map
(
    id          bigint auto_increment
        primary key,
    insurer_id  bigint                               not null,
    cov_cd      varchar(50)                          not null,
    cov_nm      varchar(255)                         not null,
    coverage_id bigint                               null,
    created_at  datetime default current_timestamp() not null,
    updated_at  datetime default current_timestamp() not null on update current_timestamp(),
    constraint uq_meritz_cov
        unique (insurer_id, cov_cd)
);

create table db_tpa_dev.travel_partner_employee
(
    id                 bigint auto_increment comment '고유 ID'
        primary key,
    partner_id         bigint                                 not null comment '제휴사 ID',
    contact_name       varchar(50)                            not null comment '담당자명',
    department_name    varchar(50)                            null comment '부서',
    position_name      varchar(50)                            null comment '직위',
    telephone_number   varchar(30)                            null comment '전화번호',
    mobile_number      varchar(30)                            null comment '휴대폰번호',
    email              varchar(100)                           null comment '이메일',
    is_primary_contact tinyint(1) default 0                   not null comment '대표담당자 여부',
    memo               text                                   null comment '메모',
    created_at         datetime   default current_timestamp() not null comment '생성일시',
    updated_at         datetime   default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at         datetime                               null comment '삭제일시',
    constraint fk_partner_contact_partner
        foreign key (partner_id) references db_tpa_dev.tpa_partner (id)
)
    comment '제휴사 담당자';

create index idx_partner_contact_partner
    on db_tpa_dev.travel_partner_employee (partner_id);

create table db_tpa_dev.travel_plan_coverage
(
    id                     bigint auto_increment comment '고유 ID'
        primary key,
    plan_id                bigint                                 not null comment '플랜 ID',
    coverage_id            bigint                                 not null comment '담보(보장) ID',
    is_included            tinyint(1) default 1                   not null comment '포함여부(0:미포함,1:포함)',
    display_name           varchar(255)                           null comment '플랜별 노출 담보명(없으면 coverage_name 사용)',
    sort_order             int        default 0                   not null comment '정렬순서',
    is_major_coverage      tinyint(1) default 0                   not null comment '주요보장여부',
    title_yn               tinyint(1) default 0                   not null comment '보장금액 타이틀 여부(0:N,1:Y)',
    category_code          varchar(50)                            null comment '담보 UI 카테고리 코드',
    claim_reason_override  varchar(300)                           null comment '지급사유 override',
    claim_content_override text                                   null comment '지급내용 override',
    sub_title_override     varchar(200)                           null comment '추가제목 override',
    sub_content_override   text                                   null comment '추가내용 override',
    created_at             datetime   default current_timestamp() not null comment '생성일시',
    updated_at             datetime   default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at             datetime                               null comment '삭제일시',
    constraint uq_plan_coverage
        unique (plan_id, coverage_id),
    constraint fk_pc_coverage
        foreign key (coverage_id) references db_tpa_dev.travel_insurance_coverage (id),
    constraint fk_pc_plan
        foreign key (plan_id) references db_tpa_dev.travel_insurance_plan (id)
)
    comment '플랜-담보 구성(플랜별 담보 속성/노출/정렬)';

create index idx_pc_category
    on db_tpa_dev.travel_plan_coverage (plan_id, category_code, sort_order);

create index idx_pc_coverage
    on db_tpa_dev.travel_plan_coverage (coverage_id);

create index idx_pc_plan
    on db_tpa_dev.travel_plan_coverage (plan_id);

create table db_tpa_dev.travel_plan_coverage_limit
(
    id                    bigint auto_increment comment '고유 ID'
        primary key,
    plan_coverage_id      bigint                               null comment '플랜담보 ID',
    age_group_id          int                                  null comment '나이구간 ID',
    coverage_status_code  int      default 1                   not null comment '담보 상태',
    coverage_limit_amount bigint   default 0                   not null comment '보장한도금액(원)',
    created_at            datetime default current_timestamp() not null comment '생성일시',
    updated_at            datetime default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at            datetime                             null comment '삭제일시',
    constraint uq_plan_coverage_age
        unique (plan_coverage_id, age_group_id),
    constraint fk_limit_plan_coverage
        foreign key (plan_coverage_id) references db_tpa_dev.travel_plan_coverage (id)
)
    comment '플랜별 담보 보장한도';

create index idx_limit_plan_coverage
    on db_tpa_dev.travel_plan_coverage_limit (plan_coverage_id);

create table db_tpa_dev.travel_plan_premium_rate
(
    id             bigint auto_increment comment '고유 ID'
        primary key,
    plan_id        bigint                                  not null comment '플랜 ID',
    period_id      int                                     not null comment '기간구간 ID',
    age_group_id   int                                     null comment '나이구간 ID',
    currency_code  varchar(10) default 'KRW'               not null comment '통화코드',
    premium_amount bigint      default 0                   not null comment '보험료(원)',
    is_active      tinyint(1)  default 1                   not null comment '판매여부',
    effective_from date                                    null comment '유효시작일',
    effective_to   date                                    null comment '유효종료일',
    created_at     datetime    default current_timestamp() not null comment '생성일시',
    updated_at     datetime    default current_timestamp() not null on update current_timestamp() comment '수정일시',
    deleted_at     datetime                                null comment '삭제일시',
    constraint uq_plan_rate
        unique (plan_id, period_id, age_group_id, currency_code),
    constraint fk_rate_plan
        foreign key (plan_id) references db_tpa_dev.travel_insurance_plan (id)
)
    comment '플랜별 보험료 요율';

create index idx_rate_period
    on db_tpa_dev.travel_plan_premium_rate (period_id);

create index idx_rate_plan
    on db_tpa_dev.travel_plan_premium_rate (plan_id);

