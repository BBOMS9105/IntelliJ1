package studio.thinkground.aroundhub.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studio.thinkground.aroundhub.data.entity.ShortUrl;

// JpaRepository<Entity 클래스, PK 타입>
// JpaRepository에는 일반적으로 많이 사용하는 데이터 조작을 다루는 메소드가 정의되어 있다.
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    // JpaRepository를 상속받은 인터페이스는 기본적인 CRUD 메소드를 자동으로 생성해준다.
//    void findByOrgUrl
    ShortUrl findByUrl(String url);

    ShortUrl findByOrgUrl(String originalUrl);

}
