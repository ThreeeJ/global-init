package com.hansung.likelion.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    // 클래스 -> 추상 클래스로 변경
    /*
    - 변경 이유
        - @MappedSuperclass 때문에 테이블로 매핑되지 않고, 자식 엔티티에게 공통 필드를 상속해주는 역할.
        - 객체로 만들어질 일이 없음. -> abstract 를 붙여서 new 로 객체가 만들어지는 것을 "미연에 방지"
    */

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
