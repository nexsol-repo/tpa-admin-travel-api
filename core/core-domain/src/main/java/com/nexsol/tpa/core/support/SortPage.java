package com.nexsol.tpa.core.support;

// int -> Integer로 변경하여 null 입력을 허용
public record SortPage(Integer page, Integer size, Sort sort) {

    // ... Sort, Direction 내부 클래스/Enum 코드는 그대로 유지 ...
    public record Sort(String property, Direction direction) {
        public static Sort of(String property, Direction direction) {
            return new Sort(property, direction);
        }
    }

    public enum Direction {

        ASC, DESC;

        public boolean isAscending() {
            return this == ASC;
        }

    }

    // Compact Constructor 수정
    public SortPage {
        // page가 null이거나 0보다 작으면 0으로 초기화
        if (page == null || page < 0) {
            page = 0;
        }
        // size가 null이거나 1보다 작으면 10으로 초기화
        if (size == null || size < 1) {
            size = 10;
        }
    }

    public long offset() {
        return (long) page * size; // Integer 자동 언박싱
    }

    // 정렬 없이 페이징만 요청할 때
    public static SortPage of(int page, int size) {
        return new SortPage(page, size, null);
    }

    // 정렬 포함 요청
    public static SortPage of(int page, int size, String property, Direction direction) {
        return new SortPage(page, size, new Sort(property, direction));
    }
}