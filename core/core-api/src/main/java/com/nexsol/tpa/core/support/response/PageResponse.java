package com.nexsol.tpa.core.support.response;

import com.nexsol.tpa.core.support.PageResult;

import java.util.List;

public record PageResponse<T>(List<T> content, long totalElements, int totalPages, int currentPage, boolean hasNext) {
	// PageResult<T> -> PageResponse<T> 변환 팩토리 메서드
	public static <T> PageResponse<T> of(PageResult<T> result) {
		return new PageResponse<>(result.getContent(), result.getTotalElements(), result.getTotalPages(),
				result.getCurrentPage(), result.hasNext());
	}

	// DTO 변환이 필요한 경우 (예: PageResult<Entity> -> PageResponse<Dto>)
	// 변환된 리스트(content)를 받아서 나머지는 result의 메타데이터를 사용
	public static <T, R> PageResponse<R> of(PageResult<T> result, List<R> convertedContent) {
		return new PageResponse<>(convertedContent, result.getTotalElements(), result.getTotalPages(),
				result.getCurrentPage(), result.hasNext());
	}
}