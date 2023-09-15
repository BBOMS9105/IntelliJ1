package studio.thinkground.aroundhub.data.repository;

import org.springframework.data.repository.CrudRepository;
import studio.thinkground.aroundhub.data.dto.ShortUrlResponseDto;

// Redis에 저장된 데이터를 조회하기 위한 Repository
public interface ShortUrlRedisRepository extends CrudRepository<ShortUrlResponseDto, String> {
}
