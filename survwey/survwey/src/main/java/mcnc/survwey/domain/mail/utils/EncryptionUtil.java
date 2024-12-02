package mcnc.survwey.domain.mail.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Slf4j
@Component
public class EncryptionUtil {

    @Value("${ENCRYPTION_SECRET_KEY}")
    private String secretKey;

    /**
     * 암/복호화 모드 설정
     * @param urlParameter
     * @param mode
     * @return
     */
    public byte[] determinedCipherMode(String urlParameter, int mode){
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");//키 세팅 -> AES 기준으로 secretKey 부분에 들어오는 길이에 따라 암호화 방식 결정
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//AES 알고리즘으로 CBC 모드, PKCS5Padding scheme 으로 초기화
            IvParameterSpec ivParameterSpec = new IvParameterSpec(secretKey.substring(0, 16).getBytes());//키를 16 바이트로 잘라 초기화 벡터 byte 로 변경
            cipher.init(mode, keySpec, ivParameterSpec);//암호 모드 결정

            if(mode == Cipher.ENCRYPT_MODE){//암호화(mode == 1)
                return cipher.doFinal(urlParameter.getBytes());
            }else if(mode == Cipher.DECRYPT_MODE){//복호화(mode == 2)
                return cipher.doFinal(Base64.getDecoder().decode(urlParameter));
            }else{
                //mode == 3 -> 키 자체를 암호화(래핑)
                //mode == 4 -> 래핑된 키를 복호화
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 암호 모드는 잘못된 방식입니다.");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 링크는 잘못된 링크입니다.", e);
        }

    }

    /**
     * AES/CBC 비밀키로 URL 파라미터 암호화
     * 16바이트 키 값은 환경변수로 저장됨
     * @param surveyId
     * @return
     */
    public String encrypt(String surveyId) {
        try{
            return Base64.getUrlEncoder().encodeToString(determinedCipherMode(surveyId, Cipher.ENCRYPT_MODE));
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 링크는 잘못된 링크입니다.", e);
        }

    }
}
