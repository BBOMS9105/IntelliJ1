package studio.thinkground.aroundhub.data.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
/*
 * @MappedSuperclass : 공통 매핑 정보가 필요할 때 사용한다.
 * id, 생성일자, 수정일자 등
 * */
@MappedSuperclass
/* Entity가 생성, 변경될 때 자동으로 값을 넣어주는 Auditing 기능을 사용하기 위해 추가
 Auditing은 각 엔티티 별로 누가, 언제 접근했는지에 대한 정보를 기록하는 기능이다. application.yml에서 @EnableJpaAuditing을 활성화시켜야 한다.
 @EntityListeners : JPA Entity에 이벤트가 발생할 때마다 콜백을 요청하는 어노테이션. 파라미터로 콜백을 요청할 클래스를 지정한다.*/
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

  /*
  @CreatedBy
  @Column(updatable = false)
  private String createdBy;
  */

    @LastModifiedDate
    private LocalDateTime updatedAt;

  /*
  @LastModifiedBy
  private String updatedBy;
  */

}


