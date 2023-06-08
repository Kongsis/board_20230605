package com.example.board.entity;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {
    // 작성시간과 수정시간을 가지고 있는 엔티티
    // 별도의 테이블X, 상속을 주기위한 목적을 가지고있음
    // 다른 엔티티에서 상속을 받아서 사용할것

    @CreationTimestamp //insert전용
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp //업데이트 전용
    @Column(insertable = false)
    private LocalDateTime updatedAt;
}
