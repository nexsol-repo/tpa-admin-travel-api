package com.nexsol.tpa.storage.db.core;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreationTimestamp
	@Column(updatable = false) // (선택사항) 생성일은 수정 불가가 명확하므로 이것만 붙여주는 경우가 많음
	private LocalDateTime createdAt;

	// @Column 완전 생략: 자동으로 updated_at 매핑됨
	@UpdateTimestamp
	private LocalDateTime updatedAt;

}
