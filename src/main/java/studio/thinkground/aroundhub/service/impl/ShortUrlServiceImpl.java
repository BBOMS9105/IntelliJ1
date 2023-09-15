package studio.thinkground.aroundhub.service.impl;

import java.net.URI;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import studio.thinkground.aroundhub.data.dao.ShortUrlDAO;
import studio.thinkground.aroundhub.data.dto.NaverUriDto;
import studio.thinkground.aroundhub.data.dto.ShortUrlResponseDto;
import studio.thinkground.aroundhub.data.entity.ShortUrl;
import studio.thinkground.aroundhub.service.ShortUrlService;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    // 이 클래스의 로그를 찍기 위한 Logger 객체 생성
    private final Logger LOGGER = LoggerFactory.getLogger(ShortUrlServiceImpl.class);

    // ShortUrlDAO 객체를 주입받기 위한 생성자
    private final ShortUrlDAO shortUrlDAO;

    // 생성자를 통해 ShortUrlDAO 객체를 주입받음
    @Autowired
    public ShortUrlServiceImpl(ShortUrlDAO shortUrlDAO) {
        // 생성자를 통해 주입받은 ShortUrlDAO 객체를 멤버 변수에 할당
        this.shortUrlDAO = shortUrlDAO;
    }

    @Override
    public ShortUrlResponseDto getShortUrl(String clientId, String clientSecret,
        String originalUrl) {

        LOGGER.info("[getShortUrl] request data : {}", originalUrl);
        // ShortUrlDAO 객체를 통해 DB에서 단축 URL을 조회
        ShortUrl getShortUrl = shortUrlDAO.getShortUrl(originalUrl);

        String orgUrl;
        String shortUrl;

        // DB에서 조회한 단축 URL이 없으면 Naver API를 통해 단축 URL을 요청
        if (getShortUrl == null) {
            LOGGER.info("[getShortUrl] No Entity in Database.");
            ResponseEntity<NaverUriDto> responseEntity = requestShortUrl(clientId, clientSecret,
                originalUrl);

            orgUrl = responseEntity.getBody().getResult().getOrgUrl();
            shortUrl = responseEntity.getBody().getResult().getUrl();

        } else {
            // DB에서 조회한 단축 URL이 있으면 DB에서 조회한 단축 URL을 응답
            orgUrl = getShortUrl.getOrgUrl();
            shortUrl = getShortUrl.getUrl();
        }

        ShortUrlResponseDto shortUrlResponseDto = new ShortUrlResponseDto(orgUrl, shortUrl);

        LOGGER.info("[getShortUrl] Response DTO : {}", shortUrlResponseDto.toString());
        return shortUrlResponseDto;
    }

    @Override
    public ShortUrlResponseDto generateShortUrl(String clientId, String clientSecret,
        String originalUrl) {

        LOGGER.info("[generateShortUrl] request data : {}", originalUrl);

        if(originalUrl.contains("me2.do")){
            throw new RuntimeException();
        }

        // Naver API를 통해 단축 URL을 요청하고, 응답을 ResponseEntity 객체로 받아옴
        ResponseEntity<NaverUriDto> responseEntity = requestShortUrl(clientId, clientSecret,
            originalUrl);

        // ResponseEntity 객체에서 응답 바디를 가져옴
        String orgUrl = responseEntity.getBody().getResult().getOrgUrl();
        String shortUrl = responseEntity.getBody().getResult().getUrl();
        String hash = responseEntity.getBody().getResult().getHash();

        // ShortUrlEntity 객체를 생성하여 DB에 저장
        ShortUrl shortUrlEntity = new ShortUrl();
        shortUrlEntity.setOrgUrl(orgUrl);
        shortUrlEntity.setUrl(shortUrl);
        shortUrlEntity.setHash(hash);

        // ShortUrlDAO 객체를 통해 DB에 저장
        shortUrlDAO.saveShortUrl(shortUrlEntity);

        // ShortUrlResponseDTO 객체를 생성하여 응답
        ShortUrlResponseDto shortUrlResponseDto = new ShortUrlResponseDto(orgUrl, shortUrl);
        LOGGER.info("[generateShortUrl] Response DTO : {}", shortUrlResponseDto.toString());
        return shortUrlResponseDto;
    }

    @Override
    public ShortUrlResponseDto updateShortUrl(String clientId, String clientSecret,
        String originalUrl) {
        return null;
    }

    @Override
    public void deleteShortUrl(String url) {
        if(url.contains("me2.do")){
            LOGGER.info("[deleteShortUrl] Request Url is 'ShortUrl'.");
            deleteByShortUrl(url);
        }else{
            LOGGER.info("[deleteShortUrl] Request Url is 'OriginalUrl'.");
            deleteByOriginalUrl(url);
        }
    }

    private void deleteByShortUrl(String url){
        LOGGER.info("[deleteByShortUrl] delete record");
        shortUrlDAO.deleteByShortUrl(url);
    }

    private void deleteByOriginalUrl(String url){
        LOGGER.info("[deleteByOriginalUrl] delete record");
        shortUrlDAO.deleteByOriginalUrl(url);
    }

    private ResponseEntity<NaverUriDto> requestShortUrl(String clientId, String clientSecret,
        String originalUrl) {
        LOGGER.info("[requestShortUrl] client ID : ***, client Secret : ***, original URL : {}", originalUrl);
        // URI 객체를 생성하기 위한 빌더 패턴 시작

        URI uri = UriComponentsBuilder
            .fromUriString("https://openapi.naver.com")
            .path("/v1/util/shorturl")
            .queryParam("url", originalUrl)
            .encode()
            .build()
            .toUri();

        LOGGER.info("[requestShortUrl] set HTTP Request Header");
        // Http 요청을 위한 헤더 객체 생성
        HttpHeaders headers = new HttpHeaders();
        // APPLICATION_JSON 타입의 응답을 받아들이기 위한 설정
        headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
        // APPLICATION_JSON 타입의 요청을 위한 설정
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        // Http 요청을 위한 엔티티 객체 생성. 바디는 빈 문자열, 헤더는 위에서 생성한 헤더 객체
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        // RestTemplate 객체를 생성하여 Http 요청을 수행하기 위한 준비
        RestTemplate restTemplate = new RestTemplate();

        LOGGER.info("[requestShortUrl] request by restTemplate");
        // Http 요청을 수행하고, 응답을 ResponseEntity 객체로 받아옴
        ResponseEntity<NaverUriDto> responseEntity = restTemplate.exchange(uri, HttpMethod.GET,
            entity, NaverUriDto.class);

        LOGGER.info("[requestShortUrl] request has been successfully complete.");

        return responseEntity;
    }

}
